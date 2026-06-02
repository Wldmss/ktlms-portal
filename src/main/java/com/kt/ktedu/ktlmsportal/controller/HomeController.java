package com.kt.ktedu.ktlmsportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        model.addAttribute("serverTime", now.format(formatter));
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("springVersion", "6.1.8");
        
        return "index";
    }
}
