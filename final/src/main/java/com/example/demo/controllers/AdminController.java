package com.example.demo.controllers;

import com.example.demo.service.IServices.IUserService;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "peniiz")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    IUserService userService;

    @PutMapping("/{id}/giveModerator")
    public ResponseEntity<?> giveModerator(@PathVariable(name = "id") Long id) {
        return userService.giveModerator(id);
    }
    @PutMapping("/{id}/takeModerator")
    public ResponseEntity<?> takeModerator(@PathVariable(name = "id") Long id) {
        return userService.takeModerator(id);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") Long id) {
        return userService.delete(id);
    }
}
