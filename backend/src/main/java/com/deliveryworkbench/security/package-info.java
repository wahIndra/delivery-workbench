/**
 * Security — JWT filter, UserDetailsService, RBAC configuration.
 * All role enforcement happens at service layer using @PreAuthorize.
 * AI may never bypass security or access production credentials (SG-09).
 */
@NonNullApi
package com.deliveryworkbench.security;

import org.springframework.lang.NonNullApi;
