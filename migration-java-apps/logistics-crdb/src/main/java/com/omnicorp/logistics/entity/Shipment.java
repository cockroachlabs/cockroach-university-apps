package com.omnicorp.logistics.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "shipments")
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_seq_gen")
	@SequenceGenerator(name = "shipment_seq_gen", sequenceName = "shipments_shipment_id_seq", allocationSize = 1)
	@Column(name = "shipment_id")
	private Long shipmentId;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "shipment_date")
	private LocalDate shipmentDate;

	public Shipment() {
	}

	// Getters and setters
	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public LocalDate getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(LocalDate shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Override
	public String toString() {
		return "Shipment{" +
				"shipmentId=" + shipmentId +
				", orderId=" + orderId +
				", productId=" + productId +
				", quantity=" + quantity +
				", shipmentDate=" + shipmentDate +
				'}';
	}
}