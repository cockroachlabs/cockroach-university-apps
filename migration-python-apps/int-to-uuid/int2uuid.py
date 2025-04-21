import psycopg2
import uuid

def execute_sql(conn, sql):
    """Executes a SQL statement."""
    with conn.cursor() as cur:
        cur.execute(sql)

def get_foreign_key_info(conn, table_name):
    """Gets information about foreign keys in a table."""
    sql = """
        SELECT
            kcu.column_name AS foreign_key_column,
            tc.table_name AS foreign_key_table,
            ccu.table_name AS referenced_table,
            ccu.column_name AS referenced_column,
            tc.constraint_name AS fk_constraint_name
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

    # 1. Add UUID column with default
    execute_sql(conn, f"ALTER TABLE {table_name} ADD COLUMN {table_name}_uuid UUID DEFAULT gen_random_uuid();")
    conn.commit()

    # 2. Set UUID column NOT NULL
    execute_sql(conn, f"ALTER TABLE {table_name} ALTER COLUMN {table_name}_uuid SET NOT NULL;")
    conn.commit()

    # 3. Add unique constraint (optional but recommended)
    execute_sql(conn, f"ALTER TABLE {table_name} ADD CONSTRAINT unique_{table_name}_uuid UNIQUE ({table_name}_uuid);")
    conn.commit()

    # 4. Get foreign key information
    foreign_keys = get_foreign_key_info(conn, table_name)

    # 5. Drop existing primary key (in a separate transaction)
    execute_sql(conn, f"ALTER TABLE {table_name} DROP CONSTRAINT {table_name}_pkey;")
    conn.commit()

    # 6. Set UUID column as primary key
    execute_sql(conn, f"ALTER TABLE {table_name} ADD CONSTRAINT {table_name}_pkey PRIMARY KEY ({table_name}_uuid);")
    conn.commit()

    # 7. Update foreign keys in other tables
    for fk in foreign_keys:
        fk_table = fk[1]
        fk_column = fk[0]
        referenced_table = fk[2]
        referenced_column = fk[3]
        fk_constraint_name = fk[4]

        # Add a new UUID foreign key column in the referencing table
        execute_sql(conn, f"ALTER TABLE {fk_table} ADD COLUMN {fk_column}_uuid UUID;")
        conn.commit()

        # Populate the new UUID foreign key column
        execute_sql(conn, f"UPDATE {fk_table} SET {fk_column}_uuid = (SELECT {referenced_table}_uuid FROM {referenced_table} WHERE {referenced_column} = {fk_column});")
        conn.commit()

        # Set the new UUID foreign key column NOT NULL
        execute_sql(conn, f"ALTER TABLE {fk_table} ALTER COLUMN {fk_column}_uuid SET NOT NULL;")
        conn.commit()

        # Drop the old foreign key constraint
        execute_sql(conn, f"ALTER TABLE {fk_table} DROP CONSTRAINT {fk_constraint_name};")
        conn.commit()

        # Add the new foreign key constraint
        new_fk_constraint_name = f"fk_{fk_table}_{fk_column}_uuid_{referenced_table}_uuid"
        execute_sql(conn, f"ALTER TABLE {fk_table} ADD CONSTRAINT {new_fk_constraint_name} FOREIGN KEY ({fk_column}_uuid) REFERENCES {referenced_table} ({referenced_table}_uuid);")
        conn.commit()

        # (Optional) Drop the old integer foreign key column
        execute_sql(conn, f"ALTER TABLE {fk_table} DROP COLUMN {fk_column};")
        conn.commit()

def main():
    conn = psycopg2.connect(
        database="omnicorp",
        user="root",
        password="",
        host="localhost",
        port="26257"
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