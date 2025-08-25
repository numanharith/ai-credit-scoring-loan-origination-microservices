package com.numan.loanapi.dto;

import com.numan.loanapi.model.LoanApplicationResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications")
@SequenceGenerator(name = "app_seq", sequenceName = "application_seq", allocationSize = 1)
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "reference_number", unique = true, nullable = false, length = 20)
    private String referenceNumber;

    @Column(name = "nric", nullable = false, length = 20)
    private String nric;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "employment_type", nullable = false, length = 20)
    private String employmentType;

    @Column(name = "employer_name", length = 100)
    private String employerName;

    @Column(name = "years_employed")
    private Integer yearsEmployed;

    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "loan_tenure", nullable = false)
    private Integer loanTenure;

    @Column(name = "purpose", length = 30)
    private String purpose;

    @Column(name = "property_type", length = 30)
    private String propertyType;

    @Column(name = "existing_debt_amount", precision = 15, scale = 2)
    private BigDecimal existingDebtAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private LoanApplicationResponse.ApplicationStatus status;

    // Audit columns
    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "last_modified")
    @UpdateTimestamp
    private LocalDateTime lastModified;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Version
    @Column(name = "version_number")
    private Long versionNumber;

    public enum Status {
        PENDING,
        SCORING,
        APPROVED,
        REJECTED,
        MANUAL_REVIEW,
        DISBURSED
    }
}
