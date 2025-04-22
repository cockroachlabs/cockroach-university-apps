package com.omnicorp.logistics.repository;

import com.omnicorp.logistics.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
	// You can add custom query methods here if needed
}