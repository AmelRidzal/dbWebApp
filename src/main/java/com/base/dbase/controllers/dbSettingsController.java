package com.base.dbase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
public class dbSettingsController {

    private static final String FILE_PATH = "src/main/resources/application.properties";


    @GetMapping("/dbSettings")
    public String getHomePage() {
        return "dbSettings";
    }

    @PostMapping("/api/db/settings")
    @ResponseBody
    public String saveSettings(@RequestBody Map<String, String> body) throws IOException {
        String target = body.get("target"); // "primary" or "secondary"
        String url = body.get("url");
        String user = body.get("user");
        String pass = body.get("password");

        String prefix = "spring.datasource." + target + ".";

        List<String> lines = Files.readAllLines(Path.of(FILE_PATH));
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith(prefix + "jdbc-url="))
                newLines.add(prefix + "jdbc-url=" + url);
            else if (line.startsWith(prefix + "username="))
                newLines.add(prefix + "username=" + user);
            else if (line.startsWith(prefix + "password="))
                newLines.add(prefix + "password=" + pass);
            else
                newLines.add(line);
        }

        Files.write(Path.of(FILE_PATH), newLines);
        return "Saved settings for " + target + " database. Restart app to apply.";
    }



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
