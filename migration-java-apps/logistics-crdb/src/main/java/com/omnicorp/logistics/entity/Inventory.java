package com.omnicorp.logistics.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="inventory_uuid")
	private UUID inventoryUuid;

	@Column(name = "inventory_id")
	private Long inventoryId;

	@Column(name = "product_uuid")
	private UUID productUuid;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "location")
	private String location;

	public Inventory() {
	}

	public UUID getInventoryUuid() {
		return inventoryUuid;
	}

	public void setInventoryUuid(UUID inventoryUuid) {
		this.inventoryUuid = inventoryUuid;
	}

	// Getters and setters
	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public UUID getProductUuid() {
		return productUuid;
	}

	public void setProductUuid(UUID productUuid) {
		this.productUuid = productUuid;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Inventory{" +
				"inventoryUuid=" + inventoryUuid +
				", inventoryId=" + inventoryId +
				", productUuid=" + productUuid +
				", productId=" + productId +
				", quantity=" + quantity +
				", location='" + location + '\'' +
				'}';
	}
}