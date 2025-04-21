INSERT INTO omnicorp.customers (customer_id, first_name, last_name, email, phone, address, customer_uuid)
SELECT customer_id, first_name, last_name, email, phone, address, gen_random_uuid()
FROM crm.customers;

SELECT * FROM customers;


INSERT INTO omnicorp.orders (order_id, customer_id, order_date, total_amount, order_uuid, customer_uuid)
SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, gen_random_uuid(), c_target.customer_uuid
FROM crm.orders o
JOIN crm.customers c_source ON o.customer_id = c_source.customer_id
JOIN omnicorp.customers c_target ON c_source.customer_id = c_target.customer_id;

SELECT * FROM orders;


INSERT INTO omnicorp.products (product_id, product_name, description, price, product_uuid)
SELECT product_id, product_name, description, price, gen_random_uuid()
FROM logistics.products;

SELECT * FROM products;


INSERT INTO omnicorp.inventory (inventory_id, product_id, quantity, location, inventory_uuid, product_uuid)
SELECT i.inventory_id, i.product_id, i.quantity, i.location, gen_random_uuid(), p_target.product_uuid
FROM logistics.inventory i
JOIN logistics.products p_source ON i.product_id = p_source.product_id
JOIN omnicorp.products p_target ON p_source.product_id = p_target.product_id;

SELECT * FROM inventory;


INSERT INTO omnicorp.shipments (shipment_id, order_id, product_id, quantity, shipment_date, shipment_uuid, order_uuid, product_uuid)
SELECT s.shipment_id, s.order_id, s.product_id, s.quantity, s.shipment_date, gen_random_uuid(),
       o_target.order_uuid, p_target.product_uuid
FROM logistics.shipments s
LEFT JOIN crm.orders o_source ON s.order_id = o_source.order_id
LEFT JOIN omnicorp.orders o_target ON o_source.order_id = o_target.order_id
LEFT JOIN logistics.products p_source ON s.product_id = p_source.product_id
LEFT JOIN omnicorp.products p_target ON p_source.product_id = p_target.product_id;

SELECT * FROM shipments;