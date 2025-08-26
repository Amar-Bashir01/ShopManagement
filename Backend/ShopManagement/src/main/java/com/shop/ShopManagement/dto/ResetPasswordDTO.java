package com.shop.ShopManagement.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    // Getters & Setters
    private String email;
    private String otp;
    private String newPassword;

}
