package com.cams.customer.service;

import com.cams.customer.dto.CustomerRequest;
import com.cams.customer.dto.CustomerResponse;
import com.cams.customer.entity.Customer;
import com.cams.customer.exception.CustomerNotFoundException;
import com.cams.customer.exception.DuplicateResourceException;
import com.cams.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {

        log.info(
                "Creating customer with customerNumber={}",
                request.getCustomerNumber()
        );

        if (customerRepository.existsByCustomerNumber(
                request.getCustomerNumber())) {

            log.warn(
                    "Customer creation failed. Customer number already exists: {}",
                    request.getCustomerNumber()
            );

            throw new DuplicateResourceException(
                    "Customer number already exists: "
                            + request.getCustomerNumber());
        }

        if (customerRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "Customer creation failed. Customer email already exists: {}",
                    request.getCustomerNumber()
            );

            throw new DuplicateResourceException(
                    "Email already exists: "
                            + request.getEmail());
        }

        Customer customer = new Customer(
                request.getCustomerNumber(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                "ACTIVE"
        );

        Customer savedCustomer =
                customerRepository.save(customer);

        log.info(
                "Customer created successfully. customerNumber={}",
                savedCustomer.getCustomerNumber()
        );

        return mapToResponse(savedCustomer);
    }

    public CustomerResponse getCustomer(Long id) {

        log.info("Fetching customer id={}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found id={}", id);
                    return new CustomerNotFoundException(
                            "Customer not found: " + id);
                });

        log.info(
                "Customer fetched successfully id={}",
                id
        );

        return mapToResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found: " + id));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updatedCustomer =
                customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found: " + id));

        customerRepository.delete(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}