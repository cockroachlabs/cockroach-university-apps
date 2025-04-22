CREATE TABLE customers (
    customer_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id INT8 NULL,
    first_name VARCHAR(255) NULL,
    last_name VARCHAR(255) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(20) NULL,
    address VARCHAR(255) NULL,
    UNIQUE INDEX customers_email_key (email ASC)
);

CREATE TABLE orders (
    order_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id INT8 NULL,
    customer_id INT8 NULL,
    customer_uuid UUID NULL REFERENCES customers (customer_uuid),
    order_date DATE NULL,
    total_amount DECIMAL(10,2) NULL,
    CONSTRAINT orders_customer_uuid_fkey FOREIGN KEY (customer_uuid) REFERENCES customers(customer_uuid),
    CONSTRAINT chk_order_total_non_negative CHECK (total_amount >= 0),
);

CREATE TABLE products (
    product_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id INT8 NOT NULL,
    product_name VARCHAR(255) NULL,
    description STRING NULL,
    price DECIMAL(10,2) NULL
);

CREATE TABLE inventory (
    inventory_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_id INT8 NOT NULL,
    product_id INT8 NULL,
    product_uuid UUID NULL REFERENCES products (product_uuid),
    quantity INT8 NULL,
    location VARCHAR(255) NULL,
    CONSTRAINT inventory_product_uuid_fkey FOREIGN KEY (product_uuid) REFERENCES products(product_uuid)
);

CREATE TABLE shipments (
    shipment_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id INT8 NOT NULL,
    order_id INT8 NULL,
    order_uuid UUID NULL REFERENCES orders (order_uuid),
    product_id INT8 NULL,
    product_uuid UUID NULL REFERENCES products (product_uuid),
    quantity INT8 NULL,
    shipment_date DATE NULL,
    CONSTRAINT shipments_product_uuid_fkey FOREIGN KEY (product_uuid) REFERENCES products(product_uuid)
);