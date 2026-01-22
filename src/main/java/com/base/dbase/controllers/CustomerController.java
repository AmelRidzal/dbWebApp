package com.base.dbase.controllers;

import com.base.dbase.model.Customer;
import com.base.dbase.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    // Dependency injection
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET request to get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // POST request to add a new customer
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    // GET request to get customers by id
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // DELETE request to delete customers by id
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return customerService.deleteCustomer(id)
                ? "Customer deleted"
                : "Customer not found";
    }

    // PUT request to update a customer
    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customer
    ) {
        return customerService.updateCustomer(id, customer);
    }

    // POST request to send a query
    @PostMapping("/query")
    public Object runQuery(@RequestBody Map<String,String> body) {
        return customerService.runQuery(body.get("query"));
    }

}
