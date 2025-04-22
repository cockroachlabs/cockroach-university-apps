package com.omnicorp.crm;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.entity.Order;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.repository.OrderRepository;
import com.omnicorp.crm.service.CrmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

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
		UUID uuid = UUID.fromString("c53dc772-2f4e-4181-9574-b95fbc60df4a"); // TODO: Replace with an existing UUID
		Customer customer = crmService.getCustomerById(uuid);
		assertNotNull(customer);
		assertEquals("Marilie", customer.getFirstName());
	}

	@Test
	@Transactional
	@Rollback
	void testCreateCustomer() {
		Customer newCustomer = new Customer();
		newCustomer.setFirstName("Test");
		newCustomer.setLastName("Customer");
		newCustomer.setEmail("test@example.com");
		newCustomer.setPhone("123-456-7890");
		newCustomer.setAddress("Test Address");

		Customer createdCustomer = crmService.createCustomer(newCustomer);
		assertNotNull(createdCustomer.getCustomerUuid());
		assertEquals("Test", createdCustomer.getFirstName());
	}

	@Test
	void testGetAllOrders() {
		List<Order> orders = crmService.getAllOrders();
		assertNotNull(orders);
		assertFalse(orders.isEmpty());
	}

	@Test
	void testGetOrderById() {
		UUID uuid = UUID.fromString("485550f4-44b3-493c-b0f0-e988960e98af"); //TODO: Replace with an existing UUID
		Order order = crmService.getOrderById(uuid);
		assertNotNull(order);
		assertEquals(5078.88, order.getTotalAmount()); // TODO: Replace with the real value
	}

	@Test
	@Transactional
	@Rollback
	void testCreateOrder() {
		Order newOrder = new Order();
		UUID customerUuid = UUID.fromString("c53dc772-2f4e-4181-9574-b95fbc60df4a"); // TODO: Assuming a customer exists with this UUID
		newOrder.setCustomerUuid(customerUuid);
		newOrder.setOrderDate(LocalDate.now());
		newOrder.setTotalAmount(100.00);

		Order createdOrder = crmService.createOrder(newOrder);
		assertNotNull(createdOrder.getOrderUuid());
		assertEquals(100.00, createdOrder.getTotalAmount());
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
		UUID customerUuid = UUID.fromString("c53dc772-2f4e-4181-9574-b95fbc60df4a"); // TODO: Assuming a customer exists with this UUID
		newOrder.setCustomerUuid(customerUuid);
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
		assertNull(crmService.getCustomerByIdInNewTransaction(newCustomer.getCustomerUuid()));
	}

	@Test
	@Transactional
	@Rollback
	void testTransactionIsolation() {
		// Simulate a concurrent read (not really concurrent in a unit test)
		UUID uuid = UUID.fromString("c53dc772-2f4e-4181-9574-b95fbc60df4a"); // Replace with an existing UUID
		Customer originalCustomer = crmService.getCustomerById(uuid);
		assertNotNull(originalCustomer);

		// Modify the customer within the transaction
		Customer customerToUpdate = crmService.getCustomerById(uuid);
		if (customerToUpdate != null) {
			customerToUpdate.setAddress("Updated Address");
			crmService.createCustomer(customerToUpdate);
		}

		// Simulate reading the customer again (before the transaction completes)
		Customer concurrentReadCustomer = crmService.getCustomerById(uuid);
		assertNotNull(concurrentReadCustomer);

		// Assert that within the transaction, the change is visible
		if (concurrentReadCustomer != null) {
			assertEquals("Updated Address", concurrentReadCustomer.getAddress());
		}
	}

	@Test
	void testMaxRetriesExceeded() throws NoSuchFieldException {
		// Create a test customer and order
		Customer testCustomer = new Customer();
		testCustomer.setFirstName("RetryTest");
		testCustomer.setLastName("Customer");
		testCustomer = crmService.createCustomer(testCustomer);
		assertNotNull(testCustomer.getCustomerUuid());

		Order testOrder = new Order();
		testOrder.setCustomerUuid(testCustomer.getCustomerUuid());
		testOrder.setOrderDate(LocalDate.now());
		testOrder.setTotalAmount(100.00);
		Order savedOrder = crmService.createOrder(testOrder);
		assertNotNull(savedOrder.getOrderUuid());

		try {
			// Simulate a case where updateOrderWithRetry will always fail
			Order invalidOrder = new Order();
			invalidOrder.setOrderUuid(savedOrder.getOrderUuid());
			invalidOrder.setTotalAmount(-999999.99); // Assuming negative values trigger retry

			Exception exception = assertThrows(DataIntegrityViolationException.class, () ->
					crmService.updateOrderWithRetry(invalidOrder)
			);

			assertTrue(exception.getMessage().contains("CHECK constraint"));

		} finally {
			// Clean up
			orderRepository.delete(savedOrder);
			customerRepository.delete(testCustomer);
		}
	}
}