package com.omnicorp.logistics.repository;

import com.omnicorp.logistics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// You can add custom query methods here if needed
}