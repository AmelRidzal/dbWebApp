package com.base.dbase.repository;

import com.base.dbase.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepo extends JpaRepository<Customer, Long> {
}
