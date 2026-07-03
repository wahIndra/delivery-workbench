-- V6__add_release_and_risk.sql
-- Create release_schedules table (Phase 12)
CREATE SEQUENCE release_schedules_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE release_schedules (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    release_title VARCHAR(255) NOT NULL,
    planned_release_date TIMESTAMP WITH TIME ZONE,
    actual_release_date TIMESTAMP WITH TIME ZONE,
    release_window VARCHAR(100),
    release_manager VARCHAR(100),
    release_status VARCHAR(50) NOT NULL,
    rollback_plan TEXT,
    release_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_release_schedule_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_rs_request_id ON release_schedules(request_id);
CREATE INDEX idx_rs_status ON release_schedules(release_status);

-- Create risk_registers table (Phase 13)
CREATE SEQUENCE risk_registers_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE risk_registers (
    id BIGINT NOT NULL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    risk_title VARCHAR(255) NOT NULL,
    risk_description TEXT,
    risk_category VARCHAR(50) NOT NULL,
    probability VARCHAR(20) NOT NULL,
    impact VARCHAR(20) NOT NULL,
    risk_score INTEGER,
    mitigation_plan TEXT,
    owner VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_risk_register_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_risk_request_id ON risk_registers(request_id);
CREATE INDEX idx_risk_status ON risk_registers(status);
