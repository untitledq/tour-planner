-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_users_username
    ON users(username);

-- LOGIN TOKENS
CREATE TABLE IF NOT EXISTS login_tokens (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_login_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_login_tokens_user_id
    ON login_tokens(user_id);

-- SESSIONS
CREATE TABLE IF NOT EXISTS sessions (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    jti UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    token TEXT NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_sessions_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_sessions_jti ON sessions(jti);
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id);

-- TEST TABLE
CREATE TABLE IF NOT EXISTS text (
                                    id BIGSERIAL PRIMARY KEY,
                                    content TEXT NOT NULL
);

INSERT INTO text (content)
VALUES ('hello world')
    ON CONFLICT DO NOTHING;