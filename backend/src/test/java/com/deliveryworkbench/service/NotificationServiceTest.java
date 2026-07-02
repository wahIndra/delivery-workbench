package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.NotificationDto;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.Notification;
import com.deliveryworkbench.entity.NotificationType;
import com.deliveryworkbench.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test.user", "password", List.of(new SimpleGrantedAuthority("ROLE_SYSTEM_ANALYST")))
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void createNotification() {
        DeliveryRequest req = DeliveryRequest.builder().id(1L).requestCode("REQ-1").build();

        notificationService.createNotification("business.owner", req, NotificationType.CLARIFICATION_REQUIRED, "Title", "Message");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getMyNotifications() {
        Notification n = Notification.builder()
                .id(1L)
                .recipientUser("test.user")
                .notificationType(NotificationType.STATUS_CHANGED)
                .title("Title")
                .message("Message")
                .createdAt(OffsetDateTime.now())
                .build();

        when(notificationRepository.findByRecipientUserInOrderByCreatedAtDesc(anyList()))
                .thenReturn(List.of(n));

        List<NotificationDto> result = notificationService.getMyNotifications();

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void getUnreadCount() {
        when(notificationRepository.countByRecipientUserInAndReadFalse(anyList())).thenReturn(5L);
        long count = notificationService.getUnreadCount();
        assertEquals(5L, count);
    }

    @Test
    void markAsRead() {
        Notification n = Notification.builder()
                .id(1L)
                .recipientUser("test.user")
                .notificationType(NotificationType.STATUS_CHANGED)
                .title("Title")
                .message("Message")
                .read(false)
                .createdAt(OffsetDateTime.now())
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        NotificationDto result = notificationService.markAsRead(1L);

        assertTrue(result.isRead());
        assertNotNull(result.getReadAt());
        verify(notificationRepository, times(1)).save(n);
    }
}
