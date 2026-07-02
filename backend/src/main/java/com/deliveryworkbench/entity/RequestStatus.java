package com.deliveryworkbench.entity;

/**
 * Main workflow status machine for DeliveryRequest.
 * All transitions must be recorded in DeliveryStageHistory (BR-06).
 *
 * <p>Key gates:
 * <ul>
 *   <li>READY_FOR_DEVELOPMENT requires DoR readyStatus == READY (BR-01).
 *   <li>READY_FOR_RELEASE requires ReleaseReadiness.readyForRelease == true and uatSignedOff == true (BR-02, BR-10).
 *   <li>READY_FOR_ANALYSIS requires businessOwner and itOwner to be assigned (BR-09).
 * </ul>
 */
public enum RequestStatus {
    DRAFT,
    SUBMITTED,
    NEED_CLARIFICATION,
    REQUIREMENT_REFINEMENT,
    READY_FOR_ANALYSIS,
    IMPACT_ANALYSIS,
    READY_FOR_DEVELOPMENT,
    IN_DEVELOPMENT,
    SIT,
    UAT,
    READY_FOR_RELEASE,
    RELEASED,
    CANCELLED
}
