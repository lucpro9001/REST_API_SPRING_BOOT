package com.example.demo.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.payload.request.ResetPWRequest;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "peniiz")
@PreAuthorize("isAuthenticated()")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/me")
	public ResponseEntity<?> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
        return new ResponseEntity< >(currentUser, HttpStatus.OK);
	}
    @GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable(value = "id") Long id) {
        return userService.getById(id);
	}
    @PutMapping("/me/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPWRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
        return userService.resetPass(request, currentUser);
    }
}
