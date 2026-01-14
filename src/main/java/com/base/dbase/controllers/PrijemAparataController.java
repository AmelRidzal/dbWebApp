package com.base.dbase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrijemAparataController {

    @GetMapping("/prijemAparata")
    public String getHomePage(Model model) {
        return "prijemAparata";
    }
}
