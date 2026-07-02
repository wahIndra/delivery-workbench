-- ============================================================
-- V3__fix_seed_data.sql
-- Corrects V2 seed data which used wrong table/column names
-- and non-existent UUID PKs. V2 is left in place; this V3
-- migration is idempotent — safe to run on both fresh and
-- pre-existing databases.
-- ============================================================

-- ── 1. Ensure demo app_user exists (requester_id FK needed) ──────────────────
INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'business.owner', '$2a$10$PLACEHOLDER_HASH', 'Business Owner', 'business.owner@example.com', 'BUSINESS_OWNER', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'business.owner');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'system.analyst', '$2a$10$PLACEHOLDER_HASH', 'System Analyst', 'system.analyst@example.com', 'SYSTEM_ANALYST', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'system.analyst');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'business.user', '$2a$10$PLACEHOLDER_HASH', 'Business User', 'business.user@example.com', 'BUSINESS_USER', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'business.user');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'principal.engineer', '$2a$10$PLACEHOLDER_HASH', 'Solution Architect', 'sa@example.com', 'SOLUTION_ARCHITECT', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'principal.engineer');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'qa.user', '$2a$10$PLACEHOLDER_HASH', 'QA Engineer', 'qa@example.com', 'QA', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'qa.user');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'release.manager', '$2a$10$PLACEHOLDER_HASH', 'Release Manager', 'rm@example.com', 'RELEASE_MANAGER', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'release.manager');

INSERT INTO app_users (username, password, full_name, email, role, active)
SELECT 'admin', '$2a$10$PLACEHOLDER_HASH', 'Administrator', 'admin@example.com', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'admin');

-- ── 2. Ensure demo delivery request exists ────────────────────────────────────
INSERT INTO delivery_requests (
    request_code, title, business_problem, expected_outcome,
    current_process, proposed_change, impacted_systems, priority,
    business_owner, it_owner, requester_id, status, created_at, updated_at
)
SELECT
    'REQ-2026-00001',
    'Automate Monthly Reconciliation',
    'The finance team currently spends 3 days a month manually reconciling invoices across 3 different systems.',
    'Reduce reconciliation time to under 4 hours via automated ETL processes.',
    'Manual export to Excel, vlookups, manual error resolution.',
    'Build an automated integration between System A, B, and C.',
    'System A, System B, System C',
    'HIGH',
    'business.owner',
    'system.analyst',
    (SELECT id FROM app_users WHERE username = 'business.owner' LIMIT 1),
    'SUBMITTED',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
);

-- ── 3. Stage history ──────────────────────────────────────────────────────────
INSERT INTO delivery_stage_history (request_id, from_status, to_status, changed_by, notes, changed_at)
SELECT id, NULL, 'DRAFT', 'business.user', 'Initial draft created', NOW() - INTERVAL '3 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM delivery_stage_history dsh
    JOIN delivery_requests dr ON dr.id = dsh.request_id
    WHERE dr.request_code = 'REQ-2026-00001' AND dsh.to_status = 'DRAFT'
  );

INSERT INTO delivery_stage_history (request_id, from_status, to_status, changed_by, notes, changed_at)
SELECT id, 'DRAFT', 'SUBMITTED', 'business.owner', 'Submitted for IT Analysis', NOW() - INTERVAL '2 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM delivery_stage_history dsh
    JOIN delivery_requests dr ON dr.id = dsh.request_id
    WHERE dr.request_code = 'REQ-2026-00001' AND dsh.to_status = 'SUBMITTED'
  );

-- ── 4. Requirement stub ───────────────────────────────────────────────────────
INSERT INTO requirements (request_id, status, version, created_at, updated_at)
SELECT id, 'DRAFT', 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM requirements r
    JOIN delivery_requests dr ON dr.id = r.request_id
    WHERE dr.request_code = 'REQ-2026-00001'
  );

-- ── 5. Impact Analysis stub ───────────────────────────────────────────────────
INSERT INTO impact_analyses (request_id, status, risk_level, created_at, updated_at)
SELECT id, 'DRAFT', 'LOW', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM impact_analyses ia
    JOIN delivery_requests dr ON dr.id = ia.request_id
    WHERE dr.request_code = 'REQ-2026-00001'
  );

-- ── 6. Definition of Ready stub ───────────────────────────────────────────────
INSERT INTO definition_of_ready_checklists (
    request_id, ready_status,
    business_problem_clear, expected_outcome_defined,
    scope_agreed, out_of_scope_agreed,
    impacted_users_identified, impacted_systems_identified,
    process_flow_documented, data_requirement_listed,
    integration_requirement_listed, acceptance_criteria_agreed,
    priority_clear, deadline_reason_clear,
    risks_identified, business_owner_assigned,
    it_owner_assigned, tester_assigned,
    created_at, updated_at
)
SELECT id, 'NOT_READY',
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM definition_of_ready_checklists dor
    JOIN delivery_requests dr ON dr.id = dor.request_id
    WHERE dr.request_code = 'REQ-2026-00001'
  );

-- ── 7. Release Readiness stub ─────────────────────────────────────────────────
INSERT INTO release_readiness (
    request_id, requirement_signed_off, solution_design_approved,
    code_reviewed, sit_passed, uat_signed_off,
    security_reviewed, db_script_reviewed,
    rollback_plan_available, monitoring_prepared,
    release_note_prepared, support_pic_assigned,
    ready_for_release, created_at, updated_at
)
SELECT id, false, false, false, false, false, false, false,
    false, false, false, false, false,
    NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM release_readiness rr
    JOIN delivery_requests dr ON dr.id = rr.request_id
    WHERE dr.request_code = 'REQ-2026-00001'
  );

-- ── 8. Clarification Question ─────────────────────────────────────────────────
INSERT INTO clarification_questions (request_id, question, asked_by, status, source, created_at)
SELECT id, 'What is the exact volume of invoices reconciled per month?',
    'system.analyst', 'OPEN', 'MANUAL', NOW() - INTERVAL '1 day'
FROM delivery_requests WHERE request_code = 'REQ-2026-00001'
  AND NOT EXISTS (
    SELECT 1 FROM clarification_questions cq
    JOIN delivery_requests dr ON dr.id = cq.request_id
    WHERE dr.request_code = 'REQ-2026-00001'
  );
