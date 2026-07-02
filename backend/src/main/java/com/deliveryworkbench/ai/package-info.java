/**
 * AI service layer.
 *
 * <p>This package contains:
 * <ul>
 *   <li>{@code AIService} — the interface that all AI callers must use (SG-07).
 *   <li>{@code MockAIService} — deterministic mock implementation used for MVP (SG-10).
 * </ul>
 *
 * <p>Guardrails enforced here:
 * <ul>
 *   <li>AI may NOT change workflow status (SG-08).
 *   <li>AI may NOT approve any checklist (SG-08).
 *   <li>AI may NOT trigger deployment (SG-09).
 *   <li>AI may NOT access secrets or production credentials (SG-09).
 *   <li>Every AI output MUST be saved to AIAuditLog before returning to caller (BR-03).
 * </ul>
 */
@NonNullApi
package com.deliveryworkbench.ai;

import org.springframework.lang.NonNullApi;
