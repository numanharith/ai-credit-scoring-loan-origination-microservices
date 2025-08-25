package com.numan.loanapi.repository;

import com.numan.loanapi.dto.LoanApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    // Oracle hint for performance
    @Query(value = "SELECT /*+ FIRST_ROWS(10) */ * FROM applications " +
           "WHERE status = ?1 ORDER BY created_date DESC",
           nativeQuery = true)
    List<LoanApplication> findRecentByStatus(String status, Pageable pageable);

    // Bulk operations using Oracle MERGE
    @Modifying
    @Query(value = "MERGE INTO applications la " +
           "USING (SELECT :appId as id, :status as new_status FROM dual) src " +
           "ON (la.id = src.id) " +
           "WHEN MATCHED THEN UPDATE SET status = src.new_status, " +
           "last_modified = SYSTIMESTAMP, " +
           "modified_by = USER",
           nativeQuery = true)
    void updateApplicationStatus(@Param("appId") Long appId, @Param("status") String status);

    // Find by reference number
    LoanApplication findByReferenceNumber(String referenceNumber);
}
