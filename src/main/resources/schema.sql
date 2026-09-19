-- ================================================================
-- EasyVyaapaar Database Schema
-- MySQL 8.0+
-- ================================================================

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    category        VARCHAR(100)    NULL,
    unit            VARCHAR(50)     NOT NULL COMMENT 'Normalized unit: BAG, KG, LITRE, PIECE, etc.',
    current_stock   DECIMAL(12, 3)  NOT NULL DEFAULT 0.000,
    minimum_stock   DECIMAL(12, 3)  NOT NULL DEFAULT 0.000 COMMENT 'Low-stock threshold',
    price           DECIMAL(12, 2)  NULL COMMENT 'Price per unit',
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_product_name (name),
    INDEX idx_active (active),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Inventory Transactions table
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    product_id          BIGINT          NOT NULL,
    operation           ENUM('ADD','REMOVE') NOT NULL,
    quantity            DECIMAL(12, 3)  NOT NULL,
    unit                VARCHAR(50)     NOT NULL,
    price               DECIMAL(12, 2)  NULL,
    source              ENUM('VOICE','MANUAL') NOT NULL DEFAULT 'MANUAL',
    original_voice_text TEXT            NULL COMMENT 'Raw speech text from user',
    notes               VARCHAR(255)    NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_product_id (product_id),
    INDEX idx_operation (operation),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_transaction_product
        FOREIGN KEY (product_id) REFERENCES products (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- App Settings table (for future extensibility)
CREATE TABLE IF NOT EXISTS app_settings (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    setting_key     VARCHAR(100)    NOT NULL,
    setting_value   TEXT            NULL,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
