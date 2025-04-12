package com.omnicorp.crm.service;

import com.omnicorp.crm.entity.Customer;
import com.omnicorp.crm.entity.Order;
import com.omnicorp.crm.repository.CustomerRepository;
import com.omnicorp.crm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Customer getCustomerByIdInNewTransaction(Long id) {
		return customerRepository.findById(id).orElse(null);
	}
}