CREATE SEQUENCE IF NOT EXISTS stage_sla_policies_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS aging_snapshots_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE stage_sla_policies (
    id BIGINT NOT NULL DEFAULT nextval('stage_sla_policies_seq'),
    stage VARCHAR(30) NOT NULL UNIQUE,
    sla_hours INT NOT NULL,
    warning_threshold_hours INT NOT NULL,
    escalation_threshold_hours INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_stage_sla_policies PRIMARY KEY (id)
);

CREATE TABLE request_aging_snapshots (
    id BIGINT NOT NULL DEFAULT nextval('aging_snapshots_seq'),
    request_id BIGINT NOT NULL,
    current_status VARCHAR(30) NOT NULL,
    entered_status_at TIMESTAMP WITH TIME ZONE NOT NULL,
    aging_hours INT NOT NULL,
    sla_hours INT,
    sla_status VARCHAR(20) NOT NULL,
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_request_aging_snapshots PRIMARY KEY (id),
    CONSTRAINT fk_aging_snapshot_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_aging_request_id ON request_aging_snapshots(request_id);
CREATE INDEX idx_aging_calculated_at ON request_aging_snapshots(calculated_at);

-- Insert default SLA policies for the stages
INSERT INTO stage_sla_policies (stage, sla_hours, warning_threshold_hours, escalation_threshold_hours, created_at, updated_at) VALUES 
('DRAFT', 72, 48, 96, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SUBMITTED', 24, 16, 48, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NEED_CLARIFICATION', 48, 24, 72, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('READY_FOR_ANALYSIS', 24, 16, 48, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('IMPACT_ANALYSIS', 72, 48, 96, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REQUIREMENT_REFINEMENT', 120, 96, 168, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('READY_FOR_DEVELOPMENT', 48, 24, 96, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('IN_DEVELOPMENT', 336, 240, 504, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 14 days
('SIT', 168, 120, 240, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 7 days
('UAT', 120, 96, 168, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 5 days
('READY_FOR_RELEASE', 72, 48, 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update DeliveryRequest to track status_entered_at. We add the column and populate it.
ALTER TABLE delivery_requests ADD COLUMN status_entered_at TIMESTAMP WITH TIME ZONE;
UPDATE delivery_requests SET status_entered_at = updated_at WHERE status_entered_at IS NULL;
ALTER TABLE delivery_requests ALTER COLUMN status_entered_at SET NOT NULL;
