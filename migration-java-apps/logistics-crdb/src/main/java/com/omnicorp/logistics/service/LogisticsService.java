package com.omnicorp.logistics.service;

import com.omnicorp.logistics.entity.Inventory;
import com.omnicorp.logistics.entity.Product;
import com.omnicorp.logistics.entity.Shipment;
import com.omnicorp.logistics.repository.InventoryRepository;
import com.omnicorp.logistics.repository.ProductRepository;
import com.omnicorp.logistics.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LogisticsService {

	private final InventoryRepository inventoryRepository;
	private final ProductRepository productRepository;
	private final ShipmentRepository shipmentRepository;

	@Autowired
	public LogisticsService(InventoryRepository inventoryRepository, ProductRepository productRepository, ShipmentRepository shipmentRepository) {
		this.inventoryRepository = inventoryRepository;
		this.productRepository = productRepository;
		this.shipmentRepository = shipmentRepository;
	}

	public List<Inventory> getAllInventories() {
		return inventoryRepository.findAll();
	}

	public Inventory getInventoryById(UUID id) {
		return inventoryRepository.findById(id).orElse(null);
	}

	public Inventory createInventory(Inventory inventory) {
		return inventoryRepository.save(inventory);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(UUID id) {
		return productRepository.findById(id).orElse(null);
	}

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	public List<Shipment> getAllShipments() {
		return shipmentRepository.findAll();
	}

	public Shipment getShipmentById(UUID id) {
		return shipmentRepository.findById(id).orElse(null);
	}

	public Shipment createShipment(Shipment shipment) {
		return shipmentRepository.save(shipment);
	}
}