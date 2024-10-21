/*
    Database and table schema
*/
DROP DATABASE IF EXISTS crawler;
CREATE DATABASE IF NOT EXISTS crawler;
use crawler;

-- Table where crawling configuration will be created
CREATE TABLE crawler_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    data_type VARCHAR(255) NOT NULL,
    tags VARCHAR(255) NOT NULL
);


-- Table to store Configuration RUN History
CREATE TABLE job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    end_time DATE,
    message  VARCHAR(255),
	start_time DATE,
    status VARCHAR(20),
    configuration_id int,
    FOREIGN KEY (configuration_id) REFERENCES crawled_data(id)
);


SELECT * FROM crawler_configurations;
SELECT * FROM  job_history;