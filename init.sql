CREATE TABLE IF NOT EXISTS user_account (
    uuid UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    verified BOOLEAN DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_user_account_username ON user_account(username);

CREATE TABLE login_token (
    id SERIAL PRIMARY KEY,
    user_uuid UUID NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_uuid) REFERENCES user_account(uuid) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_login_token_user_uuid ON login_token(user_uuid);




CREATE TABLE IF NOT EXISTS text (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL
);

INSERT INTO text (content) VALUES ('hello world') ON CONFLICT DO NOTHING;