CREATE SEQUENCE customers_seq;

CREATE SEQUENCE orders_seq;

CREATE TABLE customers (
	customer_id INT8 NOT NULL DEFAULT nextval('customers_seq'::REGCLASS),
	first_name VARCHAR(255) NULL,
	last_name VARCHAR(255) NULL,
	email VARCHAR(255) NULL,
	phone VARCHAR(20) NULL,
	address VARCHAR(255) NULL,
	CONSTRAINT customers_pkey PRIMARY KEY (customer_id ASC),
	UNIQUE INDEX customers_email_key (email ASC)
);

CREATE TABLE orders (
	order_id INT8 NOT NULL DEFAULT nextval('orders_seq'::REGCLASS),
	customer_id INT4 NULL,
	order_date DATE NULL,
	total_amount DECIMAL(10,2) NULL,
	CONSTRAINT orders_pkey PRIMARY KEY (order_id ASC),
	CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE products (
	product_id INT8 NOT NULL DEFAULT unique_rowid(),
	product_name VARCHAR(255) NULL,
	description STRING NULL,
	price DECIMAL(10,2) NULL,
	CONSTRAINT products_pkey PRIMARY KEY (product_id ASC)
);

CREATE TABLE inventory (
	inventory_id INT8 NOT NULL DEFAULT unique_rowid(),
	product_id INT8 NULL,
	quantity INT8 NULL,
	location VARCHAR(255) NULL,
	CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id ASC),
	CONSTRAINT inventory_product_id_fkey FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE shipments (
	shipment_id INT8 NOT NULL DEFAULT unique_rowid(),
	order_id INT8 NULL,
	product_id INT8 NULL,
	quantity INT8 NULL,
	shipment_date DATE NULL,
	CONSTRAINT shipments_pkey PRIMARY KEY (shipment_id ASC),
	CONSTRAINT shipments_product_id_fkey FOREIGN KEY (product_id) REFERENCES products(product_id)
);