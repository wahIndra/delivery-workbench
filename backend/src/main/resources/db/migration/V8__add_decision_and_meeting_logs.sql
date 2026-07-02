-- V8__add_decision_and_meeting_logs.sql

CREATE SEQUENCE decision_logs_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE meeting_notes_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE decision_logs (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    decision_title VARCHAR(255) NOT NULL,
    decision_description TEXT NOT NULL,
    decision_type VARCHAR(30) NOT NULL,
    decided_by VARCHAR(100) NOT NULL,
    decision_date DATE NOT NULL,
    impact TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_decision_logs_request FOREIGN KEY (request_id) REFERENCES delivery_requests (id) ON DELETE CASCADE
);

CREATE INDEX idx_dl_request_id ON decision_logs(request_id);
CREATE INDEX idx_dl_created_at ON decision_logs(created_at);

CREATE TABLE meeting_notes (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    meeting_title VARCHAR(255) NOT NULL,
    meeting_date DATE NOT NULL,
    attendees TEXT,
    discussion_summary TEXT,
    decisions TEXT,
    action_items TEXT,
    created_by VARCHAR(100) NOT NULL,
    source VARCHAR(30) NOT NULL DEFAULT 'HUMAN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_meeting_notes_request FOREIGN KEY (request_id) REFERENCES delivery_requests (id) ON DELETE CASCADE
);

CREATE INDEX idx_mn_request_id ON meeting_notes(request_id);
CREATE INDEX idx_mn_created_at ON meeting_notes(created_at);
