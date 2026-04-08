package com.base.dbase.controllers;

import com.base.dbase.services.PrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class PrijemAparataController {

    @GetMapping("/prijemAparata")
    public String getHomePage(Model model) {
        return "prijemAparata";
    }


    @Autowired
    private PrintService printService;

    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<String> print(@RequestBody Map<String, String> data) {
        boolean success = printService.printReceipt(
                data.get("name"),
                data.get("phone"),
                data.get("date"),
                data.get("problem")
        );
        return success
                ? ResponseEntity.ok("Printed")
                : ResponseEntity.status(500).body("Printer not found");
    }
}
