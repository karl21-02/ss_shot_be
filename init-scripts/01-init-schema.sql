-- SS-Shot Database Schema

-- users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    profile_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (provider, provider_id)
);

CREATE INDEX idx_users_email ON users(email);

-- screenshot_metadata 테이블
CREATE TABLE screenshot_metadata (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    local_id VARCHAR(255) NOT NULL,
    full_text TEXT,
    ocr_json JSONB,
    category VARCHAR(50) DEFAULT 'OTHER',
    captured_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, local_id)
);

-- Full-Text Search 인덱스 (Korean/Simple)
CREATE INDEX idx_screenshot_fulltext
ON screenshot_metadata USING GIN (to_tsvector('simple', COALESCE(full_text, '')));

-- 복합 인덱스
CREATE INDEX idx_user_category_captured
ON screenshot_metadata (user_id, category, captured_at DESC);

CREATE INDEX idx_user_deleted
ON screenshot_metadata (user_id, is_deleted);

-- refresh_tokens 테이블 (JWT Refresh Token 저장용)
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);
