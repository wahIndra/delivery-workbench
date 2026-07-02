CREATE SEQUENCE IF NOT EXISTS next_best_actions_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE next_best_actions (
    id BIGINT NOT NULL DEFAULT nextval('next_best_actions_seq'),
    request_id BIGINT NOT NULL,
    recommendation TEXT NOT NULL,
    reason TEXT,
    suggested_owner VARCHAR(100),
    suggested_due_date TIMESTAMP WITH TIME ZONE,
    source VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP WITH TIME ZONE,
    rejected_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_next_best_actions PRIMARY KEY (id),
    CONSTRAINT fk_nba_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_nba_request_id ON next_best_actions(request_id);
CREATE INDEX idx_nba_status ON next_best_actions(status);
