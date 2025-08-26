package com.shop.ShopManagement.service;

import com.shop.ShopManagement.dto.LoginDTO;
import com.shop.ShopManagement.dto.RegisterDTO;
import com.shop.ShopManagement.dto.ResetPasswordDTO;
import com.shop.ShopManagement.entity.JwtUtil;
import com.shop.ShopManagement.entity.User;
import com.shop.ShopManagement.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    @Getter
    private static class OtpDetails {
        private final String otp;
        private final long expiryTime;
        @Setter
        private boolean verified;

        public OtpDetails(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
            this.verified = false;
        }
    }

    private final ConcurrentHashMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        // generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(100000));
        long expiry = System.currentTimeMillis() + (2*60*1000);
        otpStorage.put(email, new OtpDetails(otp, expiry));

        // send OTP via email
        emailService.sendEmail(email, "Password Reset OTP", "Your OTP is: " + otp);

        return "OTP sent to your email.";
    }
    //verify otp
    public String verifyOtp(String email, String otp){
        OtpDetails details = otpStorage.get(email);
        if(details == null)
            throw new RuntimeException("No OTP found, please request again");
        if (System.currentTimeMillis() > details.getExpiryTime()) {
            otpStorage.remove(email);
            throw new RuntimeException("OTP expired. Request a new one.");
        }
        if (!details.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP.");

        details.setVerified(true);//it got verified
        return "OTP verified successfully.";
    }
    // ---------------- Reset Password ----------------
    public String resetPassword(ResetPasswordDTO dto) {
        OtpDetails details = otpStorage.get(dto.getEmail());
        if (details == null || !details.isVerified()) {
            throw new RuntimeException("OTP not verified or expired");
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
