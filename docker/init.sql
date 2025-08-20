-- docker/init.sql
CREATE SCHEMA IF NOT EXISTS loan_system;

CREATE TABLE loan_system.applications
(
    id               NUMBER(19) PRIMARY KEY,
    reference_number VARCHAR(20) UNIQUE NOT NULL,
    nric             VARCHAR(20)        NOT NULL,
    full_name        VARCHAR(100)       NOT NULL,
    date_of_birth    DATE               NOT NULL,
    nationality      VARCHAR(50),
    monthly_income   DECIMAL(10, 2)     NOT NULL,
    employment_type  VARCHAR(20)        NOT NULL,
    employer_name    VARCHAR(100),
    years_employed   INTEGER,
    loan_amount      DECIMAL(10, 2)     NOT NULL,
    loan_tenure      INTEGER            NOT NULL,
    purpose          VARCHAR(30),
    property_type    VARCHAR(30),
    status           VARCHAR(20) DEFAULT 'PENDING',
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loan_system.credit_scores
(
    id             BIGSERIAL PRIMARY KEY,
    application_id BIGINT REFERENCES loan_system.applications (id),
    score          INTEGER NOT NULL,
    risk_category  VARCHAR(20),
    factors        JSONB,
    ai_explanation TEXT,
    scored_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loan_system.loan_accounts
(
    id                  BIGSERIAL PRIMARY KEY,
    application_id      BIGINT REFERENCES loan_system.applications (id),
    account_number      VARCHAR(30) UNIQUE NOT NULL,
    principal_amount    DECIMAL(10, 2),
    interest_rate       DECIMAL(5, 2),
    monthly_installment DECIMAL(10, 2),
    first_payment_date  DATE,
    maturity_date       DATE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_applications_reference ON loan_system.applications (reference_number);
CREATE INDEX idx_applications_status ON loan_system.applications (status);
CREATE INDEX idx_applications_nric ON loan_system.applications (nric);