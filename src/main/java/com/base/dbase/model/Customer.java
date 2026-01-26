package com.base.dbase.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    // --- Constructors ---
    public Customer() {}

    public Customer(String name, String phoneNumber,
                    LocalDate dateCreated, String problemDescription) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateCreated = dateCreated;
        this.problemDescription = problemDescription;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateCreated=" + dateCreated +
                ", problemDescription='" + problemDescription + '\'' +
                '}';
    }
}
