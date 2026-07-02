-- V10__add_requirement_versions.sql

CREATE SEQUENCE requirement_versions_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE requirement_versions (
    id BIGINT NOT NULL PRIMARY KEY,
    requirement_id BIGINT NOT NULL,
    request_id BIGINT NOT NULL,
    version INT NOT NULL,
    scope TEXT,
    out_of_scope TEXT,
    user_story TEXT,
    acceptance_criteria TEXT,
    assumptions TEXT,
    dependencies TEXT,
    change_reason TEXT,
    changed_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_req_versions_requirement FOREIGN KEY (requirement_id) REFERENCES requirements(id) ON DELETE CASCADE
);

CREATE INDEX idx_req_versions_req_id ON requirement_versions(requirement_id);
CREATE INDEX idx_req_versions_request_id ON requirement_versions(request_id);

-- Migrate existing current requirements into the new requirement_versions table
INSERT INTO requirement_versions (
    id, requirement_id, request_id, version, scope, out_of_scope, user_story, 
    acceptance_criteria, assumptions, dependencies, change_reason, changed_by, created_at
)
SELECT 
    NEXTVAL('requirement_versions_seq'), id, request_id, version, scope, out_of_scope, user_story, 
    acceptance_criteria, assumptions, dependencies, 'Initial version', 'system', created_at
FROM requirements;
