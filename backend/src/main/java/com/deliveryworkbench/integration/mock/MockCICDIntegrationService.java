package com.deliveryworkbench.integration.mock;

import com.deliveryworkbench.integration.CICDIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockCICDIntegrationService implements CICDIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MockCICDIntegrationService.class);

    @Override
    public String triggerPipeline(String repository, String branch, String environment) {
        String runId = "RUN-" + UUID.randomUUID().toString().substring(0, 6);
        log.info("Mocking CI/CD System: Triggered pipeline {} for {} on branch {} targeting {}", 
                runId, repository, branch, environment);
        return runId;
    }

    @Override
    public String getPipelineStatus(String pipelineId) {
        log.info("Mocking CI/CD System: Checked status for pipeline {}", pipelineId);
        return "SUCCESS"; // Mock status
    }
}
