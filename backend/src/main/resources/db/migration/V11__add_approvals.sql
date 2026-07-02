-- V11__add_approvals.sql

CREATE SEQUENCE approvals_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE approvals (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    approval_type VARCHAR(50) NOT NULL,
    approver_role VARCHAR(50) NOT NULL,
    approver_user VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    approved_at TIMESTAMP WITH TIME ZONE,
    rejected_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_approvals_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id) ON DELETE CASCADE
);

CREATE INDEX idx_approvals_request_id ON approvals(request_id);
