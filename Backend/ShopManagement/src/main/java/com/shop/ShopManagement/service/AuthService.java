package com.shop.ShopManagement.service;

import com.shop.ShopManagement.dto.LoginDTO;
import com.shop.ShopManagement.dto.RegisterDTO;
import com.shop.ShopManagement.dto.ResetPasswordDTO;
import com.shop.ShopManagement.entity.JwtUtil;
import com.shop.ShopManagement.entity.User;
import com.shop.ShopManagement.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // in-memory OTP store (better use Redis or DB in real prod)
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // ---------------- Register ----------------
    public String register(RegisterDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return "Email already exists!";
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "Username already exists!";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    // ---------------- Login ----------------
    public String login(LoginDTO dto) {
        Optional<User> userOpt = Optional.empty();

        // if email is provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            userOpt = userRepository.findByEmail(dto.getEmail());
        }
        // else if username is provided
        else if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            userOpt = userRepository.findByUsername(dto.getUsername());
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or email");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // generate token with username (safer than email)
        return jwtUtil.generateToken(user.getUsername());
    }


    // ---------------- Forgot Password (send OTP) ----------------
    public String forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);

        // send OTP via email
        emailService.sendEmail(email, "Password Reset OTP", "Your OTP is: " + otp);

        return "OTP sent to your email.";
    }

    // ---------------- Reset Password ----------------
    public String resetPassword(ResetPasswordDTO dto) {
        String savedOtp = otpStorage.get(dto.getEmail());
        if (savedOtp == null || !savedOtp.equals(dto.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        otpStorage.remove(dto.getEmail()); // clear OTP after use
        return "Password updated successfully!";
    }
    public String extractUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

}
