-- V2__seed_data.sql
-- Seed data for IT Delivery Workbench MVP

-- Create a single demo Request (Status: SUBMITTED)
INSERT INTO delivery_requests (
    id, request_code, title, business_problem, expected_outcome,
    current_process, proposed_change, priority,
    business_owner, it_owner, status, created_at, updated_at
) VALUES (
    gen_random_uuid(), 'REQ-2026-00001', 'Automate Monthly Reconciliation',
    'The finance team currently spends 3 days a month manually reconciling invoices across 3 different systems.',
    'Reduce reconciliation time to under 4 hours via automated ETL processes.',
    'Manual export to Excel, vlookups, manual error resolution.',
    'Build an automated integration between System A, B, and C.',
    'HIGH',
    'business.owner',
    'system.analyst',
    'SUBMITTED',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);

-- Note: We capture the UUID to use in the following scripts by a subquery
-- In Postgres, we can do this simply by using a subquery where needed.

-- Add a stage history for creation
INSERT INTO delivery_stage_histories (
    id, delivery_request_id, from_status, to_status,
    changed_by, notes, changed_at
)
SELECT 
    gen_random_uuid(), id, NULL, 'DRAFT', 
    'business.user', 'Initial draft created', CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add a stage history for submission
INSERT INTO delivery_stage_histories (
    id, delivery_request_id, from_status, to_status,
    changed_by, notes, changed_at
)
SELECT 
    gen_random_uuid(), id, 'DRAFT', 'SUBMITTED', 
    'business.owner', 'Submitted for IT Analysis', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add an empty Requirement stub (created when transitioning to SUBMITTED by workflow service ideally, 
-- but since we seeded directly, we seed the requirement too).
INSERT INTO requirements (
    id, delivery_request_id, status, version,
    created_at, updated_at
)
SELECT 
    gen_random_uuid(), id, 'DRAFT', 1, 
    CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add an Impact Analysis stub
INSERT INTO impact_analyses (
    id, delivery_request_id, status, risk_level,
    created_at, updated_at
)
SELECT 
    gen_random_uuid(), id, 'DRAFT', 'LOW', 
    CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add Definition of Ready stub
INSERT INTO definition_of_readies (
    id, delivery_request_id, ready_status,
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
SELECT 
    gen_random_uuid(), id, 'NOT_READY',
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add Release Readiness stub
INSERT INTO release_readinesses (
    id, delivery_request_id, ready_for_release,
    requirement_signed_off, solution_design_approved,
    code_reviewed, sit_passed, uat_signed_off,
    security_reviewed, db_script_reviewed,
    rollback_plan_available, monitoring_prepared,
    release_note_prepared, support_pic_assigned,
    created_at, updated_at
)
SELECT 
    gen_random_uuid(), id, false,
    false, false, false, false, false, false, false,
    false, false, false, false,
    CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';

-- Add a demo Clarification Question
INSERT INTO clarification_questions (
    id, delivery_request_id, question,
    asked_by, status, source,
    created_at, updated_at
)
SELECT 
    gen_random_uuid(), id, 'What is the exact volume of invoices per month?',
    'system.analyst', 'OPEN', 'MANUAL',
    CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM delivery_requests 
WHERE request_code = 'REQ-2026-00001';
