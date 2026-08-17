package com.example.demo.controller;

import com.example.demo.dtos.TransferRequestDTO;
import com.example.demo.dtos.TransferResponseDTO;
import com.example.demo.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {

    @Autowired
    TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@RequestBody TransferRequestDTO requestDTO) throws Exception{
        return transferService.createTransfer(requestDTO);
    }
}
