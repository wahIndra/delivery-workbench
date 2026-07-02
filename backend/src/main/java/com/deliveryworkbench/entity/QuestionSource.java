package com.deliveryworkbench.entity;

/** Indicates whether a clarification question originated from a human or AI (MockAIService). */
public enum QuestionSource {
    HUMAN,
    /** AI-generated draft — must be reviewed by a human before being sent to business (BR-04). */
    AI
}
