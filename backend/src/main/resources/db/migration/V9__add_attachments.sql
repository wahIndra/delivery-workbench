-- V9__add_attachments.sql

CREATE SEQUENCE request_attachments_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE request_attachments (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT NOT NULL,
    storage_path VARCHAR(1024) NOT NULL,
    uploaded_by VARCHAR(100) NOT NULL,
    attachment_category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_request_attachments_request FOREIGN KEY (request_id) REFERENCES delivery_requests (id) ON DELETE CASCADE
);

CREATE INDEX idx_ra_request_id ON request_attachments(request_id);
