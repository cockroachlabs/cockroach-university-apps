package com.omnicorp.crm.repository;

import com.omnicorp.crm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	// You can add custom query methods here if needed
}
