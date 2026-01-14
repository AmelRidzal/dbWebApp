package com.base.dbase.services;

import com.base.dbase.model.Customer;
import com.base.dbase.repository.CustomerRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepo repo;

    public CustomerService(CustomerRepo repo) {
        this.repo = repo;
    }

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    public Customer addCustomer(Customer customer) {
        return repo.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public boolean deleteCustomer(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // UPDATE customer
    public Customer updateCustomer(Long id, Customer newData) {
        return repo.findById(id).map(existing -> {
            existing.setFirstName(newData.getFirstName());
            existing.setLastName(newData.getLastName());
            existing.setPhoneNumber(newData.getPhoneNumber());
            existing.setDateCreated(newData.getDateCreated());
            existing.setProblemDescription(newData.getProblemDescription());
            return repo.save(existing);
        }).orElse(null);
    }
}
