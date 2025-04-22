package com.omnicorp.logistics.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "shipment_uuid")
	private UUID shipmentUuid;

	@Column(name = "shipment_id")
	private Long shipmentId;

	@Column(name = "order_uuid")
	private UUID orderUuid;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "product_uuid")
	private UUID productUuid;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "shipment_date")
	private LocalDate shipmentDate;

	public Shipment() {
	}

	public UUID getShipmentUuid() {
		return shipmentUuid;
	}

	public void setShipmentUuid(UUID shipmentUuid) {
		this.shipmentUuid = shipmentUuid;
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

	public UUID getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(UUID orderUuid) {
		this.orderUuid = orderUuid;
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

	public LocalDate getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(LocalDate shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Override
	public String toString() {
		return "Shipment{" +
				"shipmentUuid=" + shipmentUuid +
				", shipmentId=" + shipmentId +
				", orderUuid=" + orderUuid +
				", orderId=" + orderId +
				", productUuid=" + productUuid +
				", productId=" + productId +
				", quantity=" + quantity +
				", shipmentDate=" + shipmentDate +
				'}';
	}
}