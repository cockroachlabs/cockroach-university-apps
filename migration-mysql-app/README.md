# MySQL App

This app adds rando orders to the CRM database. This app is part of the Migration Course.


## How to run it

**Pre-Requirements**
 - MySQL up and running with the Orders schema in place.
   ```sql
    CREATE TABLE orders (
        order_id INT PRIMARY KEY AUTO_INCREMENT,
        customer_id INT,
        order_date DATE,
        total_amount DECIMAL(10, 2),
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    )
    COLLATE='utf8_unicode_ci'
    ENGINE=InnoDB;
   ```
   This is only the `orders` tables that has a constraint in the `customers` table.

**Steps**

1. Clone this repo
2. Download dependencies:

    ```shell
    go download
    ```

3. Add the necessary environment variables:

   | Variable |Default|
   |----------|-------|
   | DB_HOST  |127.0.0.1|
   |DB_PORT | 3306 |
   |DB_USER| |
   |DB_PASSWORD| |
   |DB_NAME | crm |

4.  Build, this will generate `migration-mysql-app` binary.
   ```shell
   go build
   ```                                                     

5. Usage:
   ```shell
   ./migration-mysql-app -num-orders=20 -sleep-ms=1000
   ```            
   Flags:
   - `-num-orders`, number of orders to insert in the `orders` table.
   - `-sleep-ms`, the sleep time between order inserts in milliseconds.




# Linux Compilation

```shell
GOOS=linux GOARCH=amd64 go build
```