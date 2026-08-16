package com.example.demo.services;

import com.example.demo.events.TransferRequested;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.dtos.TransferRequestDTO;
import com.example.demo.dtos.TransferResponseDTO;
import com.example.demo.entities.MerchantEntity;
import com.example.demo.entities.TransactionEntity;
import com.example.demo.enums.TransactionStatusEnum;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    private final Long HARD_CODED_MERCHANT_ID = 1L;

    @Transactional
    public ResponseEntity<TransferResponseDTO> createTransfer(TransferRequestDTO requestDTO) throws Exception{

        if(requestDTO.getAmount().compareTo(new BigDecimal(0)) <= 0)
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                    .body(new TransferResponseDTO());

        if(!requestDTO.getMerchantId().equals(HARD_CODED_MERCHANT_ID))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new TransferResponseDTO());


        TransferResponseDTO responseDTO = new TransferResponseDTO();

        UUID transactionId = UUID.randomUUID();

        responseDTO.setTransactionId(transactionId);

        try{

            Optional<MerchantEntity> merchantEntityOptional = merchantRepository.findByIdForUpdate(requestDTO.getMerchantId());
            if(merchantEntityOptional.isEmpty()){
                if(!requestDTO.getMerchantId().equals(HARD_CODED_MERCHANT_ID))
                    throw new Exception("Invalid Merchant");
            }

            MerchantEntity merchant = merchantEntityOptional.get();


            if(merchant.getBalance().compareTo(requestDTO.getAmount()) < 0)
                throw new Exception("Balance Not Enough");

            merchant.setBalance(merchant.getBalance().subtract(requestDTO.getAmount()));

            merchantRepository.save(merchant);

            TransactionEntity transaction = new TransactionEntity();

            transaction.setTransactionId(transactionId);
            transaction.setAmount(requestDTO.getAmount());
            transaction.setMerchantId(requestDTO.getMerchantId());
            transaction.setDestinationAcct(requestDTO.getDestinationAcct());
            transaction.setStatus(TransactionStatusEnum.PENDING);

            transactionRepository.save(transaction);

            applicationEventPublisher.publishEvent( new TransferRequested(transactionId,
                    requestDTO.getMerchantId(),
                    requestDTO.getAmount(),
                    requestDTO.getDestinationAcct()));

        }catch (Exception e){
            System.out.println(e.toString());
            throw e;
        } finally {
            responseDTO.setStatus(TransactionStatusEnum.PENDING);
        }


        return ResponseEntity
                .status(202)
                .body(responseDTO);
    }

    @Transactional
    public boolean isPending(UUID id){
        return transactionRepository
                .findById(id)
                .map(data -> data.getStatus() == TransactionStatusEnum.PENDING)
                .orElse(false);
    }

    @Transactional
    public void markSuccess(UUID id) throws Exception{
        TransactionEntity transactionEntity = transactionRepository.findById(id).orElseThrow();
        if(!transactionEntity.getStatus().equals(TransactionStatusEnum.PENDING)) return;
        transactionEntity.setStatus(TransactionStatusEnum.SUCCESS);
        transactionRepository.saveAndFlush(transactionEntity);
    }

    @Transactional
    public void markFailedAndRefund(UUID id, String reason) throws Exception{
        TransactionEntity transactionEntity = transactionRepository.findById(id).orElseThrow();
        if(!transactionEntity.getStatus().equals(TransactionStatusEnum.PENDING)) return;

        MerchantEntity merchantEntity = merchantRepository.findByIdForUpdate(transactionEntity.getMerchantId()).orElseThrow();
        merchantEntity.setBalance(merchantEntity.getBalance().add(transactionEntity.getAmount()));

        transactionEntity.setStatus(TransactionStatusEnum.FAILED);
        transactionEntity.setFailureReason(reason);

        merchantRepository.saveAndFlush(merchantEntity);
        transactionRepository.saveAndFlush(transactionEntity);
    }


}
