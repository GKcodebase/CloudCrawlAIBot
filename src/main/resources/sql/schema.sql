/*
    Database and table schema
*/
CREATE DATABASE IF NOT EXISTS crawler;
use crawler;

-- Table where crawling configuration will be created
CREATE TABLE crawler_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    data_type VARCHAR(255) NOT NULL,
    tags VARCHAR(255) NOT NULL
);

--- Table to store crawled data
CREATE TABLE crawled_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crawler_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table to store job configuration
CREATE TABLE jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crawler_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    result_file_path VARCHAR(255)
);