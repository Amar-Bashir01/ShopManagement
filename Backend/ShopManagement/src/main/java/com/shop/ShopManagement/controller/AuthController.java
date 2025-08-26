package com.shop.ShopManagement.controller;

import com.shop.ShopManagement.dto.LoginDTO;
import com.shop.ShopManagement.dto.RegisterDTO;
import com.shop.ShopManagement.dto.ResetPasswordDTO;
import com.shop.ShopManagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // constructor injection (no Lombok)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------------- Register ----------------
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDTO dto) {
        String message = authService.register(dto);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // ---------------- Forgot Password ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        String message = authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", message));
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String message = authService.verifyOtp(email, otp);
        return ResponseEntity.ok(Map.of("message", message));
    }
    // ---------------- Reset Password ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordDTO dto) {
        String message = authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", message));
    }
// ---------------- Test Endpoint (JWT Protected) ----------------
//    @GetMapping("/me")
//    public ResponseEntity<Map<String, String>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//        String username = authService.extractUsernameFromToken(token);
//        return ResponseEntity.ok(Map.of("username", username));
//    }
}
