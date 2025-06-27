package com.bookstore.controller;

import com.bookstore.dto.JwtResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.User;
import com.bookstore.security.JwtUtils;
import com.bookstore.service.UserPrincipal;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    public AuthController(AuthenticationManager authenticationManager, 
                         UserService userService, 
                         JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }
    
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        
        JwtResponse jwtResponse = new JwtResponse(
            jwt,
            userDetails.getUsername(),
            userDetails.getEmail(),
            userDetails.getAuthorities().iterator().next().getAuthority()
        );
        
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticateUser(loginRequest);
    }
    
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "This is a POST endpoint for user login");
        info.put("method", "POST");
        info.put("contentType", "application/json");
        info.put("example", "{\"username\":\"admin\",\"password\":\"admin123\"}");
        return ResponseEntity.ok(info);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.createUser(registerRequest);
        return new ResponseEntity<>("User registered successfully: " + user.getUsername(), HttpStatus.CREATED);
    }
}
