package com.base.dbase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dbManagerController {

    @GetMapping("/dbManager")
    public String getHomePage(Model model) {
        return "dbManager";
    }
}