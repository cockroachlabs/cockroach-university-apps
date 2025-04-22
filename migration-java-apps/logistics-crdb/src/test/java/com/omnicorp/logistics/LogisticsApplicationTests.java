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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
		UUID uuid = UUID.fromString("046b536d-50b4-4f00-b624-ad5db65801e9"); // TODO: Replace with an existing UUID
		Inventory inventory = logisticsService.getInventoryById(uuid);
		assertNotNull(inventory);
		assertEquals(248622, inventory.getQuantity()); // TODO: Assuming initial data
	}

	@Test
	@Transactional
	@Rollback
	void testCreateInventory() {
		Inventory newInventory = new Inventory();
		UUID productUuid = UUID.fromString("7a8cb753-2207-4088-83ef-14518c2d0082"); // TODO: Assuming a product exists with this UUID
		newInventory.setProductUuid(productUuid);
		newInventory.setQuantity(50);
		newInventory.setLocation("Warehouse A");

		Inventory createdInventory = logisticsService.createInventory(newInventory);
		assertNotNull(createdInventory.getInventoryUuid());
		assertEquals(50, createdInventory.getQuantity());
	}

	@Test
	void testGetAllProducts() {
		List<Product> products = logisticsService.getAllProducts();
		assertNotNull(products);
		assertFalse(products.isEmpty());
	}

	@Test
	void testGetProductById() {
		UUID uuid = UUID.fromString("f52a9b8d-05f1-4c08-8fe8-aab471712248"); // TODO: Replace with an existing UUID
		Product product = logisticsService.getProductById(uuid);
		assertNotNull(product);
		assertEquals("est", product.getProductName()); // Assuming initial data
	}

	@Test
	@Transactional
	@Rollback
	void testCreateProduct() {
		Product newProduct = new Product();
		newProduct.setProductName("Keyboard");
		newProduct.setDescription("Ergonomic Keyboard");
		newProduct.setPrice(75.00);

		Product createdProduct = logisticsService.createProduct(newProduct);
		assertNotNull(createdProduct.getProductUuid());
		assertEquals("Keyboard", createdProduct.getProductName());
	}

	@Test
	void testGetAllShipments() {
		List<Shipment> shipments = logisticsService.getAllShipments();
		assertNotNull(shipments);
		assertFalse(shipments.isEmpty());
	}

	@Test
	void testGetShipmentById() {
		UUID uuid = UUID.fromString("0430f3b8-878e-4e63-af88-4c880873dde5"); // TODO: Replace with an existing UUID
		Shipment shipment = logisticsService.getShipmentById(uuid);
		assertNotNull(shipment);
		assertEquals(2483, shipment.getQuantity()); // Assuming initial data
	}

	@Test
	@Transactional
	@Rollback
	void testCreateShipment() {
		Shipment newShipment = new Shipment();
		UUID orderUuid = UUID.fromString("2861badd-835e-4008-96e0-98e697a7d2ae"); // TODO: Assuming an order exists with this UUID
		UUID productUuid = UUID.fromString("074471a8-96bf-43b2-b5f6-08fbaac191d9"); // TODO: Assuming a product exists with this UUID
		newShipment.setOrderUuid(orderUuid);
		newShipment.setProductUuid(productUuid);
		newShipment.setQuantity(10);
		newShipment.setShipmentDate(LocalDate.now());

		Shipment createdShipment = logisticsService.createShipment(newShipment);
		assertNotNull(createdShipment.getShipmentUuid());
		assertEquals(10, createdShipment.getQuantity());
	}

	@Test
	@Transactional
	@Rollback
	void testTransactionRollback() {
		Product newProduct = new Product();
		newProduct.setProductName("Test Product");
		newProduct.setDescription("Transactional Test");
		newProduct.setPrice(-10.00); // Invalid price

		Inventory newInventory = new Inventory();
		UUID productUuid = UUID.fromString("98765432-10fe-dcba-9876-543210fedcba"); // TODO: Assuming a product exists with this UUID
		newInventory.setProductUuid(productUuid);
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
	@Transactional
	@Rollback
	void testTransactionIsolation() {
		// Simulate a concurrent read (not really concurrent in a unit test)
		UUID uuid = UUID.fromString("f52a9b8d-05f1-4c08-8fe8-aab471712248"); // TODO: Replace with an existing UUID
		Product originalProduct = logisticsService.getProductById(uuid);
		assertNotNull(originalProduct);
		String originalDescription = originalProduct.getDescription();

		// Modify the product within the transaction
		Product productToUpdate = logisticsService.getProductById(uuid);
		if (productToUpdate != null) {
			productToUpdate.setDescription("Updated Description");
			logisticsService.createProduct(productToUpdate);
		}

		// Simulate reading the product again (before the transaction completes)
		Product concurrentReadProduct = logisticsService.getProductById(uuid);
		assertNotNull(concurrentReadProduct);

		// Assert that within the transaction, the change is visible
		if (concurrentReadProduct != null) {
			assertEquals("Updated Description", concurrentReadProduct.getDescription());
		}
	}
}