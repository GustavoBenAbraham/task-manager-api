package com.gustavo.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Task Manager API");
        response.put("status", "Online");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "health", "/api/v1/health",
            "register", "/api/v1/auth/register",
            "login", "/api/v1/auth/login",
            "tasks", "/api/v1/tasks",
            "tasks_paginated", "/api/v1/tasks/paginado?page=0&size=10"
        ));
        response.put("documentation", "Use POST /api/v1/auth/register to create a user, then POST /api/v1/auth/login to get a JWT token");
        return response;
    }
}
