package com.cams.customer.service;

import com.cams.customer.dto.CustomerRequest;
import com.cams.customer.dto.CustomerResponse;
import com.cams.customer.entity.Customer;
import com.cams.customer.exception.CustomerNotFoundException;
import com.cams.customer.exception.DuplicateResourceException;
import com.cams.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldCreateCustomerSuccessfully() {

        CustomerRequest request = new CustomerRequest();
        request.setCustomerNumber("CUST001");
        request.setFirstName("Amit");
        request.setLastName("Kumar");
        request.setEmail("amit@test.com");
        request.setPhone("9999999999");

        when(customerRepository.existsByCustomerNumber("CUST001"))
                .thenReturn(false);

        when(customerRepository.existsByEmail("amit@test.com"))
                .thenReturn(false);

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerNumber("CUST001");
        savedCustomer.setFirstName("Amit");
        savedCustomer.setLastName("Kumar");
        savedCustomer.setEmail("amit@test.com");
        savedCustomer.setPhone("9999999999");
        savedCustomer.setStatus("ACTIVE");

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponse response =
                customerService.createCustomer(request);

        assertNotNull(response);
        //assertEquals(1L, response.getId());
        assertEquals("CUST001", response.getCustomerNumber());
        assertEquals("Amit", response.getFirstName());
        assertEquals("amit@test.com", response.getEmail());
        assertEquals("ACTIVE", response.getStatus());

        verify(customerRepository)
                .existsByCustomerNumber("CUST001");

        verify(customerRepository)
                .existsByEmail("amit@test.com");

        verify(customerRepository)
                .save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowException_whenCustomerNumberAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setCustomerNumber("CUST001");
        request.setFirstName("Amit");
        request.setLastName("Kumar");
        request.setEmail("amit@test.com");
        request.setPhone("9999999999");

        when(customerRepository.existsByCustomerNumber("CUST001"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository)
                .existsByCustomerNumber("CUST001");

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowException_whenEmailAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setCustomerNumber("CUST001");
        request.setFirstName("Amit");
        request.setLastName("Kumar");
        request.setEmail("amit@test.com");
        request.setPhone("9999999999");

        when(customerRepository.existsByCustomerNumber("CUST001"))
                .thenReturn(false);

        when(customerRepository.existsByEmail("amit@test.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository)
                .existsByEmail("amit@test.com");

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {

        Customer customer = new Customer();
        //customer.setId(1L);
        customer.setCustomerNumber("CUST001");
        customer.setFirstName("Amit");
        customer.setLastName("Kumar");
        customer.setEmail("amit@test.com");
        customer.setStatus("ACTIVE");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponse response =
                customerService.getCustomer(1L);

        assertNotNull(response);
        //assertEquals(1L, response.getId());
        assertEquals("CUST001", response.getCustomerNumber());
        assertEquals("Amit", response.getFirstName());
        assertEquals("ACTIVE", response.getStatus());

        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_shouldThrowException_whenCustomerNotFound() {

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(999L)
        );

        verify(customerRepository).findById(999L);
    }

    @Test
    void updateCustomer_shouldUpdateCustomerSuccessfully() {

        Customer existingCustomer = new Customer();
        //existingCustomer.setId(1L);
        existingCustomer.setCustomerNumber("CUST001");
        existingCustomer.setFirstName("Amit");
        existingCustomer.setLastName("Kumar");
        existingCustomer.setEmail("old@test.com");
        existingCustomer.setPhone("9999999999");
        existingCustomer.setStatus("ACTIVE");

        CustomerRequest request = new CustomerRequest();
        request.setCustomerNumber("CUST001");
        request.setFirstName("Amit");
        request.setLastName("Sharma");
        request.setEmail("new@test.com");
        request.setPhone("8888888888");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(existingCustomer));

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(existingCustomer);

        CustomerResponse response =
                customerService.updateCustomer(1L, request);

        assertNotNull(response);
        assertEquals("Amit", response.getFirstName());
        assertEquals("Sharma", response.getLastName());
        assertEquals("new@test.com", response.getEmail());
        assertEquals("8888888888", response.getPhone());

        verify(customerRepository).findById(1L);
        verify(customerRepository).save(existingCustomer);
    }



}