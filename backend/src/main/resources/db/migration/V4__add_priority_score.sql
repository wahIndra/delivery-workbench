CREATE SEQUENCE IF NOT EXISTS pri_scores_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE request_priority_scores (
    id BIGINT NOT NULL DEFAULT nextval('pri_scores_seq'),
    request_id BIGINT NOT NULL,
    business_impact_score INT DEFAULT 1,
    urgency_score INT DEFAULT 1,
    regulatory_impact_score INT DEFAULT 1,
    customer_impact_score INT DEFAULT 1,
    operational_risk_score INT DEFAULT 1,
    technical_complexity_score INT DEFAULT 1,
    dependency_score INT DEFAULT 1,
    total_score INT DEFAULT 0,
    priority_recommendation VARCHAR(20),
    scoring_notes TEXT,
    scored_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_request_priority_scores PRIMARY KEY (id),
    CONSTRAINT fk_pri_score_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE UNIQUE INDEX idx_pri_score_request_id ON request_priority_scores(request_id);

-- Insert stubs for existing requests
INSERT INTO request_priority_scores (request_id, created_at, updated_at)
SELECT id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM delivery_requests;
