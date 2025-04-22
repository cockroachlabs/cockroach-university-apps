package com.omnicorp.logistics.service;

import com.omnicorp.logistics.entity.Shipment;
import com.omnicorp.logistics.repository.ShipmentRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;

	@Autowired
	public ShipmentService(ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	public Optional<Shipment> getShipmentByUuid(UUID uuid) {
		return shipmentRepository.findById(uuid);
	}

	public Shipment createShipment(Shipment shipment) {
		return shipmentRepository.save(shipment);
	}

	@Transactional
	@Retryable(value = DataAccessException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
	public Shipment updateShipment(UUID uuid, Shipment updatedShipment) {
		Optional<Shipment> existingShipmentOptional = shipmentRepository.findById(uuid);
		if (existingShipmentOptional.isPresent()) {
			Shipment existingShipment = existingShipmentOptional.get();
			// Update fields as needed
			existingShipment.setOrderId(updatedShipment.getOrderId());
			existingShipment.setProductId(updatedShipment.getProductId());
			existingShipment.setQuantity(updatedShipment.getQuantity());
			existingShipment.setShipmentDate(updatedShipment.getShipmentDate());
			return shipmentRepository.save(existingShipment);
		} else {
			return null;
		}
	}

	public void deleteShipment(UUID uuid) {
		shipmentRepository.deleteById(uuid);
	}
}
