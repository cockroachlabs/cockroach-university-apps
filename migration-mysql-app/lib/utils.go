// Copyright 2025 The Cockroach Authors.
//
// Use of this software is governed by the CockroachDB Software License
// included in the /LICENSE file.

package lib

import (
	"math/rand"
	"time"
)

func GenerateRandomDate() string {
	start := time.Now().AddDate(-1, 0, 0).Unix()
	end := time.Now().Unix()
	random := rand.Int63n(end-start) + start
	return time.Unix(random, 0).Format("2006-01-02")
}

func GenerateRandomAmount() float64 {
	return float64(rand.Intn(99100)+1000) / 100.0
}

func GenerateRandomCustomerID() int {
	return rand.Intn(10) + 1
}
