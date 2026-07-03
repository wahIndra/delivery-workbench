package com.deliveryworkbench.integration;

import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.integration.mock.MockCICDIntegrationService;
import com.deliveryworkbench.integration.mock.MockGitIntegrationService;
import com.deliveryworkbench.integration.mock.MockTicketingIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationLayerTest {

    private TicketingIntegrationService ticketingService;
    private GitIntegrationService gitService;
    private CICDIntegrationService cicdService;

    @BeforeEach
    void setUp() {
        ticketingService = new MockTicketingIntegrationService();
        gitService = new MockGitIntegrationService();
        cicdService = new MockCICDIntegrationService();
    }

    @Test
    void testTicketingIntegration() {
        DeliveryRequest request = DeliveryRequest.builder().requestCode("REQ-2026-00001").build();
        String ticketId = ticketingService.createTicket(request);
        
        assertNotNull(ticketId);
        assertTrue(ticketId.startsWith("TKT-"));
        
        assertEquals("IN_PROGRESS", ticketingService.getTicketStatus(ticketId));
    }

    @Test
    void testGitIntegration() {
        String branchUrl = gitService.createBranch("my-repo", "feature/REQ-2026-00001", "main");
        assertTrue(branchUrl.contains("my-repo/tree/feature/REQ-2026-00001"));

        String prUrl = gitService.createPullRequest("my-repo", "feature/REQ-2026-00001", "main", "Feature branch");
        assertTrue(prUrl.contains("my-repo/pull/PR-"));
    }

    @Test
    void testCICDIntegration() {
        String runId = cicdService.triggerPipeline("my-repo", "main", "dev");
        assertTrue(runId.startsWith("RUN-"));

        assertEquals("SUCCESS", cicdService.getPipelineStatus(runId));
    }
}
