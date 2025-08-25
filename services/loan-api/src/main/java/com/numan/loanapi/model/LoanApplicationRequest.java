package com.numan.loanapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request model for loan application submission
 * Contains all applicant information required for credit assessment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotBlank(message = "NRIC is mandatory")
    @Pattern(regexp = "^[STFGstfg]\\d{7}[A-Za-z]$",
            message = "Invalid Singapore NRIC/FIN format")
    private String nric;

    @NotBlank(message = "Full name is mandatory")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Date of birth is mandatory")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Nationality is mandatory")
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @NotNull(message = "Monthly income is mandatory")
    @DecimalMin(value = "2000.00", message = "Minimum monthly income is S$2,000")
    @DecimalMax(value = "500000.00", message = "Maximum monthly income is S$500,000")
    @Digits(integer = 8, fraction = 2, message = "Invalid income format")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Employment type is mandatory")
    private EmploymentType employmentType;

    @NotBlank(message = "Employer name is mandatory")
    @Size(min = 2, max = 100, message = "Employer name must be between 2 and 100 characters")
    private String employerName;

    @NotNull(message = "Years of employment is mandatory")
    @Min(value = 0, message = "Years employed cannot be negative")
    @Max(value = 45, message = "Years employed cannot exceed 45")
    private Integer yearsEmployed;

    @NotNull(message = "Loan amount is mandatory")
    @DecimalMin(value = "5000.00", message = "Minimum loan amount is S$5,000")
    @DecimalMax(value = "200000.00", message = "Maximum loan amount is S$200,000")
    @Digits(integer = 8, fraction = 2, message = "Invalid loan amount format")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan tenure is mandatory")
    @Min(value = 12, message = "Minimum loan tenure is 12 months")
    @Max(value = 84, message = "Maximum loan tenure is 84 months")
    private Integer loanTenure;

    @NotNull(message = "Loan purpose is mandatory")
    private LoanPurpose purpose;

    private PropertyType propertyType;

    private List<ExistingLoan> existingLoans;

    // Contact information (optional but recommended)
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[689]\\d{7}$", message = "Invalid Singapore mobile number")
    private String mobileNumber;

    // Address information
    private String address;
    private String postalCode;

    /**
     * Employment Type Enumeration
     */
    public enum EmploymentType {
        PERMANENT("Permanent Employee"),
        CONTRACT("Contract Employee"),
        SELF_EMPLOYED("Self Employed");

        private final String description;

        EmploymentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Loan Purpose Enumeration
     */
    public enum LoanPurpose {
        RENOVATION("Home Renovation"),
        MEDICAL("Medical Expenses"),
        EDUCATION("Education"),
        WEDDING("Wedding"),
        TRAVEL("Travel"),
        DEBT_CONSOLIDATION("Debt Consolidation"),
        BUSINESS("Business Investment"),
        OTHERS("Others");

        private final String description;

        LoanPurpose(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Property Type Enumeration (Singapore-specific)
     */
    public enum PropertyType {
        HDB_3ROOM("HDB 3-Room"),
        HDB_4ROOM("HDB 4-Room"),
        HDB_5ROOM("HDB 5-Room"),
        HDB_EXECUTIVE("HDB Executive"),
        CONDO("Condominium"),
        LANDED("Landed Property"),
        NONE("No Property");

        private final String description;

        PropertyType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Existing Loan Details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExistingLoan {
        private String loanType;
        private String bankName;
        private BigDecimal outstandingAmount;
        private BigDecimal monthlyInstallment;
        private Integer remainingTenure;
    }

    /**
     * Calculate age from date of birth
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Calculate total monthly obligations from existing loans
     */
    public BigDecimal getTotalMonthlyObligations() {
        if (existingLoans == null || existingLoans.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return existingLoans.stream()
                .map(ExistingLoan::getMonthlyInstallment)
                .filter(installment -> installment != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate Debt Service Ratio (DSR)
     */
    public BigDecimal calculateDSR() {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalObligations = getTotalMonthlyObligations();
        return totalObligations.divide(monthlyIncome, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}