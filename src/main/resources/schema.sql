-- ================================================================
-- EasyVyaapaar Database Schema (ANSI SQL Compatible: MySQL & H2)
-- ================================================================

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    category        VARCHAR(100)    NULL,
    unit            VARCHAR(50)     NOT NULL,
    current_stock   DECIMAL(12, 3)  NOT NULL DEFAULT 0.000,
    minimum_stock   DECIMAL(12, 3)  NOT NULL DEFAULT 0.000,
    price           DECIMAL(12, 2)  NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_product_name UNIQUE (name)
);

-- Inventory Transactions table
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    product_id          BIGINT          NOT NULL,
    operation           VARCHAR(20)     NOT NULL,
    quantity            DECIMAL(12, 3)  NOT NULL,
    unit                VARCHAR(50)     NOT NULL,
    price               DECIMAL(12, 2)  NULL,
    source              VARCHAR(20)     NOT NULL DEFAULT 'MANUAL',
    original_voice_text TEXT            NULL,
    notes               VARCHAR(255)    NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- App Settings table
CREATE TABLE IF NOT EXISTS app_settings (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    setting_key     VARCHAR(100)    NOT NULL,
    setting_value   TEXT            NULL,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_setting_key UNIQUE (setting_key)
);
