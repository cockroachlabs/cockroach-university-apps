// Copyright 2025 The Cockroach Authors.
//
// Use of this software is governed by the CockroachDB Software License
// included in the /LICENSE file.

package db

import (
	"database/sql"
	"fmt"
	utils "github.com/cockroachlabs/cockroach-university-apps/migration-mysql-app/lib"
	_ "github.com/go-sql-driver/mysql"
	"github.com/joho/godotenv"
	"log"
	"os"
	"time"
)

func getDSN() string {
	err := godotenv.Load()
	if err != nil {
		log.Println("No .env file found, using system env.")
	}

	host := os.Getenv("DB_HOST")
	port := os.Getenv("DB_PORT")
	user := os.Getenv("DB_USER")
	pass := os.Getenv("DB_PASSWORD")
	db := os.Getenv("DB_NAME")

	if host == "" {
		host = "127.0.0.1"
	}
	if port == "" {
		port = "3306"
	}

	if db == "" {
		db = "crm"
	}

	if user == "" || pass == "" {
		log.Fatal("Missing DB_USER or DB_PASSWORD in .env")
	}

	return fmt.Sprintf("%s:%s@tcp(%s:%s)/%s", user, pass, host, port, db)
}

func insertOrder(db *sql.DB) bool {
	query := `INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, ?, ?)`
	_, err := db.Exec(query, utils.GenerateRandomCustomerID(), utils.GenerateRandomDate(), utils.GenerateRandomAmount())
	if err != nil {
		log.Println("Insert error:", err)
		return false
	}
	return true
}

func AddRecords(numOrders *int, sleepMs *int) {
	dsn := getDSN()
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal("DB connection failed:", err)
	}
	defer func(db *sql.DB) {
		err := db.Close()
		if err != nil {
			log.Fatal("DB close failed:", err)
		}
	}(db)

	err = db.Ping()
	if err != nil {
		log.Fatal("DB ping failed:", err)
	}

	fmt.Printf("Attempting to insert %d random orders...\n", *numOrders)
	if *sleepMs > 0 {
		fmt.Printf("Sleeping %dms between inserts.\n", *sleepMs)
	}

	success, failed := 0, 0
	for i := 1; i <= *numOrders; i++ {
		fmt.Printf("Inserting order %d/%d...\n", i, *numOrders)
		if insertOrder(db) {
			fmt.Println(" -> Success")
			success++
		} else {
			fmt.Println(" -> Failed")
			failed++
		}
		if *sleepMs > 0 && i < *numOrders {
			time.Sleep(time.Duration(*sleepMs) * time.Millisecond)
		}
	}

	fmt.Println("\n--- Insertion Summary ---")
	fmt.Println("Successfully inserted:", success)
	fmt.Println("Failed attempts:      ", failed)
}
