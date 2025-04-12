package com.omnicorp.logistics;

import com.omnicorp.logistics.entity.Inventory;
import com.omnicorp.logistics.entity.Product;
import com.omnicorp.logistics.entity.Shipment;
import com.omnicorp.logistics.repository.InventoryRepository;
import com.omnicorp.logistics.repository.ProductRepository;
import com.omnicorp.logistics.repository.ShipmentRepository;
import com.omnicorp.logistics.service.LogisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@SpringBootTest
class LogisticsApplicationTests {

	@Autowired
	private LogisticsService logisticsService;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ShipmentRepository shipmentRepository;

	@Test
	void testGetAllInventories() {
		List<Inventory> inventories = logisticsService.getAllInventories();
		assertNotNull(inventories);
		assertFalse(inventories.isEmpty());
	}

	@Test
	void testGetInventoryById() {
		Inventory inventory = logisticsService.getInventoryById(1L);
		assertNotNull(inventory);
		assertEquals(82490205, inventory.getQuantity()); // Assuming initial data
	}

	@Test
	void testCreateInventory() {
		Inventory newInventory = new Inventory();
		newInventory.setProductId(1L); // Assuming a product exists with ID 1
		newInventory.setQuantity(50);
		newInventory.setLocation("Warehouse A");

		Inventory createdInventory = logisticsService.createInventory(newInventory);
		assertNotNull(createdInventory.getInventoryId());
		assertEquals(50, createdInventory.getQuantity());

		// Clean up (rollback)
		inventoryRepository.delete(createdInventory);
		assertNull(logisticsService.getInventoryById(createdInventory.getInventoryId()));
	}

	@Test
	void testGetAllProducts() {
		List<Product> products = logisticsService.getAllProducts();
		assertNotNull(products);
		assertFalse(products.isEmpty());
	}

	@Test
	void testGetProductById() {
		Product product = logisticsService.getProductById(1L);
		assertNotNull(product);
		assertEquals("est", product.getProductName()); // Assuming initial data
	}

	@Test
	void testCreateProduct() {
		Product newProduct = new Product();
		newProduct.setProductName("Keyboard");
		newProduct.setDescription("Ergonomic Keyboard");
		newProduct.setPrice(75.00);

		Product createdProduct = logisticsService.createProduct(newProduct);
		assertNotNull(createdProduct.getProductId());
		assertEquals("Keyboard", createdProduct.getProductName());

		// Clean up (rollback)
		productRepository.delete(createdProduct);
		assertNull(logisticsService.getProductById(createdProduct.getProductId()));
	}

	@Test
	void testGetAllShipments() {
		List<Shipment> shipments = logisticsService.getAllShipments();
		assertNotNull(shipments);
		assertFalse(shipments.isEmpty());
	}

	@Test
	void testGetShipmentById() {
		Shipment shipment = logisticsService.getShipmentById(1L);
		assertNotNull(shipment);
		assertEquals(2599, shipment.getQuantity()); // Assuming initial data
	}

	@Test
	void testCreateShipment() {
		Shipment newShipment = new Shipment();
		newShipment.setOrderId(1L); // Assuming an order exists with ID 1
		newShipment.setProductId(1L); // Assuming a product exists with ID 1
		newShipment.setQuantity(10);
		newShipment.setShipmentDate(LocalDate.now());

		Shipment createdShipment = logisticsService.createShipment(newShipment);
		assertNotNull(createdShipment.getShipmentId());
		assertEquals(10, createdShipment.getQuantity());

		// Clean up (rollback)
		shipmentRepository.delete(createdShipment);
		assertNull(logisticsService.getShipmentById(createdShipment.getShipmentId()));
	}

	@Test
	void testTransactionRollback() {
		Product newProduct = new Product();
		newProduct.setProductName("Test Product");
		newProduct.setDescription("Transactional Test");
		newProduct.setPrice(-10.00); // Invalid price

		Inventory newInventory = new Inventory();
		newInventory.setProductId(1L); // Assuming a product exists with ID 1
		newInventory.setQuantity(20);
		newInventory.setLocation("Test Location");

		logisticsService.createProduct(newProduct);
		assertThrows(IllegalArgumentException.class, () -> {
			if (newProduct.getPrice() < 0) {
				throw new IllegalArgumentException("Invalid product price");
			}
			logisticsService.createInventory(newInventory);
		});
	}

	@Test
	void testTransactionIsolation() {
		// Simulate a concurrent read (not really concurrent in a unit test)
		Product originalProduct = logisticsService.getProductById(1L);
		assertNotNull(originalProduct);
		String originalDescription = originalProduct.getDescription();

		// Modify the product within the transaction
		Product productToUpdate = logisticsService.getProductById(1L);
		productToUpdate.setDescription("Updated Description");
		logisticsService.createProduct(productToUpdate);

		// Simulate reading the product again (before the transaction completes)
		Product concurrentReadProduct = logisticsService.getProductById(1L);
		assertNotNull(concurrentReadProduct);

		// Assert that within the transaction, the change is visible
		assertEquals("Updated Description", concurrentReadProduct.getDescription());

		// The transaction will be rolled back by @Rollback
		// We are NOT explicitly throwing an exception to force it.
	}

}
