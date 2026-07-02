package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.NotificationDto;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.Notification;
import com.deliveryworkbench.entity.NotificationType;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.NotificationRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(String recipientUser, DeliveryRequest request, NotificationType type, String title, String message) {
        if (recipientUser == null || recipientUser.isBlank()) {
            log.warn("Cannot create notification for empty recipient. Type: {}", type);
            return;
        }

        Notification notification = Notification.builder()
                .recipientUser(recipientUser)
                .request(request)
                .notificationType(type)
                .title(title)
                .message(message)
                .build();
                
        notificationRepository.save(notification);
        log.info("Created notification '{}' for {}", title, recipientUser);
        
        // Prepare integration point (e.g. Teams/Email) - To be implemented in Phase 14
        // integrationService.send(recipientUser, title, message);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications() {
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        
        // Recipient can match either username or ROLE_XXX
        List<String> identities = List.of(
            username != null ? username : "", 
            role != null ? "ROLE_" + role : ""
        );

        return notificationRepository.findByRecipientUserInOrderByCreatedAtDesc(identities)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        
        List<String> identities = List.of(
            username != null ? username : "", 
            role != null ? "ROLE_" + role : ""
        );
        
        return notificationRepository.countByRecipientUserInAndReadFalse(identities);
    }

    @Transactional
    public NotificationDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
                
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(OffsetDateTime.now());
            notification = notificationRepository.save(notification);
        }
        
        return mapToDto(notification);
    }

    private NotificationDto mapToDto(Notification notif) {
        return NotificationDto.builder()
                .id(notif.getId())
                .recipientUser(notif.getRecipientUser())
                .requestId(notif.getRequest() != null ? notif.getRequest().getId() : null)
                .requestCode(notif.getRequest() != null ? notif.getRequest().getRequestCode() : null)
                .requestTitle(notif.getRequest() != null ? notif.getRequest().getTitle() : null)
                .notificationType(notif.getNotificationType())
                .title(notif.getTitle())
                .message(notif.getMessage())
                .read(notif.isRead())
                .createdAt(notif.getCreatedAt())
                .readAt(notif.getReadAt())
                .build();
    }
}
