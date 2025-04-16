import psycopg2
import uuid

def execute_sql(conn, sql):
    """Executes a SQL statement."""
    with conn.cursor() as cur:
        cur.execute(sql)

def get_foreign_key_info(conn, table_name):
    """Gets information about foreign keys in a table."""
    # SQL query to get foreign key details (table, column, referenced table, referenced column)
    sql = """
        SELECT
            kcu.column_name AS foreign_key_column,
            tc.table_name AS foreign_key_table,
            ccu.table_name AS referenced_table,
            ccu.column_name AS referenced_column
        FROM
            information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
              ON tc.constraint_name = kcu.constraint_name
              AND tc.table_schema = kcu.table_schema
            JOIN information_schema.constraint_column_usage AS ccu
              ON ccu.constraint_name = tc.constraint_name
              AND ccu.table_schema = tc.table_schema
        WHERE
            tc.constraint_type = 'FOREIGN KEY'
            AND tc.table_name = %s;
    """
    with conn.cursor() as cur:
        cur.execute(sql, (table_name,))
        return cur.fetchall()

def process_table(conn, table_name, id_column_name):
    """Processes a single table to migrate primary key to UUID."""

    # 1. Add UUID column
    execute_sql(conn, f"ALTER TABLE {table_name} ADD COLUMN {table_name}_uuid UUID;")

    # 2. Populate UUID column
    execute_sql(conn, f"UPDATE {table_name} SET {table_name}_uuid = gen_random_uuid();")

    # 3. Set UUID column NOT NULL
    execute_sql(conn, f"ALTER TABLE {table_name} ALTER COLUMN {table_name}_uuid SET NOT NULL;")

    # 4. Add unique constraint (optional)
    execute_sql(conn, f"ALTER TABLE {table_name} ADD CONSTRAINT unique_{table_name}_uuid UNIQUE ({table_name}_uuid);")

    # 5. Get foreign key information
    foreign_keys = get_foreign_key_info(conn, table_name)

    # 6. Drop existing primary key
    execute_sql(conn, f"ALTER TABLE {table_name} DROP CONSTRAINT {table_name}_pkey;")  # Adjust constraint name if needed

    # 7. Set UUID column as primary key
    execute_sql(conn, f"ALTER TABLE {table_name} ADD CONSTRAINT {table_name}_pkey PRIMARY KEY ({table_name}_uuid);")

    # 8. Update foreign keys in other tables
    for fk in foreign_keys:
        fk_table = fk[1]
        fk_column = fk[0]
        referenced_table = fk[2]
        referenced_column = fk[3]

        # Add a new UUID foreign key column in the referencing table
        execute_sql(conn, f"ALTER TABLE {fk_table} ADD COLUMN {fk_column}_uuid UUID;")

        # Populate the new UUID foreign key column
        execute_sql(conn, f"UPDATE {fk_table} t1 SET {fk_column}_uuid = (SELECT t2.{referenced_table}_uuid FROM {referenced_table} t2 WHERE t2.{referenced_column} = t1.{fk_column});")

        # Set the new UUID foreign key column NOT NULL
        execute_sql(conn, f"ALTER TABLE {fk_table} ALTER COLUMN {fk_column}_uuid SET NOT NULL;")

        # Drop the old foreign key constraint
        execute_sql(conn, f"ALTER TABLE {fk_table} DROP CONSTRAINT ...;")  # You'll need to dynamically construct the constraint name

        # Add the new foreign key constraint
        execute_sql(conn, f"ALTER TABLE {fk_table} ADD CONSTRAINT ... FOREIGN KEY ({fk_column}_uuid) REFERENCES {referenced_table} ({referenced_table}_uuid);")  # You'll need to dynamically construct the constraint name

        # (Optional) Drop the old integer foreign key column
        execute_sql(conn, f"ALTER TABLE {fk_table} DROP COLUMN {fk_column};")

def main():
    conn = psycopg2.connect(
        database="your_database",
        user="your_user",
        password="your_password",
        host="your_host",
        port="your_port"
    )

    tables_to_process = [
        ("customers", "customer_id"),
        ("orders", "order_id"),
        ("products", "product_id"),
        ("inventory", "inventory_id"),
        ("shipments", "shipment_id")
    ]

    with conn:  # Use 'with' for automatic transaction management
        for table, id_column in tables_to_process:
            process_table(conn, table, id_column)

    conn.close()

if __name__ == "__main__":
    main()