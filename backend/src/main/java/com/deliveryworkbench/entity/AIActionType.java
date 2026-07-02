package com.deliveryworkbench.entity;

/**
 * AI action types — every AI call must be logged with one of these types (BR-03).
 * Saved to AIAuditLog before returning output to the caller.
 */
public enum AIActionType {
    GENERATE_CLARIFICATION_QUESTIONS,
    GENERATE_USER_STORY,
    GENERATE_ACCEPTANCE_CRITERIA,
    GENERATE_IMPACT_ANALYSIS_DRAFT,
    GENERATE_TEST_SCENARIOS,
    GENERATE_RELEASE_CHECKLIST
}
