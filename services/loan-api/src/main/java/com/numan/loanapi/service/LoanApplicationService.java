package com.numan.loanapi.service;

import com.numan.loanapi.dto.LoanApplication;
import com.numan.loanapi.model.LoanApplicationRequest;
import com.numan.loanapi.model.LoanApplicationResponse;
import com.numan.loanapi.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoanApplicationService {
    
    private final LoanApplicationRepository repository;

    public LoanApplicationResponse submitApplication(LoanApplicationRequest request) {
        // Validate
//        validateApplication(request);

        // Create entity
        LoanApplication application = LoanApplication.builder()
                .referenceNumber(generateReferenceNumber())
                .nric(request.getNric())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .monthlyIncome(request.getMonthlyIncome())
                .employmentType(String.valueOf(request.getEmploymentType()))
                .loanAmount(request.getLoanAmount())
                .loanTenure(request.getLoanTenure())
                .status(LoanApplicationResponse.ApplicationStatus.PENDING)
                .build();

        // Save to database
        application = repository.save(application);

        // Publish to Kafka for scoring
//        LoanApplicationEvent event = LoanApplicationEvent.builder()
//                .applicationId(application.getId())
//                .referenceNumber(application.getReferenceNumber())
//                .timestamp(Instant.now())
//                .build();

//        kafkaTemplate.send("loan-applications", event);

        log.info("LoanApplication {} submitted and sent for scoring",
                application.getReferenceNumber());

        return LoanApplicationResponse.builder()
                .referenceNumber(application.getReferenceNumber())
                .status(application.getStatus())
                .message("Loan application submitted successfully")
                .build();
    }

    private String generateReferenceNumber() {
        return String.format("LA-%s-%05d",
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                ThreadLocalRandom.current().nextInt(10000, 99999));
    }

}
