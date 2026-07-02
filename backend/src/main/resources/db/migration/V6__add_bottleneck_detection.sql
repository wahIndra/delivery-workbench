CREATE SEQUENCE IF NOT EXISTS bottleneck_findings_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bottleneck_findings (
    id BIGINT NOT NULL DEFAULT nextval('bottleneck_findings_seq'),
    request_id BIGINT NOT NULL,
    finding_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT,
    recommended_action TEXT,
    detected_by VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_bottleneck_findings PRIMARY KEY (id),
    CONSTRAINT fk_bottleneck_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_bottleneck_request_id ON bottleneck_findings(request_id);
CREATE INDEX idx_bottleneck_status ON bottleneck_findings(status);
