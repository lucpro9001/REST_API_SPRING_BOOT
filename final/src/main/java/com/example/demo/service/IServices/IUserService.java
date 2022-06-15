package com.example.demo.service.IServices;

import org.springframework.http.ResponseEntity;

import com.example.demo.models.User;
import com.example.demo.payload.request.ResetPWRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.security.service.UserDetailsImpl;

public interface IUserService {
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateUser(User user);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> add(SignupRequest signUpRequest);
    ResponseEntity<?> giveModerator(Long id);
    ResponseEntity<?> takeModerator(Long id);
    ResponseEntity<?> resetPass(ResetPWRequest request, UserDetailsImpl user);
}
