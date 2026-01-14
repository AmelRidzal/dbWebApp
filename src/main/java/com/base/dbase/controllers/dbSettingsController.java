package com.base.dbase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dbSettingsController {

    @GetMapping("/dbSettings")
    public String getHomePage(Model model) {
        return "dbSettings";
    }
}
