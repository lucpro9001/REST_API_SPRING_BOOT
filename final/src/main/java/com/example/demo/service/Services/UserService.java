package com.example.demo.service.Services;

import java.util.Set;

import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.payload.request.ResetPWRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public ResponseEntity<?> getById(Long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: User id is not found"),
                    HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateUser(User user) {
        var temp = userRepository.findById(user.getId());
        if (temp.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: User id is not found"),
                    HttpStatus.NOT_FOUND);
        var u = temp.get();
        var res = userRepository.save(u);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        var temp = userRepository.findById(id);
        if (temp.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: User id is not found"),
                HttpStatus.NOT_FOUND);
        var user = temp.get();
        userRepository.delete(user);
        return new ResponseEntity<>(new MessageResponse("Delete user successfully!"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> add(SignupRequest signUpRequest) {
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        var res = userRepository.save(user);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> giveModerator(Long id) {
        var query = userRepository.findById(id);
        if (query.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: User id is not found"),
                HttpStatus.NOT_FOUND);
        var user = query.get();
        var roles = user.getRoles();
        for (Role role : roles) {
            if (role.getName().equals(ERole.ROLE_MODERATOR))
                return new ResponseEntity<>(user, HttpStatus.OK);
        }
        var role = roleRepository.findByName(ERole.ROLE_MODERATOR).get();
        roles.add(role);
        user.setRoles(roles);
        var res = userRepository.save(user);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> takeModerator(Long id) {
        var query = userRepository.findById(id);
        if (query.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: User id is not found"),
                HttpStatus.NOT_FOUND);
        var user = query.get();
        var roles = user.getRoles();
        for (Role role : roles) {
            if (role.getName().equals(ERole.ROLE_MODERATOR)) {
                roles.remove(role);
                user.setRoles(roles);
                var res = userRepository.save(user);
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> resetPass(ResetPWRequest request, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        if (!BCrypt.checkpw(request.getCurrentPassword(), user.getPassword()))
            return new ResponseEntity<>(
                new MessageResponse("Error: Current password is not match!"),
                HttpStatus.BAD_REQUEST);
        user.setPassword(encoder.encode(request.getNewPassword()));
        var res = userRepository.save(user);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
