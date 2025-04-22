package com.omnicorp.logistics;

import com.omnicorp.logistics.entity.Shipment;
import com.omnicorp.logistics.repository.ShipmentRepository;
import com.omnicorp.logistics.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableRetry
public class ShipmentServiceTest {

	@MockitoBean
	private ShipmentRepository shipmentRepository;

	@Autowired
	private ShipmentService shipmentService;

	@Test
	void testUpdateShipment_success() {
		UUID uuid = UUID.randomUUID();
		Shipment existingShipment = new Shipment();
		existingShipment.setShipmentUuid(uuid);
		existingShipment.setQuantity(10);

		Shipment updatedShipment = new Shipment();
		updatedShipment.setQuantity(20);
		updatedShipment.setShipmentDate(LocalDate.now());

		when(shipmentRepository.findById(uuid)).thenReturn(Optional.of(existingShipment));
		when(shipmentRepository.save(any(Shipment.class))).thenReturn(updatedShipment);

		Shipment result = shipmentService.updateShipment(uuid, updatedShipment);

		assertNotNull(result);
		assertEquals(updatedShipment.getQuantity(), result.getQuantity());
		assertEquals(updatedShipment.getShipmentDate(), result.getShipmentDate());
		verify(shipmentRepository, times(1)).findById(uuid);
		verify(shipmentRepository, times(1)).save(existingShipment);
	}

	@Test
	void testUpdateShipment_notFound() {
		UUID uuid = UUID.randomUUID();
		Shipment updatedShipment = new Shipment();

		when(shipmentRepository.findById(uuid)).thenReturn(Optional.empty());

		Shipment result = shipmentService.updateShipment(uuid, updatedShipment);

		assertNull(result);
		verify(shipmentRepository, times(1)).findById(uuid);
		verify(shipmentRepository, never()).save(any(Shipment.class));
	}

	@Test
	void testUpdateShipment_retrySuccess() {
		UUID uuid = UUID.randomUUID();
		Shipment existingShipment = new Shipment();
		existingShipment.setShipmentUuid(uuid);
		existingShipment.setQuantity(10);

		Shipment updatedShipment = new Shipment();
		updatedShipment.setQuantity(20);

		when(shipmentRepository.findById(uuid)).thenReturn(Optional.of(existingShipment));
		when(shipmentRepository.save(existingShipment))
				.thenThrow(new OptimisticLockingFailureException("Simulated conflict"))
				.thenReturn(updatedShipment);

		Shipment result = shipmentService.updateShipment(uuid, updatedShipment);

		assertNotNull(result);
		assertEquals(updatedShipment.getQuantity(), result.getQuantity());
		verify(shipmentRepository, times(2)).findById(uuid); // Find is called once
		verify(shipmentRepository, times(2)).save(existingShipment); // Save is called twice due to retry
	}

	@Test
	void testUpdateShipment_retryFailure_maxAttemptsReached() {
		UUID uuid = UUID.randomUUID();
		Shipment existingShipment = new Shipment();
		existingShipment.setShipmentUuid(uuid);
		existingShipment.setQuantity(10);

		Shipment updatedShipment = new Shipment();
		updatedShipment.setQuantity(20);

		when(shipmentRepository.findById(uuid)).thenReturn(Optional.of(existingShipment));
		when(shipmentRepository.save(existingShipment))
				.thenThrow(new OptimisticLockingFailureException("Simulated conflict"));

		try {
			shipmentService.updateShipment(uuid, updatedShipment);
		} catch (OptimisticLockingFailureException e) {
			assertEquals("Simulated conflict", e.getMessage());
		}

		verify(shipmentRepository, times(5)).findById(uuid);
		verify(shipmentRepository, times(5)).save(existingShipment);
	}
}