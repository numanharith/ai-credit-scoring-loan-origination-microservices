// LoanApplicationController.java
package com.numan.loanapi.controller;

import com.numan.loanapi.model.LoanApplicationRequest;
import com.numan.loanapi.model.LoanApplicationResponse;
import com.numan.loanapi.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationController {

    private final LoanApplicationService service;

    @PostMapping("/apply")
    public ResponseEntity<LoanApplicationResponse> submitApplication(
            @Valid @RequestBody LoanApplicationRequest request) {
        
        log.info("Received loan application for NRIC: {}", 
                 maskNric(request.getNric()));

        // TODO: Publish to Kafka

        return ResponseEntity.ok(
                service.submitApplication(request)
        );
    }
    
    private String generateReferenceNumber() {
        return String.format("LA-%s-%05d", 
            LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE),
            ThreadLocalRandom.current().nextInt(10000, 99999));
    }
    
    private String maskNric(String nric) {
        if (nric == null || nric.length() < 5) return "****";
        return nric.substring(0, 1) + "****" + nric.substring(nric.length() - 4);
    }
}