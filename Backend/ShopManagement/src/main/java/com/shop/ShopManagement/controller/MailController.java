package com.shop.ShopManagement.controller;

import com.shop.ShopManagement.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send")
    public String sendMail() {
        emailService.sendEmail("amarumeh113118@gmail.com", "Test Subject", "Hello from Spring Boot!");
        return "Email sent!";
    }
}

