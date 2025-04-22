package com.omnicorp.crm.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "order_uuid")
	private UUID orderUuid;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "customer_uuid")
	private UUID customerUuid;

	@Column(name = "customer_id")
	private Long customerId;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@Column(name = "total_amount")
	private Double totalAmount;

	public Order() {
	}

	public UUID getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(UUID orderUuid) {
		this.orderUuid = orderUuid;
	}

	public UUID getCustomerUuid() {
		return customerUuid;
	}

	public void setCustomerUuid(UUID customerUuid) {
		this.customerUuid = customerUuid;
	}

	// Getters and setters
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderUuid=" + orderUuid +
				", orderId=" + orderId +
				", customerUuid=" + customerUuid +
				", customerId=" + customerId +
				", orderDate=" + orderDate +
				", totalAmount=" + totalAmount +
				'}';
	}
}