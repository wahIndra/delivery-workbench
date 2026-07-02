package com.deliveryworkbench.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MockAIService — deterministic, human-readable AI responses for MVP.
 *
 * <p>This implementation uses the request data to produce structured, useful output
 * so the system is demonstrable without a real LLM.
 *
 * <p>When a real LLM is integrated, this class is replaced by a new implementation
 * of {@link AIService} and wired via Spring profile — no other code needs to change.
 *
 * <p><strong>Guardrails (never to be violated in any implementation):</strong>
 * <ul>
 *   <li>This service returns text only — it does NOT change any entity state.
 *   <li>This service does NOT access the database, secrets, or credentials.
 *   <li>This service does NOT approve checklists or readiness.
 *   <li>The calling service layer is responsible for saving output to AIAuditLog (BR-03).
 * </ul>
 */
@Slf4j
@Service
public class MockAIService implements AIService {

    @Override
    public String generateClarificationQuestions(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String proposedChange) {

        log.info("[MockAI] generateClarificationQuestions for requestId={}", requestId);

        return """
                AI-Generated Clarification Questions for: %s
                =====================================================
                
                Based on the business problem and proposed change, the following clarification
                questions are suggested. Please review and edit before sending to the business team.
                
                1. What is the current volume of transactions affected by this change per day?
                2. Are there any downstream systems that consume data from the impacted area?
                3. What is the expected peak load during UAT and after go-live?
                4. Are there any existing integrations (APIs or queues) that need to be updated?
                5. What is the rollback plan if the change causes issues in production?
                6. Are there regulatory or compliance requirements associated with this change?
                7. Who are the key business stakeholders who must sign off on UAT?
                8. Is there a hard deadline driven by a business event (e.g. regulatory date, partner launch)?
                
                Note: These questions are AI-suggested drafts. A human reviewer must validate,
                edit, and approve them before sending to the business team (BR-04).
                """.formatted(requestTitle);
    }

    @Override
    public String generateUserStoryAndAcceptanceCriteria(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String clarificationSummary) {

        log.info("[MockAI] generateUserStoryAndAcceptanceCriteria for requestId={}", requestId);

        return """
                AI-Generated User Story and Acceptance Criteria for: %s
                =====================================================
                
                USER STORY
                ----------
                As a business user impacted by "%s",
                I want the system to support the proposed change,
                So that the business problem is resolved and operations can continue smoothly.
                
                ACCEPTANCE CRITERIA
                -------------------
                Given the change has been deployed to the target environment,
                When the user performs the relevant business operation,
                Then the expected outcome is achieved without errors.
                
                Given edge cases and boundary conditions exist,
                When they are exercised during testing,
                Then the system handles them gracefully with appropriate messages.
                
                Given the change affects downstream systems,
                When integration points are exercised,
                Then data consistency is maintained end-to-end.
                
                Note: This is an AI-generated draft. A human System Analyst must review,
                refine, and approve this content before it is used as the official requirement (BR-05).
                """.formatted(requestTitle, businessProblem);
    }

    @Override
    public String generateImpactAnalysisDraft(
            Long requestId,
            String requestTitle,
            String impactedSystems,
            String requirementScope) {

        log.info("[MockAI] generateImpactAnalysisDraft for requestId={}", requestId);

        return """
                AI-Generated Impact Analysis Draft for: %s
                =====================================================
                
                IMPACTED SYSTEMS: %s
                REQUIREMENT SCOPE: %s
                
                TECHNICAL IMPACT ASSESSMENT (DRAFT)
                ------------------------------------
                Application Impact:
                  - Review all modules that interact with the impacted systems.
                  - Identify API contracts that may change or need versioning.
                
                Database Impact:
                  - Assess schema changes required (new tables, columns, indexes).
                  - Review migration complexity and reversibility.
                
                Integration Impact:
                  - Identify all upstream and downstream integration points.
                  - Confirm backward compatibility or coordinate version upgrades.
                
                Security Impact:
                  - Review authentication and authorisation changes if any.
                  - Confirm no sensitive data is exposed by new endpoints.
                
                Performance Impact:
                  - Estimate query impact for new data access patterns.
                  - Confirm caching strategy if high read volume is expected.
                
                Operational Impact:
                  - Confirm monitoring and alerting are updated for new functionality.
                  - Prepare runbook additions for operations team.
                
                PRELIMINARY RISK LEVEL: MEDIUM
                (This is AI-estimated. A Solution Architect must review and confirm the risk level.)
                
                Note: This is an AI-generated draft. A Solution Architect must review,
                update, and approve this impact analysis before it is used in planning (BR-05).
                """.formatted(requestTitle, impactedSystems, requirementScope);
    }

    @Override
    public String generateTestScenarios(
            Long requestId,
            String requirementScope,
            String acceptanceCriteria,
            String riskLevel) {

        log.info("[MockAI] generateTestScenarios for requestId={}", requestId);

        return """
                AI-Generated Test Scenarios
                =====================================================
                
                SCOPE: %s
                RISK LEVEL: %s
                
                POSITIVE SCENARIOS
                ------------------
                Scenario 1: Happy Path — Primary Use Case
                  Precondition: System is in normal operating state, user is authenticated.
                  Steps: Execute the primary business operation end-to-end.
                  Expected: Operation completes successfully; correct data is persisted.
                
                Scenario 2: Boundary Values — Valid Edge Cases
                  Precondition: Valid data at boundary limits.
                  Steps: Submit requests with maximum/minimum accepted values.
                  Expected: System accepts and processes correctly.
                
                NEGATIVE SCENARIOS
                ------------------
                Scenario 3: Invalid Input — Mandatory Fields Missing
                  Precondition: Incomplete form submission.
                  Steps: Submit with mandatory fields blank.
                  Expected: System returns validation error with clear message.
                
                Scenario 4: Unauthorized Access
                  Precondition: User without required role attempts action.
                  Steps: Send request without valid token or correct role.
                  Expected: HTTP 403 Forbidden returned.
                
                REGRESSION SCENARIOS
                --------------------
                Scenario 5: Existing Functionality Not Broken
                  Precondition: Related features are operational.
                  Steps: Execute existing use cases unaffected by this change.
                  Expected: No regression; all existing tests pass.
                
                INTEGRATION SCENARIOS
                ---------------------
                Scenario 6: Downstream System Integration
                  Precondition: Downstream system is available in SIT environment.
                  Steps: Trigger the operation that calls the downstream system.
                  Expected: Data is correctly propagated; downstream system acknowledges.
                
                Note: These are AI-generated draft scenarios. A QA Engineer must review,
                add specific test data, and approve before use in SIT/UAT (BR-05).
                """.formatted(requirementScope, riskLevel);
    }

    @Override
    public String generateReleaseChecklist(
            Long requestId,
            String requestTitle,
            String riskLevel,
            int qaScenarioCount) {

        log.info("[MockAI] generateReleaseChecklist for requestId={}", requestId);

        return """
                AI-Generated Release Checklist for: %s
                =====================================================
                
                RISK LEVEL: %s | QA SCENARIOS DEFINED: %d
                
                PRE-RELEASE CHECKLIST (AI Draft)
                ---------------------------------
                [ ] Requirement signed off by Business Owner
                [ ] Solution design approved by Solution Architect
                [ ] Code review completed by peer developer
                [ ] SIT passed — all %d test scenarios executed and passed
                [ ] UAT signed off by Business Owner / UAT PIC
                [ ] Security review completed (if HIGH risk)
                [ ] Database migration script reviewed and tested
                [ ] Rollback plan documented and rehearsed
                [ ] Monitoring and alerts updated
                [ ] Release note prepared and communicated
                [ ] Support PIC assigned for go-live window
                
                RECOMMENDED DEPLOYMENT WINDOW
                ------------------------------
                Based on risk level %s:
                %s
                
                Note: This is an AI-generated draft checklist. The Release Manager must review,
                complete, and formally approve each item. AI does not approve release readiness (BR-05).
                """.formatted(
                requestTitle,
                riskLevel,
                qaScenarioCount,
                qaScenarioCount,
                riskLevel,
                getDeploymentWindowRecommendation(riskLevel)
        );
    }

    private String getDeploymentWindowRecommendation(String riskLevel) {
        return switch (riskLevel.toUpperCase()) {
            case "HIGH" -> "Deploy during off-peak hours with full war-room support. Avoid Friday releases.";
            case "MEDIUM" -> "Deploy during business hours with on-call support available.";
            case "LOW" -> "Standard deployment window applies.";
            default -> "Consult Release Manager for deployment window recommendation.";
        };
    }

    @Override
    public String generatePriorityRecommendation(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String expectedOutcome) {

        log.info("[MockAI] generatePriorityRecommendation for requestId={}", requestId);

        // Simple mock logic based on keywords
        String lowerContext = (businessProblem + " " + expectedOutcome).toLowerCase();
        String recommendation = "MEDIUM";
        String notes = "Standard priority based on typical request parameters.";

        if (lowerContext.contains("urgent") || lowerContext.contains("critical") || lowerContext.contains("outage")) {
            recommendation = "CRITICAL";
            notes = "Identified critical keywords (urgent, critical, outage). Immediate attention recommended.";
        } else if (lowerContext.contains("regulatory") || lowerContext.contains("compliance") || lowerContext.contains("deadline")) {
            recommendation = "HIGH";
            notes = "Identified time-sensitive or compliance keywords (regulatory, compliance, deadline). High priority recommended.";
        } else if (lowerContext.contains("minor") || lowerContext.contains("nice to have") || lowerContext.contains("cosmetic")) {
            recommendation = "LOW";
            notes = "Identified low impact keywords (minor, cosmetic, nice to have).";
        }

        return """
                AI-Generated Priority Recommendation
                =====================================================
                
                RECOMMENDED PRIORITY: %s
                
                ANALYSIS NOTES:
                %s
                
                CONTEXT ANALYZED:
                - Title: %s
                - Problem: %s
                - Expected Outcome: %s
                
                Note: This is an AI-generated draft. A human must review and approve the final priority score (BR-05).
                """.formatted(recommendation, notes, requestTitle, businessProblem, expectedOutcome);
    }

    @Override
    public NextBestActionRecommendation generateNextBestAction(
            Long requestId,
            String status,
            boolean isDorReady,
            long clarificationCount,
            long openBottlenecks,
            String requestedBy) {

        log.info("[MockAI] generateNextBestAction for requestId={}", requestId);

        String recommendation = "No clear action identified. Please review manually.";
        String reason = "Context does not match any known proactive patterns.";

        if (openBottlenecks > 0) {
            recommendation = "Review and resolve active bottleneck findings.";
            reason = "There are " + openBottlenecks + " open bottleneck(s) that need intervention before proceeding smoothly.";
        } else if ("SUBMITTED".equals(status) || "NEED_CLARIFICATION".equals(status)) {
            recommendation = "Complete Definition of Ready (DoR) checklist.";
            reason = "The request is in early stages. Completing the DoR ensures all requirements are clear before moving to development.";
        } else if ("READY_FOR_DEVELOPMENT".equals(status)) {
            if (!isDorReady) {
                recommendation = "Finish remaining DoR items or reassess readiness.";
                reason = "Request is marked ready for development but DoR is incomplete. This is a risk.";
            } else {
                recommendation = "Assign to a developer and move to IN_DEVELOPMENT.";
                reason = "Request is ready and awaiting development resources.";
            }
        } else if ("IN_DEVELOPMENT".equals(status)) {
            recommendation = "Complete coding and peer review, then move to SIT.";
            reason = "Standard development lifecycle progression.";
        } else if ("SIT".equals(status)) {
            recommendation = "Complete SIT test execution and move to UAT.";
            reason = "Testing phase active. Needs SIT sign-off.";
        } else if ("UAT".equals(status)) {
            recommendation = "Obtain UAT sign-off from Business Owner (" + requestedBy + ") and move to READY_FOR_RELEASE.";
            reason = "UAT must be explicitly approved by the business owner before release.";
        } else if ("READY_FOR_RELEASE".equals(status)) {
            recommendation = "Complete Release Readiness checklist and deploy to PROD.";
            reason = "Awaiting deployment execution.";
        } else if ("PROD".equals(status)) {
            recommendation = "Monitor system post-deployment. No further action needed.";
            reason = "Request is completed.";
        }

        return NextBestActionRecommendation.builder()
                .recommendation(recommendation)
                .reason(reason)
                .build();
    }

    @Override
    public MeetingNoteSummary summarizeMeetingNotes(String rawNotes) {
        log.info("[MockAI] Summarizing meeting notes: {} characters", rawNotes != null ? rawNotes.length() : 0);
        
        if (rawNotes == null || rawNotes.isBlank()) {
            return MeetingNoteSummary.builder()
                    .discussionSummary("No notes provided.")
                    .decisions("None")
                    .actionItems("None")
                    .build();
        }

        // Extremely simple deterministic summarization for mock purposes
        return MeetingNoteSummary.builder()
                .discussionSummary("The team discussed the requirements and technical details based on the provided notes. Key points were reviewed to ensure alignment on the delivery approach.")
                .decisions("- Approved the proposed scope.\n- Agreed to proceed with the current technical architecture.")
                .actionItems("- Update the Definition of Ready.\n- Follow up with the IT Owner for the API specification.")
                .build();
    }
}
