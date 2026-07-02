-- V12__add_notifications.sql

CREATE SEQUENCE notifications_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE notifications (
    id BIGINT NOT NULL PRIMARY KEY,
    recipient_user VARCHAR(100) NOT NULL,
    request_id BIGINT,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_notifications_request FOREIGN KEY (request_id) REFERENCES delivery_requests(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_user);
CREATE INDEX idx_notifications_request ON notifications(request_id);
