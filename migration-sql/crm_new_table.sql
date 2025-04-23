CREATE TABLE crm.addresses (
    address_uuid BINARY(16) PRIMARY KEY, -- UUID in MySQL is often stored as BINARY(16)
    customer_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES crm.customers(customer_id)
);



INSERT INTO crm.addresses (address_uuid, customer_id, street, city, state, zip_code, country) VALUES
(UUID_TO_BIN(UUID()), 1, '123 Main St', 'Anytown', 'CA', '90210', 'USA'),
(UUID_TO_BIN(UUID()), 2, '456 Oak Ave', 'Springfield', 'IL', '62704', 'USA'),
(UUID_TO_BIN(UUID()), 3, '789 Pine Ln', 'Seattle', 'WA', '98101', 'USA'),
(UUID_TO_BIN(UUID()), 4, '101 Elm Rd', 'Miami', 'FL', '33101', 'USA'),
(UUID_TO_BIN(UUID()), 5, '222 Willow Dr', 'Austin', 'TX', '78701', 'USA'),
(UUID_TO_BIN(UUID()), 6, '333 Maple Ct', 'Boston', 'MA', '02108', 'USA'),
(UUID_TO_BIN(UUID()), 7, '444 Cherry Blvd', 'Denver', 'CO', '80202', 'USA'),
(UUID_TO_BIN(UUID()), 8, '555 Spruce Way', 'Atlanta', 'GA', '30303', 'USA'),
(UUID_TO_BIN(UUID()), 9, '666 Birch Pl', 'Chicago', 'IL', '60601', 'USA'),
(UUID_TO_BIN(UUID()), 10, '777 Cedar Blvd', 'Phoenix', 'AZ', '85001', 'USA');
