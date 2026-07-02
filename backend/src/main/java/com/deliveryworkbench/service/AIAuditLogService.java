package com.deliveryworkbench.service;

import com.deliveryworkbench.entity.AIActionType;
import com.deliveryworkbench.entity.AIAuditLog;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.repository.AIAuditLogRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage AI Audit Logs (BR-03, SG-05).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIAuditLogService {

    private final AIAuditLogRepository auditLogRepository;

    /**
     * Saves an AI action to the audit log.
     * Uses REQUIRES_NEW propagation to ensure the audit log is saved even if the outer transaction rolls back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAIAction(DeliveryRequest request, AIActionType actionType, String inputPrompt, String outputText) {
        String requestedBy = SecurityUtils.getCurrentUsername();
        if (requestedBy == null) {
            requestedBy = "SYSTEM";
        }

        AIAuditLog logEntry = AIAuditLog.builder()
                .request(request)
                .aiActionType(actionType)
                .inputPrompt(inputPrompt)
                .outputText(outputText)
                .requestedBy(requestedBy)
                .build();

        auditLogRepository.save(logEntry);
        log.info("Saved AI Audit Log for action {} by user {}", actionType, requestedBy);
    }
}
