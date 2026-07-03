package com.dlms.controller;

import com.dlms.dto.ApiResponse;
import com.dlms.dto.LoginRequest;
import com.dlms.dto.RegisterRequest;
import com.dlms.model.User;
import com.dlms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    
    private final AuthenticationManager authenticationManager;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            User user = userService.registerUser(request);
            user.setPasswordHash("[HIDDEN]"); // NEVER expose password hash
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Registration successful", user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            // 1. Actually authenticate the user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. Set the authenticated user in the Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Save the context to the session so the user stays logged in across pages
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

           // 4. Extract the true, verified role from Spring Security
            String verifiedRole = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .orElse("APPLICANT"); 

            // Prepare the user data to send back to the frontend
            Map<String, String> userData = new HashMap<>();
            userData.put("email", request.getEmail());
            userData.put("role", verifiedRole); 

            // 5. Return the 200 OK response with the expected JSON structure
            return ResponseEntity.ok(ApiResponse.ok("Login successful", userData));

        } catch (BadCredentialsException e) {
            // This catches wrong passwords
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid email or password"));
                    
        } catch (Exception e) {
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Backend Error: " + e.getMessage()));
        }
    }
}