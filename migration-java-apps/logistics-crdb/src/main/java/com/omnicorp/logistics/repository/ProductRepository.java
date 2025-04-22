package com.omnicorp.logistics.repository;

import com.omnicorp.logistics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	// You can add custom query methods here if needed
}