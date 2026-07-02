package com.deliveryworkbench.entity;

/** Definition of Ready evaluation result. Controls the READY_FOR_DEVELOPMENT gate (BR-01). */
public enum ReadyStatus {
    NOT_READY,
    PARTIALLY_READY,
    /** Only READY allows transition to READY_FOR_DEVELOPMENT (BR-01). */
    READY
}
