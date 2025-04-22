package com.omnicorp.crm.service;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.entity.Order;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

	public Customer getCustomerById(UUID id) {
		return customerRepository.findById(id).orElse(null);
	}

	public Customer createCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order getOrderById(UUID id) {
		return orderRepository.findById(id).orElse(null);
	}

	public Order createOrder(Order order) {
		return orderRepository.save(order);
	}

	// Added
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Customer getCustomerByIdInNewTransaction(UUID id) {
		return customerRepository.findById(id).orElse(null);
	}

	// With Spring Retry
	@Transactional
	@Retryable(value = DataAccessException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
	public Customer updateCustomer(UUID uuid, Customer updatedCustomer) {
		Optional<Customer> existingCustomerOptional = customerRepository.findById(uuid);
		if (existingCustomerOptional.isPresent()) {
			Customer existingCustomer = existingCustomerOptional.get();
			// Update fields as needed
			existingCustomer.setFirstName(updatedCustomer.getFirstName());
			existingCustomer.setLastName(updatedCustomer.getLastName());
			existingCustomer.setEmail(updatedCustomer.getEmail());
			existingCustomer.setPhone(updatedCustomer.getPhone());
			existingCustomer.setAddress(updatedCustomer.getAddress());
			return customerRepository.save(existingCustomer);
		} else {
			return null; // Or throw an exception indicating not found
		}
	}


	// New Code
	public Order updateOrderWithRetry(Order updatedOrder) {
		return executeWithRetry(() -> {
			// Fetch the existing order
			Order currentOrder = orderRepository.findById(updatedOrder.getOrderUuid())
					.orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + updatedOrder.getOrderId()));

			// Update fields
			// Validate for negative numbers, if that's the case then throw an error
			//if (updatedOrder.getTotalAmount() < 0) {
			//	throw new IllegalArgumentException("Invalid order amount");
			//}
			// Adding a Table Constrain instead:
			// ALTER TABLE orders
			// ADD CONSTRAINT chk_order_total_non_negative CHECK (total_amount >= 0);

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
		return (cause != null && cause.getMessage() != null && cause.getMessage().contains("SQLSTATE 40001")) ||
				(exception instanceof DataIntegrityViolationException && cause != null && cause.getMessage() != null && cause.getMessage().contains("failed to satisfy CHECK constraint"));
	}


	@FunctionalInterface
	private interface RetryableTransaction<T> {
		T execute() throws DataAccessException;
	}


}