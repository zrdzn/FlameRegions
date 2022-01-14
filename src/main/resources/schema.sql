CREATE TABLE IF NOT EXISTS explored_regions (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    region_name VARCHAR(128) NOT NULL UNIQUE KEY,
    explorer_uuid VARCHAR(36),
    explorer_name VARCHAR(18)
);