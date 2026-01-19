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
        List<Map<String, Object>> users =
                primaryJdbc.queryForList("SELECT * FROM users");

        for (Map<String, Object> u : users) {
            secondaryJdbc.update("""
                INSERT INTO users(id, name, email)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    email = EXCLUDED.email
            """,
                    u.get("id"),
                    u.get("name"),
                    u.get("email")
            );
        }
    }
}
