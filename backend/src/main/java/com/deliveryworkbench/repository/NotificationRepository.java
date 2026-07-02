package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserInOrderByCreatedAtDesc(List<String> recipients);
    long countByRecipientUserInAndReadFalse(List<String> recipients);
}
