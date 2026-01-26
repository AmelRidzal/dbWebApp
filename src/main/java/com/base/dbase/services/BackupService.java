package com.base.dbase.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BackupService {

    @Qualifier("primaryJdbc")
    @Autowired
    private JdbcTemplate primaryJdbc;

    @Qualifier("secondaryJdbc")
    @Autowired
    private JdbcTemplate secondaryJdbc;

    public void syncUsers() {
        List<Map<String, Object>> customers =
                primaryJdbc.queryForList("SELECT * FROM customers");

        for (Map<String, Object> c : customers) {
            secondaryJdbc.update("""
                INSERT INTO customers
                (id, name, phone_number, date_created, problem_description)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    phone_number = EXCLUDED.phone_number,
                    date_created = EXCLUDED.date_created,
                    problem_description = EXCLUDED.problem_description
            """,
                    c.get("id"),
                    c.get("name"),
                    c.get("phone_number"),
                    c.get("date_created"),
                    c.get("problem_description")
            );
        }
    }
}
