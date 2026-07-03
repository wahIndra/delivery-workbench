package com.deliveryworkbench.entity;

/**
 * Application user roles — controls what each user can do in the system.
 * Role enforcement is done at service layer via Spring Security @PreAuthorize (SG-03).
 */
public enum UserRole {
    BUSINESS_USER,
    BUSINESS_OWNER,
    SYSTEM_ANALYST,
    PRINCIPAL_ENGINEER,
    SOLUTION_ARCHITECT,
    DEVELOPER,
    QA,
    RELEASE_MANAGER,
    /** Read-only access to dashboards and reports (BR-07). */
    MANAGEMENT_VIEWER,
    /** Can manage users and master data (BR-08). */
    ADMIN
}
