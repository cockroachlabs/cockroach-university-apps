package com.omnicorp.crm;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.service.CrmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableRetry
public class CrmCustomerTests {
	@Autowired
	private CrmService crmService;

	@MockitoBean
	private CustomerRepository customerRepository; // Use MockBean for Spring context

	@Test
	void testUpdateCustomer_success() {
		UUID uuid = UUID.randomUUID();
		Customer existingCustomer = new Customer();
		existingCustomer.setCustomerUuid(uuid);
		existingCustomer.setFirstName("Original");

		Customer updatedCustomer = new Customer();
		updatedCustomer.setFirstName("Updated");
		updatedCustomer.setEmail("updated@example.com");

		when(customerRepository.findById(uuid)).thenReturn(Optional.of(existingCustomer));
		when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

		Customer result = crmService.updateCustomer(uuid, updatedCustomer);

		assertNotNull(result);
		assertEquals("Updated", result.getFirstName());
		assertEquals("updated@example.com", result.getEmail());
		verify(customerRepository, times(1)).findById(uuid);
		verify(customerRepository, times(1)).save(existingCustomer);
	}

	@Test
	void testUpdateCustomer_notFound() {
		UUID uuid = UUID.randomUUID();
		Customer updatedCustomer = new Customer();

		when(customerRepository.findById(uuid)).thenReturn(Optional.empty());

		Customer result = crmService.updateCustomer(uuid, updatedCustomer);

		assertNull(result);
		verify(customerRepository, times(1)).findById(uuid);
		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	void testUpdateCustomer_retrySuccess() {
		UUID uuid = UUID.randomUUID();
		Customer existingCustomer = new Customer();
		existingCustomer.setCustomerUuid(uuid);
		existingCustomer.setFirstName("Original");

		Customer updatedCustomer = new Customer();
		updatedCustomer.setFirstName("Updated");

		when(customerRepository.findById(uuid)).thenReturn(Optional.of(existingCustomer));
		when(customerRepository.save(existingCustomer))
				.thenThrow(new OptimisticLockingFailureException("Simulated conflict"))
				.thenReturn(updatedCustomer);

		Customer result = crmService.updateCustomer(uuid, updatedCustomer);

		assertNotNull(result);
		assertEquals("Updated", result.getFirstName());
		verify(customerRepository, times(2)).findById(uuid);
		verify(customerRepository, times(2)).save(existingCustomer); // Save is called twice due to retry
	}

	@Test
	void testUpdateCustomer_retryFailure_maxAttemptsReached() {
		UUID uuid = UUID.randomUUID();
		Customer existingCustomer = new Customer();
		existingCustomer.setCustomerUuid(uuid);
		existingCustomer.setFirstName("Original");

		Customer updatedCustomer = new Customer();
		updatedCustomer.setFirstName("Updated");

		when(customerRepository.findById(uuid)).thenReturn(Optional.of(existingCustomer));
		when(customerRepository.save(existingCustomer))
				.thenThrow(new OptimisticLockingFailureException("Simulated conflict"));

		assertThrows(OptimisticLockingFailureException.class, () -> crmService.updateCustomer(uuid, updatedCustomer));

		verify(customerRepository, times(5)).findById(uuid);
		verify(customerRepository, times(5)).save(existingCustomer); // Save is called maxAttempts times
	}
}
