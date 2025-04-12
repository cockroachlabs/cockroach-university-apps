package com.omnicorp.crm.repository;

import com.omnicorp.crm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	// You can add custom query methods here if needed
}

