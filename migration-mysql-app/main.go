// Copyright 2025 The Cockroach Authors.
//
// Use of this software is governed by the CockroachDB Software License
// included in the /LICENSE file.

package main

import (
	"flag"
	mysql "github.com/cockroachlabs/cockroach-university-apps/migration-mysql-app/db"
	"math/rand"
	"time"
)

func main() {
	rand.New(rand.NewSource(time.Now().UnixNano()))

	numOrders := flag.Int("num-orders", 10, "Number of orders to insert")
	sleepMs := flag.Int("sleep-ms", 0, "Sleep time between inserts in milliseconds")
	flag.Parse()

	mysql.AddRecords(numOrders, sleepMs)
}
