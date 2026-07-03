package com.deliveryworkbench.integration;

import com.deliveryworkbench.entity.DeliveryRequest;

public interface TicketingIntegrationService {
    
    /**
     * Creates a ticket in the external ticketing system (e.g. Jira, ServiceNow).
     * @param request the DeliveryRequest entity
     * @return the external ticket ID (e.g. JIRA-101)
     */
    String createTicket(DeliveryRequest request);
    
    /**
     * Updates the status of an existing ticket in the external system.
     * @param ticketId the external ticket ID
     * @param status the new status
     */
    void updateTicketStatus(String ticketId, String status);
    
    /**
     * Gets the current status of the ticket from the external system.
     * @param ticketId the external ticket ID
     * @return the status string
     */
    String getTicketStatus(String ticketId);
}
