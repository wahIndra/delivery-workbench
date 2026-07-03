package com.deliveryworkbench.ai;

/**
 * AIService — the single authorised interface for all AI operations in the system.
 *
 * <p><strong>Governance contract (must never be violated):</strong>
 * <ul>
 *   <li>Every implementation MUST save output to AIAuditLog before returning (BR-03).
 *   <li>Implementations MUST NOT change workflow status (SG-08).
 *   <li>Implementations MUST NOT approve any checklist (SG-08).
 *   <li>Implementations MUST NOT trigger deployment (SG-09).
 *   <li>Implementations MUST NOT access secrets or production credentials (SG-09).
 * </ul>
 *
 * <p>For MVP, use {@link MockAIService}. Real LLM implementation is wired in later via Spring profile.
 */
public interface AIService {

    /**
     * Generate clarification questions for a delivery request.
     * Output must be saved to AIAuditLog by the caller (service layer).
     *
     * @param requestId  the delivery request id
     * @param requestTitle   the request title
     * @param businessProblem the business problem description
     * @param proposedChange  the proposed change description
     * @return AI-generated clarification questions as formatted text
     */
    String generateClarificationQuestions(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String proposedChange
    );

    /**
     * Generate user story and acceptance criteria from request and clarification answers.
     *
     * @param requestId       the delivery request id
     * @param requestTitle    the request title
     * @param businessProblem the business problem
     * @param clarificationSummary summary of Q&A clarifications
     * @return AI-generated user story and acceptance criteria as formatted text
     */
    String generateUserStoryAndAcceptanceCriteria(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String clarificationSummary
    );

    /**
     * Generate a draft impact analysis from the request and its requirements.
     *
     * @param requestId       the delivery request id
     * @param requestTitle    the request title
     * @param impactedSystems comma-separated impacted systems from the request
     * @param requirementScope scope of the approved requirement
     * @return AI-generated impact analysis draft as formatted text
     */
    String generateImpactAnalysisDraft(
            Long requestId,
            String requestTitle,
            String impactedSystems,
            String requirementScope
    );

    /**
     * Generate test scenarios from the requirement and impact analysis.
     *
     * @param requestId       the delivery request id
     * @param requirementScope scope of the requirement
     * @param acceptanceCriteria the acceptance criteria
     * @param riskLevel       the assessed risk level (LOW/MEDIUM/HIGH)
     * @return AI-generated test scenarios as formatted text
     */
    String generateTestScenarios(
            Long requestId,
            String requirementScope,
            String acceptanceCriteria,
            String riskLevel
    );

    /**
     * Generate a release checklist from requirement, impact analysis, and QA summary.
     *
     * @param requestId        the delivery request id
     * @param requestTitle     the request title
     * @param riskLevel        the assessed risk level
     * @param qaScenarioCount  number of QA scenarios defined
     * @return AI-generated release checklist as formatted text
     */
    String generateReleaseChecklist(
            Long requestId,
            String requestTitle,
            String riskLevel,
            int qaScenarioCount
    );

    /**
     * Generate priority recommendation based on request attributes.
     *
     * @param requestId        the delivery request id
     * @param requestTitle     the request title
     * @param businessProblem  the business problem
     * @param expectedOutcome  the expected outcome
     * @return AI-generated priority recommendation as formatted text
     */
    String generatePriorityRecommendation(
            Long requestId,
            String requestTitle,
            String businessProblem,
            String expectedOutcome
    );

    /**
     * Generates the Next Best Action recommendation for a delivery request based on context.
     */
    NextBestActionRecommendation generateNextBestAction(
            Long requestId,
            String status,
            boolean isDorReady,
            long clarificationCount,
            long openBottlenecks,
            String requestedBy);

    MeetingNoteSummary summarizeMeetingNotes(String rawNotes);

    /**
     * Generates risk suggestions based on the request context.
     */
    java.util.List<RiskSuggestionDto> generateRiskSuggestions(
            Long requestId,
            String requestTitle,
            String businessProblem
    );
}
