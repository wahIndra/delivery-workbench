package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String recipientUser;
    private Long requestId;
    private String requestCode;
    private String requestTitle;
    private NotificationType notificationType;
    private String title;
    private String message;
    private boolean read;
    private OffsetDateTime createdAt;
    private OffsetDateTime readAt;
}
