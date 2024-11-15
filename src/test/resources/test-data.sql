-- Insert into the main user table
INSERT INTO users (id, name, email)
VALUES (1, 'Test User', 'test.user@example.com');

-- Insert roles into the associated collection table
INSERT INTO user_roles (user_id, role)
VALUES (1, 'USER');
