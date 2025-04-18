package com.omnicorp.crm.service;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.entity.Order;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.repository.OrderRepository;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrmService {

	private final CustomerRepository customerRepository;
	private final OrderRepository orderRepository;

	@Autowired
	public CrmService(CustomerRepository customerRepository, OrderRepository orderRepository) {
		this.customerRepository = customerRepository;
		this.orderRepository = orderRepository;
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id).orElse(null);
	}

	public Customer createCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	public Order createOrder(Order order) {
		return orderRepository.save(order);
	}

	public Customer getCustomerByIdInNewTransaction(Long id) {
		return customerRepository.findById(id).orElse(null);
	}


	// New Code

	@Transactional
	public Order updateOrderWithRetry(Order updatedOrder) {
		return executeWithRetry(() -> {
			// Fetch the existing order
			Order currentOrder = orderRepository.findById(updatedOrder.getOrderId())
					.orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + updatedOrder.getOrderId()));

			// Update fields
			// Validate for negative numbers, if that's the case then throw an error
			if (updatedOrder.getTotalAmount() < 0) {
				throw new IllegalArgumentException("Invalid order amount");
			}
			currentOrder.setTotalAmount(updatedOrder.getTotalAmount());

			// Save and return
			return orderRepository.save(currentOrder);
		});
	}



	private static int MAX_RETRIES = 5;
	private static int INITIAL_BACKOFF_MS = 100;


	private <T> T executeWithRetry(RetryableTransaction<T> transaction) {
		int retryCount = 0;
		int backoff = INITIAL_BACKOFF_MS;

		while (retryCount < MAX_RETRIES) {
			try {
				// Execute the transaction
				return transaction.execute();
			} catch (DataAccessException ex) {
				// Check if the exception is due to a transient transaction conflict (SQLSTATE 40001)
				if (isRetryableException(ex) && retryCount < MAX_RETRIES - 1) {
					retryCount++;
					try {
						// Exponential backoff before retrying
						Thread.sleep(backoff);
						backoff *= 2;
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						throw new IllegalStateException("Thread was interrupted during backoff", e);
					}
				} else {
					throw ex; // Rethrow non-retryable exception or if retries are exhausted
				}
			}
		}
		throw new IllegalStateException("Transaction retry limit exceeded");
	}

	private boolean isRetryableException(DataAccessException exception) {
		Throwable cause = exception.getRootCause();
		return cause != null && cause.getMessage() != null && cause.getMessage().contains("SQLSTATE 40001");
	}


	@FunctionalInterface
	private interface RetryableTransaction<T> {
		T execute() throws DataAccessException;
	}


}