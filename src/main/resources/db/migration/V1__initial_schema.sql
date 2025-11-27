-- Initial database schema for Math Learning Platform
-- Users table (students only for MVP)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255), -- nullable for OAuth users
    full_name VARCHAR(255) NOT NULL,
    grade_level INTEGER NOT NULL CHECK (grade_level >= 6 AND grade_level <= 10),
    profile_image_url TEXT,
    auth_provider VARCHAR(50) DEFAULT 'EMAIL', -- EMAIL, GOOGLE
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Streaks and XP tracking
CREATE TABLE user_progress (
    user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_xp INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    last_activity_date DATE,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Questions bank (hardcoded 20 for MVP, expandable)
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    subject VARCHAR(50) DEFAULT 'MATH',
    topic VARCHAR(100) NOT NULL, -- fractions, algebra, geometry, etc.
    grade_level INTEGER NOT NULL CHECK (grade_level >= 6 AND grade_level <= 10),
    question_text TEXT NOT NULL,
    question_image_url TEXT, -- optional
    correct_answer TEXT NOT NULL, -- for validation reference
    solution_steps JSONB, -- array of steps
    difficulty VARCHAR(20) CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Student submissions
CREATE TABLE submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    original_image_url TEXT NOT NULL,
    annotated_image_url TEXT, -- with red/green marks
    ai_score DECIMAL(4,2), -- out of 10
    is_correct BOOLEAN,
    ai_feedback TEXT,
    correct_steps JSONB, -- solution from AI
    topic_tags JSONB, -- extracted topics
    processing_time_ms INTEGER,
    ai_provider VARCHAR(50), -- GPT4O, CLAUDE, etc.
    created_at TIMESTAMP DEFAULT NOW()
);

-- Wrong answer bank (mistakes notebook)
CREATE TABLE mistake_notebook (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    submission_id BIGINT NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    reviewed BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_submissions_user_id ON submissions(user_id);
CREATE INDEX idx_submissions_created_at ON submissions(created_at DESC);
CREATE INDEX idx_questions_grade_topic ON questions(grade_level, topic);
CREATE INDEX idx_mistake_notebook_user ON mistake_notebook(user_id, reviewed);
CREATE INDEX idx_users_email ON users(email);

-- Create trigger for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_progress_updated_at BEFORE UPDATE ON user_progress
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
