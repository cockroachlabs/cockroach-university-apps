CREATE TABLE shipment_addresses (
    address_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id)
);

INSERT INTO shipment_addresses (address_uuid, shipment_id, street, city, state, zip_code, country) VALUES
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '901 First St', 'Logisticsville', 'NY', '10001', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '902 Second Ave', 'Cargo City', 'NJ', '07030', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '903 Third Ln', 'Port Point', 'PA', '19104', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '904 Fourth Rd', 'Transit Town', 'OH', '43215', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '905 Fifth Way', 'Dispatch Depot', 'MI', '48226', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '906 Sixth Blvd', 'Freight Forks', 'IN', '46204', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '907 Seventh Pl', 'Haul Hamlet', 'WI', '53202', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '908 Eighth Ct', 'Delivery Dales', 'MO', '63101', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '909 Ninth Way', 'Shipping Shores', 'KY', '40202', 'USA'),
(gen_random_uuid(), FLOOR(RANDOM() * 150) + 1, '910 Tenth Blvd', 'Logistics Landing', 'TN', '37201', 'USA');

