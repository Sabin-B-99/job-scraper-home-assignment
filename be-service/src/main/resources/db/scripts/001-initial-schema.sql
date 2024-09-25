CREATE TABLE IF NOT EXISTS jobs (
    id SERIAL PRIMARY KEY,
    job_title VARCHAR(256) NOT NULL,
    company_name VARCHAR(256) NOT NULL,
    location VARCHAR(256) NOT NULL,
    job_description TEXT,
    job_information TEXT,
    job_detail_page_link VARCHAR(256) NOT NULL,
    duplicate_check_hash VARCHAR(256) NOT NULL
);

SELECT COUNT(*) FROM jobs;