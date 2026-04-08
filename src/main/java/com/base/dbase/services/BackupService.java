package com.base.dbase.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BackupService {

    @Qualifier("primaryJdbc")
    @Autowired
    private JdbcTemplate primaryJdbc;

    @Qualifier("secondaryJdbc")
    @Autowired
    private JdbcTemplate secondaryJdbc;

    @Qualifier("secondaryDataSource")
    @Autowired
    private DataSource secondaryDataSource;

    private final List<Map<String, Object>> pendingBuffer = new CopyOnWriteArrayList<>();

    private static final String BUFFER_FILE = "backup_buffer.json";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // On startup: load any leftover buffer from the previous session
    @PostConstruct
    public void loadBufferFromDisk() {
        File file = new File(BUFFER_FILE);
        if (!file.exists()) return;

        try {
            List<Map<String, Object>> saved = mapper.readValue(
                    file,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
            pendingBuffer.addAll(saved);
            System.out.println("[Backup] Loaded " + saved.size()
                    + " ops from disk buffer. Will push next cycle.");
        } catch (Exception e) {
            System.out.println("[Backup] Could not read buffer file: " + e.getMessage());
        }
    }

    // Save buffer to disk (call after every change and on shutdown)
    private void saveBufferToDisk() {
        try {
            mapper.writeValue(new File(BUFFER_FILE), pendingBuffer);
        } catch (Exception e) {
            System.out.println("[Backup] Could not save buffer to disk: " + e.getMessage());
        }
    }

    private void clearDiskBuffer() {
        new File(BUFFER_FILE).delete();
    }

    /** Call on every INSERT or UPDATE — never on DELETE */
    public void bufferChange(Map<String, Object> customerData) {
        pendingBuffer.add(customerData);
        saveBufferToDisk(); // persist immediately so no change is ever lost
    }

    /** Called by the scheduler every 15 min, and on shutdown */
    public void tryPushBuffer() {
        if (pendingBuffer.isEmpty()) {
            System.out.println("[Backup] Nothing to push.");
            return;
        }

        if (!isSecondaryReachable()) {
            System.out.println("[Backup] Secondary DB unreachable. "
                    + pendingBuffer.size() + " ops buffered. Retrying later.");
            saveBufferToDisk(); // make sure disk is up to date
            return;
        }

        List<Map<String, Object>> toPush = new ArrayList<>(pendingBuffer);
        pendingBuffer.clear();

        try {
            for (Map<String, Object> c : toPush) {
                // date_created deserializes from JSON as a map — convert it back to a string safely
                Object rawDate = c.get("date_created");
                String dateStr = null;
                if (rawDate instanceof String s) {
                    dateStr = s;
                } else if (rawDate instanceof Map<?, ?> dateMap) {
                    // Jackson deserializes LocalDate as {"year":X,"monthValue":X,"dayOfMonth":X}
                    int year  = ((Number) dateMap.get("year")).intValue();
                    int month = ((Number) dateMap.get("monthValue")).intValue();
                    int day   = ((Number) dateMap.get("dayOfMonth")).intValue();
                    dateStr = String.format("%04d-%02d-%02d", year, month, day);
                }

                // Convert the string date to java.sql.Date so Neon accepts it
                java.sql.Date sqlDate = null;
                if (dateStr != null) {
                    sqlDate = java.sql.Date.valueOf(dateStr); // expects "yyyy-MM-dd" which is exactly what's in JSON
                }

                secondaryJdbc.update(
                        "INSERT INTO customers (id, name, phone_number, date_created, problem_description) " +
                                "VALUES (?, ?, ?, ?, ?) " +
                                "ON CONFLICT (id) DO UPDATE SET " +
                                "name = EXCLUDED.name, " +
                                "phone_number = EXCLUDED.phone_number, " +
                                "date_created = EXCLUDED.date_created, " +
                                "problem_description = EXCLUDED.problem_description",
                        c.get("id"),
                        c.get("name"),
                        c.get("phone_number"),
                        sqlDate,                    // ← properly typed now
                        c.get("problem_description")
                );
            }
            clearDiskBuffer(); // push succeeded — no need to keep the file
            System.out.println("[Backup] Pushed " + toPush.size() + " records successfully.");
        } catch (Exception e) {
            pendingBuffer.addAll(toPush);
            saveBufferToDisk();
            System.out.println("[Backup] Push failed, re-queued " + toPush.size()
                    + " ops. Error: " + e.getMessage());
            e.printStackTrace(); // ← full stack trace with root cause
        }
    }

    private boolean isSecondaryReachable() {
        try (Connection conn = secondaryDataSource.getConnection()) {
            return conn.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }
}