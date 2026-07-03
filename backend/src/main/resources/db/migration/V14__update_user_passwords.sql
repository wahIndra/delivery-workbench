-- Update placeholder hashes to a valid BCrypt hash for 'password'
UPDATE app_users 
SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE password = '$2a$10$PLACEHOLDER_HASH';
