package com.base.dbase.services;

import com.base.dbase.model.Customer;
import com.base.dbase.repository.CustomerRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private final CustomerRepo repo;
    private final EntityManager entityManager;
    private final BackupService backupService;   // ← add this

    public CustomerService(CustomerRepo repo, EntityManager entityManager,
                           BackupService backupService) {
        this.repo = repo;
        this.entityManager = entityManager;
        this.backupService = backupService;       // ← add this
    }

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    public Customer addCustomer(Customer customer) {
        Customer saved = repo.save(customer);
        backupService.bufferChange(toMap(saved));  // ← buffer the insert
        return saved;
    }

    public Customer getCustomerById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public boolean deleteCustomer(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            // ← NO buffer call here — deletes are not backed up
            return true;
        }
        return false;
    }

    public Customer updateCustomer(Long id, Customer newData) {
        return repo.findById(id).map(existing -> {
            existing.setName(newData.getName());
            existing.setPhoneNumber(newData.getPhoneNumber());
            existing.setDateCreated(newData.getDateCreated());
            existing.setProblemDescription(newData.getProblemDescription());
            Customer saved = repo.save(existing);
            backupService.bufferChange(toMap(saved));  // ← buffer the update
            return saved;
        }).orElse(null);
    }

    public Object runQuery(String queryText) {
        try {
            Query query = entityManager.createNativeQuery(queryText);
            return query.getResultList();
        } catch (Exception e) {
            return "Query error: " + e.getMessage();
        }
    }

    // Helper to convert Customer entity → Map for the buffer
    private Map<String, Object> toMap(Customer c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("name", c.getName());
        map.put("phone_number", c.getPhoneNumber());
        map.put("date_created", c.getDateCreated());
        map.put("problem_description", c.getProblemDescription());
        return map;
    }
}