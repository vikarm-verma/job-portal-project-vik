-- Development schema for the Job Portal database.
-- Hibernate ddl-auto=update remains enabled for local development, so this
-- script is idempotent and can also initialize a fresh MySQL volume.
-- The Docker entrypoint runs this script against MYSQL_DATABASE. For manual
-- execution, select the target database before loading this file.

CREATE TABLE IF NOT EXISTS users (
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	password VARCHAR(255) NOT NULL,
	role VARCHAR(30) NOT NULL,
	enabled BIT(1) NOT NULL DEFAULT b'1',
	created_at DATETIME(6) NOT NULL,
	updated_at DATETIME(6) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS job_seeker_profiles (
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	phone VARCHAR(30) NOT NULL,
	city VARCHAR(100),
	experience_level VARCHAR(100),
	skills VARCHAR(200),
	summary TEXT,
	profile_complete BIT(1) NOT NULL DEFAULT b'0',
	PRIMARY KEY (id),
	CONSTRAINT uk_job_seeker_profiles_user UNIQUE (user_id),
	CONSTRAINT fk_job_seeker_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recruiter_profiles (
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	company_name VARCHAR(150) NOT NULL,
	company_website VARCHAR(255),
	location VARCHAR(100),
	company_description TEXT,
	PRIMARY KEY (id),
	CONSTRAINT uk_recruiter_profiles_user UNIQUE (user_id),
	CONSTRAINT fk_recruiter_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS jobs (
	id BIGINT NOT NULL AUTO_INCREMENT,
	recruiter_profile_id BIGINT NOT NULL,
	title VARCHAR(150) NOT NULL,
	description TEXT NOT NULL,
	location VARCHAR(150) NOT NULL,
	employment_type VARCHAR(30) NOT NULL,
	salary_min DOUBLE NOT NULL,
	salary_max DOUBLE,
	experience_level VARCHAR(100),
	required_skills VARCHAR(255) NOT NULL,
	status VARCHAR(30) NOT NULL,
	created_at DATETIME(6) NOT NULL,
	updated_at DATETIME(6) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_jobs_recruiter_profile FOREIGN KEY (recruiter_profile_id) REFERENCES recruiter_profiles (id),
	INDEX idx_jobs_recruiter_profile (recruiter_profile_id),
	INDEX idx_jobs_title (title),
	INDEX idx_jobs_required_skills (required_skills),
	INDEX idx_jobs_location (location),
	INDEX idx_jobs_employment_type (employment_type),
	INDEX idx_jobs_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS applications (
	id BIGINT NOT NULL AUTO_INCREMENT,
	job_id BIGINT NOT NULL,
	job_seeker_profile_id BIGINT NOT NULL,
	status VARCHAR(30) NOT NULL,
	applied_at DATETIME(6) NOT NULL,
	updated_at DATETIME(6),
	PRIMARY KEY (id),
	CONSTRAINT uk_applications_job_job_seeker UNIQUE (job_id, job_seeker_profile_id),
	CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs (id),
	CONSTRAINT fk_applications_job_seeker FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profiles (id),
	INDEX idx_applications_job (job_id),
	INDEX idx_applications_job_seeker (job_seeker_profile_id),
	INDEX idx_applications_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS resumes (
	id BIGINT NOT NULL AUTO_INCREMENT,
	job_seeker_profile_id BIGINT NOT NULL,
	file_name VARCHAR(255) NOT NULL,
	file_path VARCHAR(500) NOT NULL,
	content_type VARCHAR(100) NOT NULL,
	uploaded_at DATETIME(6) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT uk_resumes_job_seeker_profile UNIQUE (job_seeker_profile_id),
	CONSTRAINT fk_resumes_job_seeker_profile FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profiles (id)
) ENGINE=InnoDB;
