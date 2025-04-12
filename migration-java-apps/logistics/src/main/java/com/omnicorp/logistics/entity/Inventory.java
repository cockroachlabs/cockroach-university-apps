package com.omnicorp.logistics.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq_gen")
	@SequenceGenerator(name = "inventory_seq_gen", sequenceName = "inventory_inventory_id_seq", allocationSize = 1)
	@Column(name = "inventory_id")
	private Long inventoryId;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "location")
	private String location;

	public Inventory() {
	}

	// Getters and setters
	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
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
				"inventoryId=" + inventoryId +
				", productId=" + productId +
				", quantity=" + quantity +
				", location='" + location + '\'' +
				'}';
	}
}