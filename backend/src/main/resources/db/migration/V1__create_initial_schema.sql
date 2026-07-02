-- ============================================================
-- V1__create_initial_schema.sql
-- IT Delivery Workbench — Initial Database Schema
-- ============================================================

-- ── Sequences ────────────────────────────────────────────────
CREATE SEQUENCE IF NOT EXISTS app_users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS delivery_requests_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS clarification_questions_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS requirements_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS dor_checklists_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS impact_analyses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS delivery_stage_history_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS qa_test_scenarios_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS release_readiness_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ai_audit_logs_seq START WITH 1 INCREMENT BY 1;

-- ── app_users ─────────────────────────────────────────────────
CREATE TABLE app_users (
    id          BIGINT      NOT NULL DEFAULT nextval('app_users_seq'),
    username    VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(150) NOT NULL,
    email       VARCHAR(150),
    role        VARCHAR(30)  NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_app_users PRIMARY KEY (id),
    CONSTRAINT uk_app_users_username UNIQUE (username)
);

-- ── delivery_requests ─────────────────────────────────────────
CREATE TABLE delivery_requests (
    id                  BIGINT       NOT NULL DEFAULT nextval('delivery_requests_seq'),
    request_code        VARCHAR(30)  NOT NULL,
    title               VARCHAR(255) NOT NULL,
    business_problem    TEXT,
    expected_outcome    TEXT,
    current_process     TEXT,
    proposed_change     TEXT,
    impacted_users      TEXT,
    impacted_channels   TEXT,
    impacted_systems    TEXT,
    priority            VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    deadline            DATE,
    deadline_reason     TEXT,
    business_owner      VARCHAR(100),
    it_owner            VARCHAR(100),
    requester_id        BIGINT       NOT NULL,
    uat_pic             VARCHAR(100),
    status              VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_delivery_requests PRIMARY KEY (id),
    CONSTRAINT uk_delivery_requests_code UNIQUE (request_code),
    CONSTRAINT fk_dr_requester FOREIGN KEY (requester_id) REFERENCES app_users(id)
);

CREATE INDEX idx_dr_status      ON delivery_requests(status);
CREATE INDEX idx_dr_request_code ON delivery_requests(request_code);
CREATE INDEX idx_dr_requester_id ON delivery_requests(requester_id);
CREATE INDEX idx_dr_created_at  ON delivery_requests(created_at);

-- ── clarification_questions ───────────────────────────────────
CREATE TABLE clarification_questions (
    id          BIGINT      NOT NULL DEFAULT nextval('clarification_questions_seq'),
    request_id  BIGINT      NOT NULL,
    question    TEXT        NOT NULL,
    answer      TEXT,
    asked_by    VARCHAR(100),
    answered_by VARCHAR(100),
    source      VARCHAR(10)  NOT NULL DEFAULT 'HUMAN',
    status      VARCHAR(15)  NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    answered_at TIMESTAMPTZ,
    CONSTRAINT pk_clarification_questions PRIMARY KEY (id),
    CONSTRAINT fk_cq_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_cq_request_id ON clarification_questions(request_id);
CREATE INDEX idx_cq_status     ON clarification_questions(status);

-- ── requirements ──────────────────────────────────────────────
CREATE TABLE requirements (
    id                  BIGINT      NOT NULL DEFAULT nextval('requirements_seq'),
    request_id          BIGINT      NOT NULL,
    scope               TEXT,
    out_of_scope        TEXT,
    user_story          TEXT,
    acceptance_criteria TEXT,
    assumptions         TEXT,
    dependencies        TEXT,
    status              VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    version             INTEGER      NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_requirements PRIMARY KEY (id),
    CONSTRAINT fk_req_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_req_request_id ON requirements(request_id);

-- ── definition_of_ready_checklists ────────────────────────────
CREATE TABLE definition_of_ready_checklists (
    id                              BIGINT      NOT NULL DEFAULT nextval('dor_checklists_seq'),
    request_id                      BIGINT      NOT NULL,
    business_problem_clear          BOOLEAN     NOT NULL DEFAULT FALSE,
    expected_outcome_defined        BOOLEAN     NOT NULL DEFAULT FALSE,
    scope_agreed                    BOOLEAN     NOT NULL DEFAULT FALSE,
    out_of_scope_agreed             BOOLEAN     NOT NULL DEFAULT FALSE,
    impacted_users_identified       BOOLEAN     NOT NULL DEFAULT FALSE,
    impacted_systems_identified     BOOLEAN     NOT NULL DEFAULT FALSE,
    process_flow_documented         BOOLEAN     NOT NULL DEFAULT FALSE,
    data_requirement_listed         BOOLEAN     NOT NULL DEFAULT FALSE,
    integration_requirement_listed  BOOLEAN     NOT NULL DEFAULT FALSE,
    acceptance_criteria_agreed      BOOLEAN     NOT NULL DEFAULT FALSE,
    priority_clear                  BOOLEAN     NOT NULL DEFAULT FALSE,
    deadline_reason_clear           BOOLEAN     NOT NULL DEFAULT FALSE,
    risks_identified                BOOLEAN     NOT NULL DEFAULT FALSE,
    business_owner_assigned         BOOLEAN     NOT NULL DEFAULT FALSE,
    it_owner_assigned               BOOLEAN     NOT NULL DEFAULT FALSE,
    tester_assigned                 BOOLEAN     NOT NULL DEFAULT FALSE,
    ready_status                    VARCHAR(20) NOT NULL DEFAULT 'NOT_READY',
    reviewed_by                     VARCHAR(100),
    reviewed_at                     TIMESTAMPTZ,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_dor_checklists PRIMARY KEY (id),
    CONSTRAINT uk_dor_request UNIQUE (request_id),
    CONSTRAINT fk_dor_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_dor_request_id ON definition_of_ready_checklists(request_id);

-- ── impact_analyses ───────────────────────────────────────────
CREATE TABLE impact_analyses (
    id                      BIGINT      NOT NULL DEFAULT nextval('impact_analyses_seq'),
    request_id              BIGINT      NOT NULL,
    impacted_applications   TEXT,
    impacted_databases      TEXT,
    impacted_apis           TEXT,
    impacted_jobs           TEXT,
    impacted_queues         TEXT,
    integration_impact      TEXT,
    security_impact         TEXT,
    performance_impact      TEXT,
    operational_impact      TEXT,
    data_impact             TEXT,
    risk_level              VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    mitigation_plan         TEXT,
    reviewed_by             VARCHAR(100),
    status                  VARCHAR(15) NOT NULL DEFAULT 'DRAFT',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_impact_analyses PRIMARY KEY (id),
    CONSTRAINT uk_ia_request UNIQUE (request_id),
    CONSTRAINT fk_ia_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_ia_request_id ON impact_analyses(request_id);

-- ── delivery_stage_history ────────────────────────────────────
-- Immutable audit trail (BR-06, SG-06) — no UPDATE or DELETE permitted.
CREATE TABLE delivery_stage_history (
    id          BIGINT      NOT NULL DEFAULT nextval('delivery_stage_history_seq'),
    request_id  BIGINT      NOT NULL,
    from_status VARCHAR(30),
    to_status   VARCHAR(30) NOT NULL,
    changed_by  VARCHAR(100) NOT NULL,
    notes       TEXT,
    changed_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_delivery_stage_history PRIMARY KEY (id),
    CONSTRAINT fk_dsh_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_dsh_request_id ON delivery_stage_history(request_id);
CREATE INDEX idx_dsh_changed_at ON delivery_stage_history(changed_at);

-- ── qa_test_scenarios ─────────────────────────────────────────
CREATE TABLE qa_test_scenarios (
    id              BIGINT      NOT NULL DEFAULT nextval('qa_test_scenarios_seq'),
    request_id      BIGINT      NOT NULL,
    scenario_name   VARCHAR(255) NOT NULL,
    scenario_type   VARCHAR(20)  NOT NULL DEFAULT 'POSITIVE',
    precondition    TEXT,
    test_steps      TEXT,
    expected_result TEXT,
    status          VARCHAR(15)  NOT NULL DEFAULT 'DRAFT',
    created_by      VARCHAR(100),
    source          VARCHAR(10)  NOT NULL DEFAULT 'HUMAN',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_qa_test_scenarios PRIMARY KEY (id),
    CONSTRAINT fk_qa_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_qa_request_id ON qa_test_scenarios(request_id);
CREATE INDEX idx_qa_status     ON qa_test_scenarios(status);

-- ── release_readiness ─────────────────────────────────────────
CREATE TABLE release_readiness (
    id                      BIGINT      NOT NULL DEFAULT nextval('release_readiness_seq'),
    request_id              BIGINT      NOT NULL,
    requirement_signed_off  BOOLEAN     NOT NULL DEFAULT FALSE,
    solution_design_approved BOOLEAN    NOT NULL DEFAULT FALSE,
    code_reviewed           BOOLEAN     NOT NULL DEFAULT FALSE,
    sit_passed              BOOLEAN     NOT NULL DEFAULT FALSE,
    uat_signed_off          BOOLEAN     NOT NULL DEFAULT FALSE,
    security_reviewed       BOOLEAN     NOT NULL DEFAULT FALSE,
    db_script_reviewed      BOOLEAN     NOT NULL DEFAULT FALSE,
    rollback_plan_available BOOLEAN     NOT NULL DEFAULT FALSE,
    monitoring_prepared     BOOLEAN     NOT NULL DEFAULT FALSE,
    release_note_prepared   BOOLEAN     NOT NULL DEFAULT FALSE,
    support_pic_assigned    BOOLEAN     NOT NULL DEFAULT FALSE,
    ready_for_release       BOOLEAN     NOT NULL DEFAULT FALSE,
    reviewed_by             VARCHAR(100),
    reviewed_at             TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_release_readiness PRIMARY KEY (id),
    CONSTRAINT uk_rr_request UNIQUE (request_id),
    CONSTRAINT fk_rr_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_rr_request_id ON release_readiness(request_id);

-- ── ai_audit_logs ─────────────────────────────────────────────
-- Immutable (BR-03, SG-05) — no UPDATE or DELETE permitted.
CREATE TABLE ai_audit_logs (
    id              BIGINT      NOT NULL DEFAULT nextval('ai_audit_logs_seq'),
    request_id      BIGINT,
    ai_action_type  VARCHAR(50)  NOT NULL,
    input_prompt    TEXT,
    output_text     TEXT,
    requested_by    VARCHAR(100) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_ai_audit_logs PRIMARY KEY (id),
    CONSTRAINT fk_aal_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id)
);

CREATE INDEX idx_aal_request_id  ON ai_audit_logs(request_id);
CREATE INDEX idx_aal_action_type ON ai_audit_logs(ai_action_type);
CREATE INDEX idx_aal_created_at  ON ai_audit_logs(created_at);
