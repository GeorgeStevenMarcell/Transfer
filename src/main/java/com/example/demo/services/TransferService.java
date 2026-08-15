package com.example.demo.services;

import com.example.demo.Repository.MerchantRepository;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.dtos.TransferRequestDTO;
import com.example.demo.dtos.TransferResponseDTO;
import com.example.demo.entities.MerchantEntity;
import com.example.demo.entities.TransactionEntity;
import com.example.demo.enums.TransactionStatusEnum;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    TransactionRepository transactionRepository;

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
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new TransferResponseDTO());
            }

            MerchantEntity merchant = merchantEntityOptional.get();


            if(merchant.getBalance().compareTo(requestDTO.getAmount()) < 0)
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new TransferResponseDTO());

            merchant.setBalance(merchant.getBalance().subtract(requestDTO.getAmount()));

            merchantRepository.save(merchant);

            TransactionEntity transaction = new TransactionEntity();

            transaction.setTransactionId(transactionId);
            transaction.setAmount(requestDTO.getAmount());
            transaction.setMerchantId(requestDTO.getMerchantId());
            transaction.setDestinationAcct(merchant.getAccountNumber());
            transaction.setStatus(TransactionStatusEnum.PENDING);

            transactionRepository.save(transaction);





        }catch (Exception e){
            System.out.println(e.toString());
            throw e;
        } finally {

        }


        return ResponseEntity
                .status(202)
                .body(responseDTO);
    }


}
