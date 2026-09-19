-- ================================================================
-- EasyVyaapaar Demo Seed Data
-- Only inserts if products table is empty or product doesn't exist
-- ================================================================

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Rice', 'Grains', 'BAG', 25.000, 5.000, 2200.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Rice');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Sugar', 'Essentials', 'BAG', 10.000, 5.000, 3800.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Sugar');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Cooking Oil', 'Oils', 'LITRE', 30.000, 5.000, 140.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cooking Oil');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Toor Dal', 'Pulses', 'KG', 20.000, 5.000, 160.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Toor Dal');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Moong Dal', 'Pulses', 'KG', 15.000, 4.000, 110.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Moong Dal');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Chana Dal', 'Pulses', 'KG', 18.000, 4.000, 95.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Chana Dal');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Wheat', 'Grains', 'BAG', 15.000, 4.000, 2000.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Wheat');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Salt', 'Essentials', 'KG', 40.000, 10.000, 20.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Salt');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Soap', 'Hygiene', 'PIECE', 50.000, 10.000, 45.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Soap');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Tea', 'Beverages', 'KG', 10.000, 3.000, 250.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Tea');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Milk', 'Dairy', 'LITRE', 15.000, 5.000, 54.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Milk');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Coffee', 'Beverages', 'KG', 8.000, 2.000, 480.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Coffee');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Biscuits', 'Snacks', 'CARTON', 12.000, 3.000, 450.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Biscuits');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Turmeric', 'Spices', 'KG', 8.000, 2.000, 220.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Turmeric');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Chilli Powder', 'Spices', 'KG', 10.000, 3.000, 280.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Chilli Powder');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Onions', 'Vegetables', 'KG', 50.000, 10.000, 35.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Onions');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Potatoes', 'Vegetables', 'KG', 40.000, 10.000, 30.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Potatoes');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Tomatoes', 'Vegetables', 'KG', 25.000, 5.000, 40.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Tomatoes');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Tamarind', 'Spices', 'KG', 12.000, 3.000, 180.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Tamarind');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Mustard Seeds', 'Spices', 'KG', 6.000, 2.000, 120.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mustard Seeds');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Cumin Seeds', 'Spices', 'KG', 5.000, 2.000, 350.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cumin Seeds');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Garlic', 'Vegetables', 'KG', 8.000, 2.000, 240.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Garlic');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Ginger', 'Vegetables', 'KG', 6.000, 2.000, 160.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Ginger');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Ghee', 'Dairy', 'KG', 10.000, 2.000, 650.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Ghee');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Toothpaste', 'Hygiene', 'PIECE', 30.000, 5.000, 75.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Toothpaste');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Shampoo', 'Hygiene', 'PACKET', 40.000, 10.000, 5.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Shampoo');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Eggs', 'Dairy', 'DOZEN', 20.000, 5.000, 72.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Eggs');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Poha', 'Grains', 'KG', 15.000, 3.000, 50.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Poha');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Rava', 'Grains', 'KG', 18.000, 4.000, 45.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Rava');

INSERT INTO products (name, category, unit, current_stock, minimum_stock, price)
SELECT 'Jaggery', 'Essentials', 'KG', 22.000, 5.000, 60.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Jaggery');

-- Sample transactions (demo history)
INSERT INTO inventory_transactions (product_id, operation, quantity, unit, source, original_voice_text, notes)
SELECT p.id, 'ADD', 5.000, 'BAG', 'VOICE', 'Rice 5 bags add cheyyi', 'Demo transaction'
FROM products p WHERE p.name = 'Rice'
AND NOT EXISTS (SELECT 1 FROM inventory_transactions LIMIT 1);

INSERT INTO inventory_transactions (product_id, operation, quantity, unit, source, original_voice_text, notes)
SELECT p.id, 'REMOVE', 2.000, 'BAG', 'VOICE', 'Sugar 2 bags sold', 'Demo transaction'
FROM products p WHERE p.name = 'Sugar'
AND NOT EXISTS (SELECT 1 FROM inventory_transactions WHERE operation = 'REMOVE' LIMIT 1);

-- Default app settings
INSERT INTO app_settings (setting_key, setting_value)
VALUES ('DEFAULT_LANGUAGE', 'en')
ON DUPLICATE KEY UPDATE setting_value = setting_value;

INSERT INTO app_settings (setting_key, setting_value)
VALUES ('AUTO_CONFIRM', 'false')
ON DUPLICATE KEY UPDATE setting_value = setting_value;

INSERT INTO app_settings (setting_key, setting_value)
VALUES ('SHOP_NAME', 'My Shop')
ON DUPLICATE KEY UPDATE setting_value = setting_value;
