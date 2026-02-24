package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.dto.AuthLoginRequest;
import at.htl.fitnesscenter.dto.AuthRegisterRequest;
import at.htl.fitnesscenter.dto.AuthResponse;
import at.htl.fitnesscenter.exception.ConflictException;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.User;
import at.htl.fitnesscenter.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        User user = new User();
        user.setFirstname(request.getFirstName() == null ? "User" : request.getFirstName());
        user.setLastname(request.getLastName() == null ? "Account" : request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(user.getEmail()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }
        return new AuthResponse(user.getEmail());
    }
}
