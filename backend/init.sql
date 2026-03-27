-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_users_created_at
    ON users(created_at);



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

CREATE UNIQUE INDEX IF NOT EXISTS idx_login_tokens_token
    ON login_tokens(token);


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
CREATE INDEX IF NOT EXISTS idx_sessions_token ON sessions(token);
CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_sessions_user_active ON sessions(user_id, revoked);




-- TOURS
CREATE TABLE IF NOT EXISTS tours (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by UUID REFERENCES users(id),
    name TEXT,
    description TEXT,
    start_name TEXT,
    start_lat DOUBLE PRECISION,
    start_lng DOUBLE PRECISION,
    end_name TEXT,
    end_lat DOUBLE PRECISION,
    end_lng DOUBLE PRECISION
    );

CREATE INDEX IF NOT EXISTS idx_tours_created_by ON tours(created_by);
CREATE INDEX IF NOT EXISTS idx_tours_name ON tours(name);
CREATE INDEX IF NOT EXISTS idx_tours_start_coords ON tours(start_lat, start_lng);
CREATE INDEX IF NOT EXISTS idx_tours_end_coords ON tours(end_lat, end_lng);

-- ROUTES
CREATE TABLE IF NOT EXISTS routes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tour_id UUID UNIQUE REFERENCES tours(id),
    transport_type TEXT,
    distance INTEGER,
    duration INTEGER,
    coordinates JSONB
    );

CREATE TABLE IF NOT EXISTS tour_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tour_id UUID REFERENCES tours(id),
    created_by UUID REFERENCES users(id),
    date_time TIMESTAMP,
    rating INTEGER CHECK (rating >= 0 AND rating <= 5),
    comment TEXT
    );

CREATE INDEX IF NOT EXISTS idx_tour_logs_tour_id ON tour_logs(tour_id);
CREATE INDEX IF NOT EXISTS idx_tour_logs_created_by ON tour_logs(created_by);
CREATE INDEX IF NOT EXISTS idx_tour_logs_date_time ON tour_logs(date_time);

-- FAVORITE_TOURS
CREATE TABLE IF NOT EXISTS user_favorite_tours (
    user_id UUID REFERENCES users(id),
    tour_id UUID REFERENCES tours(id),
    PRIMARY KEY (user_id, tour_id)
    );

CREATE INDEX IF NOT EXISTS idx_user_fav_tour_id ON user_favorite_tours(tour_id);