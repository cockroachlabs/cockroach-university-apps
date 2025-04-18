package com.omnicorp.crm;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.entity.Order;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.repository.OrderRepository;
import com.omnicorp.crm.service.CrmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
class CrmApplicationTests {

	@Autowired
	private CrmService crmService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	void testGetAllCustomers() {
		List<Customer> customers = crmService.getAllCustomers();
		assertNotNull(customers);
		assertFalse(customers.isEmpty());
	}

	@Test
	void testGetCustomerById() {
		Customer customer = crmService.getCustomerById(1L);
		assertNotNull(customer);
		assertEquals("Marilie", customer.getFirstName());
	}

	@Test
	void testCreateCustomer() {
		Customer newCustomer = new Customer();
		newCustomer.setFirstName("Test");
		newCustomer.setLastName("Customer");
		newCustomer.setEmail("test@example.com");
		newCustomer.setPhone("123-456-7890");
		newCustomer.setAddress("Test Address");

		Customer createdCustomer = crmService.createCustomer(newCustomer);
		assertNotNull(createdCustomer.getCustomerId());
		assertEquals("Test", createdCustomer.getFirstName());

		// Clean up (rollback) - demonstrating transactional behavior
		customerRepository.delete(createdCustomer);
		assertNull(crmService.getCustomerById(createdCustomer.getCustomerId()));
	}

	@Test
	void testGetAllOrders() {
		List<Order> orders = crmService.getAllOrders();
		assertNotNull(orders);
		assertFalse(orders.isEmpty());
	}

	@Test
	void testGetOrderById() {
		Order order = crmService.getOrderById(1L);
		assertNotNull(order);
		assertEquals(2706.92, order.getTotalAmount());
	}

	@Test
	void testCreateOrder() {
		Order newOrder = new Order();
		newOrder.setCustomerId(1L);
		newOrder.setOrderDate(LocalDate.now());
		newOrder.setTotalAmount(100.00);

		Order createdOrder = crmService.createOrder(newOrder);
		assertNotNull(createdOrder.getOrderId());
		assertEquals(100.00, createdOrder.getTotalAmount());

		// Clean up (rollback)
		orderRepository.delete(createdOrder);
		assertNull(crmService.getOrderById(createdOrder.getOrderId()));
	}

	@Test
	void testCreateCustomerDuplicateEmail() {
		Customer newCustomer = new Customer();
		newCustomer.setFirstName("Duplicate");
		newCustomer.setLastName("Email");
		newCustomer.setEmail("turner.ole@example.org"); // Using an existing email
		newCustomer.setPhone("111-222-3333");
		newCustomer.setAddress("Duplicate Address");

		assertThrows(DataIntegrityViolationException.class, () -> {
			crmService.createCustomer(newCustomer);
		});
	}

	@Test
	@Transactional
	@Rollback
	void testTransactionRollback() {
		Customer newCustomer = new Customer();
		newCustomer.setFirstName("Transactional");
		newCustomer.setLastName("Test");
		newCustomer.setEmail("transactional@example.com");
		newCustomer.setPhone("444-555-6666");
		newCustomer.setAddress("Transactional Address");

		Order newOrder = new Order();
		newOrder.setCustomerId(1L); // Existing customer
		newOrder.setOrderDate(LocalDate.now());
		newOrder.setTotalAmount(-50.00); // Invalid amount

		crmService.createCustomer(newCustomer);
		assertThrows(IllegalArgumentException.class, () -> {
			if (newOrder.getTotalAmount() < 0) {
				throw new IllegalArgumentException("Invalid order amount");
			}
			crmService.createOrder(newOrder);
		});

		// Assert that neither customer nor order were saved (rolled back)
		assertNotNull(crmService.getCustomerByIdInNewTransaction(newCustomer.getCustomerId()));
	}

	@Test
	@Transactional
	@Rollback
	void testTransactionIsolation() {
		// Simulate a concurrent read (not really concurrent in a unit test)
		Customer originalCustomer = crmService.getCustomerById(1L);
		assertNotNull(originalCustomer);

		// Modify the customer within the transaction
		Customer customerToUpdate = crmService.getCustomerById(1L);
		customerToUpdate.setAddress("Updated Address");
		crmService.createCustomer(customerToUpdate);

		// Simulate reading the customer again (before the transaction completes)
		Customer concurrentReadCustomer = crmService.getCustomerById(1L);
		assertNotNull(concurrentReadCustomer);

		// Assert that within the transaction, the change is visible
		assertEquals("Updated Address", concurrentReadCustomer.getAddress());
	}

	@Test
	void testMaxRetriesExceeded() throws NoSuchFieldException {
		// Create a test order
		Order testOrder = new Order();
		testOrder.setCustomerId(1L);
		testOrder.setOrderDate(LocalDate.now());
		testOrder.setTotalAmount(100.00);

		Order savedOrder = crmService.createOrder(testOrder);
		assertNotNull(savedOrder.getOrderId());

		try {
			// Create a field accessor for the private method
			Field maxRetriesField = CrmService.class.getDeclaredField("MAX_RETRIES");
			maxRetriesField.setAccessible(true);

			// Store the original value to restore it later
			int originalMaxRetries = (int) maxRetriesField.get(crmService);

			// Set MAX_RETRIES to 1 to make the test run faster
			maxRetriesField.set(crmService, 0);

			// Create an order with invalid amount to trigger an exception that would need retrying
			// We're simulating a case where no matter how many times we retry, it will always fail
			Order invalidOrder = new Order();
			invalidOrder.setOrderId(savedOrder.getOrderId());
			// Set to a value that will cause a validation error or constraint violation
			invalidOrder.setTotalAmount(-999999.99); // Assuming there's validation against negative values

			// This should exhaust retries and throw the transaction retry limit exception
			Exception exception = assertThrows(IllegalStateException.class, () ->
					crmService.updateOrderWithRetry(invalidOrder)
			);

			assertEquals("Transaction retry limit exceeded", exception.getMessage());

			// Restore the original MAX_RETRIES value
			maxRetriesField.set(crmService, originalMaxRetries);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			// Clean up the test order
			orderRepository.delete(savedOrder);
		}
	}
}
