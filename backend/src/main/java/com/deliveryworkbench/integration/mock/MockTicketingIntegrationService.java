package com.deliveryworkbench.integration.mock;

import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.integration.TicketingIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockTicketingIntegrationService implements TicketingIntegrationService {
    
    private static final Logger log = LoggerFactory.getLogger(MockTicketingIntegrationService.class);

    @Override
    public String createTicket(DeliveryRequest request) {
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        log.info("Mocking Ticketing System: Created ticket {} for request {}", ticketId, request.getRequestCode());
        return ticketId;
    }

    @Override
    public void updateTicketStatus(String ticketId, String status) {
        log.info("Mocking Ticketing System: Updated ticket {} to status {}", ticketId, status);
    }

    @Override
    public String getTicketStatus(String ticketId) {
        log.info("Mocking Ticketing System: Checked status for ticket {}", ticketId);
        return "IN_PROGRESS"; // Mock status
    }
}
