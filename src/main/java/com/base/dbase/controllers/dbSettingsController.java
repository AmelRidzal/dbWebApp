package com.base.dbase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
public class dbSettingsController {

    private static final String FILE_PATH = "src/main/resources/application.properties";

    // Serve page
    @GetMapping("/dbSettings")
    public String getHomePage() {
        return "dbSettings";
    }

    // Save DB settings
    @PostMapping("/api/db/settings")
    @ResponseBody
    public String saveSettings(@RequestBody Map<String, String> body) throws IOException {
        String appName = body.get("name");
        String url = body.get("url");
        String user = body.get("user");
        String pass = body.get("password");

        List<String> lines = Files.readAllLines(Path.of(FILE_PATH));
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("spring.application.name="))
                newLines.add("spring.application.name=" + appName);
            else if (line.startsWith("spring.datasource.url="))
                newLines.add("spring.datasource.url=" + url);
            else if (line.startsWith("spring.datasource.username="))
                newLines.add("spring.datasource.username=" + user);
            else if (line.startsWith("spring.datasource.password="))
                newLines.add("spring.datasource.password=" + pass);
            else
                newLines.add(line);
        }

        Files.write(Path.of(FILE_PATH), newLines);
        return "Settings saved. Restart app to apply.";
    }

    // Test DB connection
    @PostMapping("/api/db/test")
    @ResponseBody
    public String testConnection(@RequestBody Map<String, String> body) {
        try {
            String url = body.get("url");
            String user = body.get("user");
            String pass = body.get("password");

            java.sql.Connection conn =
                    java.sql.DriverManager.getConnection(url, user, pass);
            conn.close();
            return "Connection OK!";
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
