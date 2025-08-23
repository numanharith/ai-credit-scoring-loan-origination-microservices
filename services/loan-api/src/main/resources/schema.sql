-- services/loan-api/src/main/resources/db/migration/V1__Create_Loan_Intake_Schema.sql
-- This runs as LOAN_INTAKE user

CREATE SEQUENCE application_seq START WITH 100000 INCREMENT BY 1;
CREATE SEQUENCE audit_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE applications (
    id NUMBER(19) PRIMARY KEY,
    reference_number VARCHAR2(20) UNIQUE NOT NULL,
    nric VARCHAR2(20) NOT NULL,
    full_name VARCHAR2(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    nationality VARCHAR2(50) DEFAULT 'SG',
    mobile_number VARCHAR2(20),
    email VARCHAR2(100),
    monthly_income NUMBER(15,2) NOT NULL,
    employment_type VARCHAR2(20) NOT NULL,
    employer_name VARCHAR2(100),
    years_employed NUMBER(3),
    loan_amount NUMBER(15,2) NOT NULL,
    loan_tenure NUMBER(3) NOT NULL,
    purpose VARCHAR2(30),
    property_type VARCHAR2(30),
    existing_debt_amount NUMBER(15,2) DEFAULT 0,
    status VARCHAR2(20) DEFAULT 'PENDING',

    -- Audit columns
    created_date TIMESTAMP(6) DEFAULT SYSTIMESTAMP,
    created_by VARCHAR2(100) DEFAULT USER,
    last_modified TIMESTAMP(6) DEFAULT SYSTIMESTAMP,
    modified_by VARCHAR2(100),
    version_number NUMBER(10) DEFAULT 1,

    -- Constraints
    CONSTRAINT chk_loan_amount CHECK (loan_amount BETWEEN 5000 AND 200000),
    CONSTRAINT chk_monthly_income CHECK (monthly_income >= 2000),
    CONSTRAINT chk_nric_format CHECK (REGEXP_LIKE(nric, '^[ST][0-9]{7}[A-Z]$')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'SCORING', 'APPROVED', 'REJECTED', 'MANUAL_REVIEW', 'DISBURSED'))
);
COMMENT ON TABLE applications IS 'Stores all loan application data submitted by customers.';

-- Indexes for performance
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_created_date ON applications(created_date);
CREATE INDEX idx_applications_reference ON applications(reference_number);

-- Audit table
CREATE TABLE application_audit (
    audit_id NUMBER(19) PRIMARY KEY,
    application_id NUMBER(19) NOT NULL,
    field_name VARCHAR2(50),
    old_value VARCHAR2(4000),
    new_value VARCHAR2(4000),
    changed_by VARCHAR2(100),
    changed_date TIMESTAMP(6) DEFAULT SYSTIMESTAMP
);
COMMENT ON TABLE application_audit IS 'Tracks all changes to application records.';

-- Audit trigger (procedure)
CREATE OR REPLACE TRIGGER trg_application_audit
    AFTER UPDATE ON applications
    FOR EACH ROW
BEGIN
    IF :OLD.status != :NEW.status THEN
        INSERT INTO application_audit (
            audit_id, application_id, field_name, old_value, new_value, changed_by
        ) VALUES (
            audit_seq.NEXTVAL, :NEW.application_id, 'STATUS', :OLD.status, :NEW.status, USER
        );
    END IF;

    :NEW.last_modified := SYSTIMESTAMP;
    :NEW.modified_by := USER;
    :NEW.version_number := :OLD.version_number + 1;
END;
/