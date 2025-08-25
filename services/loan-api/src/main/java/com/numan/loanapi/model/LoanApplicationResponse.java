package com.numan.loanapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response model for loan application operations
 * Returns application status and relevant information to the client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanApplicationResponse {

    private String referenceNumber;

    private ApplicationStatus status;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Included for status inquiry responses
    private ApplicantInfo applicant;

    // Included for approved applications
    private LoanDetails loanDetails;

    // Included when scoring is complete
    private CreditScoreInfo creditScore;

    // Next steps for the applicant
    private List<String> nextSteps;

    // Any validation errors
    private List<String> errors;

    // Processing information
    private ProcessingInfo processing;

    /**
     * Application Status Enumeration
     */
    public enum ApplicationStatus {
        PENDING("Application received and being processed"),
        SCORING("Credit assessment in progress"),
        MANUAL_REVIEW("Application requires manual review"),
        APPROVED("Loan approved"),
        REJECTED("Loan application rejected"),
        DISBURSED("Loan amount disbursed"),
        CANCELLED("Application cancelled by applicant");

        private final String description;

        ApplicationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Applicant Information (masked for privacy)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicantInfo {
        private String maskedNric;
        private String fullName;
        private BigDecimal loanAmount;
        private Integer loanTenure;
        private String purpose;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime submittedAt;
    }

    /**
     * Loan Details for approved applications
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDetails {
        private String loanAccountNumber;
        private BigDecimal approvedAmount;
        private BigDecimal interestRate;
        private Integer tenure;
        private BigDecimal monthlyInstallment;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime firstPaymentDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime maturityDate;

        private String loanAgreementUrl;
        private String repaymentScheduleUrl;
    }

    /**
     * Credit Score Information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditScoreInfo {
        private Integer score;
        private String riskCategory;
        private List<String> keyFactors;
        private String recommendation;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scoredAt;
    }

    /**
     * Processing Information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingInfo {
        private String currentStage;
        private Integer estimatedCompletionMinutes;
        private String trackingId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUpdated;

        private List<ProcessingStep> steps;
    }

    /**
     * Individual Processing Step
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingStep {
        private String stepName;
        private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        private String notes;
    }

    /**
     * Factory method for successful submission
     */
    public static LoanApplicationResponse success(String referenceNumber, String message) {
        return LoanApplicationResponse.builder()
                .referenceNumber(referenceNumber)
                .status(ApplicationStatus.PENDING)
                .message(message)
                .timestamp(LocalDateTime.now())
                .nextSteps(List.of(
                        "Your application is being processed",
                        "You will receive an SMS/email update within 5 minutes",
                        "Check status using reference number: " + referenceNumber
                ))
                .build();
    }

    /**
     * Factory method for validation errors
     */
    public static LoanApplicationResponse validationError(List<String> errors) {
        return LoanApplicationResponse.builder()
                .status(ApplicationStatus.PENDING) // Keep as pending since no application created
                .message("Application validation failed")
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .nextSteps(List.of(
                        "Please correct the validation errors and resubmit",
                        "Contact customer service if you need assistance"
                ))
                .build();
    }

    /**
     * Factory method for approved applications
     */
    public static LoanApplicationResponse approved(String referenceNumber, LoanDetails loanDetails, CreditScoreInfo creditScore) {
        return LoanApplicationResponse.builder()
                .referenceNumber(referenceNumber)
                .status(ApplicationStatus.APPROVED)
                .message("Congratulations! Your loan has been approved")
                .timestamp(LocalDateTime.now())
                .loanDetails(loanDetails)
                .creditScore(creditScore)
                .nextSteps(List.of(
                        "Review and sign the loan agreement",
                        "Submit required documents",
                        "Loan disbursement will be processed within 1 business day"
                ))
                .build();
    }

    /**
     * Factory method for rejected applications
     */
    public static LoanApplicationResponse rejected(String referenceNumber, CreditScoreInfo creditScore, List<String> reasons) {
        return LoanApplicationResponse.builder()
                .referenceNumber(referenceNumber)
                .status(ApplicationStatus.REJECTED)
                .message("We regret to inform you that your loan application has been declined")
                .timestamp(LocalDateTime.now())
                .creditScore(creditScore)
                .errors(reasons)
                .nextSteps(List.of(
                        "You may reapply after 30 days",
                        "Consider improving your credit profile",
                        "Contact us for financial advisory services"
                ))
                .build();
    }

    /**
     * Factory method for manual review
     */
    public static LoanApplicationResponse manualReview(String referenceNumber, CreditScoreInfo creditScore) {
        return LoanApplicationResponse.builder()
                .referenceNumber(referenceNumber)
                .status(ApplicationStatus.MANUAL_REVIEW)
                .message("Your application requires additional review")
                .timestamp(LocalDateTime.now())
                .creditScore(creditScore)
                .nextSteps(List.of(
                        "A credit analyst will review your application",
                        "You may be contacted for additional information",
                        "Decision will be communicated within 2 business days"
                ))
                .processing(ProcessingInfo.builder()
                        .currentStage("Manual Review Queue")
                        .estimatedCompletionMinutes(2880) // 2 days
                        .lastUpdated(LocalDateTime.now())
                        .build())
                .build();
    }

    /**
     * Add processing step information
     */
    public void addProcessingStep(String stepName, String status, String notes) {
        if (this.processing == null) {
            this.processing = ProcessingInfo.builder()
                    .steps(new java.util.ArrayList<>())
                    .build();
        }

        if (this.processing.getSteps() == null) {
            this.processing.setSteps(new java.util.ArrayList<>());
        }

        ProcessingStep step = ProcessingStep.builder()
                .stepName(stepName)
                .status(status)
                .startTime(LocalDateTime.now())
                .notes(notes)
                .build();

        this.processing.getSteps().add(step);
    }

    /**
     * Update current processing stage
     */
    public void updateProcessingStage(String stage, Integer estimatedMinutes) {
        if (this.processing == null) {
            this.processing = ProcessingInfo.builder().build();
        }

        this.processing.setCurrentStage(stage);
        this.processing.setEstimatedCompletionMinutes(estimatedMinutes);
        this.processing.setLastUpdated(LocalDateTime.now());
    }
}