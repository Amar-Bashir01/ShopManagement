import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../Services/auth-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
  standalone: false   // 👈 classic component
})
export class Login {
  currentTab: string = 'login';
  email: string = '';
  password: string = '';
  name: string = '';
  otp: string = '';
  newPassword: string = '';
  message: string = '';
  username: any;

  constructor(private authService: AuthService, private router: Router) {}

  // Login
  onLogin() {
    if (!this.email || !this.password) {
      alert('Please fill in email and password');
      return;
    }
    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: (res) => {
          localStorage.setItem('token', res.token);
          alert('Login successful!');
          this.router.navigate(['/dashboard']);
        },
        error: () => alert('Invalid credentials')
      });
  }

  // Register
onRegister() {
  if (!this.name || !this.email || !this.password) {
    alert('All fields required');
    return;
  }

  this.authService.register({
    username: this.name.trim(),   // send 'username', not 'name'
    email: this.email.trim(),
    password: this.password
  }).subscribe({
    next: () => {
      alert('User registered successfully!');
      this.switchTab('login');
    },
    error: () => alert('Registration failed')
  });
}

  // Forgot Password
  onForgotPassword() {
    if (!this.email) {
      alert('Email required');
      return;
    }
    this.authService.forgotPassword(this.email)
      .subscribe({
        next: (res) => {
          this.message = res.message;
          alert('OTP sent to your email');
          this.switchTab('verify');
        },
        error: () => alert('Failed to send OTP')
      });
  }
//verify Otp
onVerifyOtp() {
  if (!this.email || !this.otp) {
    alert('Enter email and OTP');
    return;
  }
  this.authService.verifyOtp(this.email, this.otp).subscribe({
    next: () => {
      alert('OTP verified successfully!');
      this.switchTab('reset');
    },
    error: () => alert('Invalid or expired OTP')
  });
}
  // Reset Password
  onResetPassword() {
    if (!this.email || !this.otp || !this.newPassword) {
      alert('All fields required');
      return;
    }
    this.authService.resetPassword({ email: this.email, otp: this.otp, newPassword: this.newPassword })
      .subscribe({
        next: () => {
          alert('Password reset successful!');
          this.switchTab('login');
        },
        error: () => alert('Reset failed')
      });
  }

  // Switch tabs
  switchTab(tab: string) {
    console.log('Switching tab to:', tab);
    this.currentTab = tab;
    this.message = '';
    this.password = '';
    this.newPassword = '';
    this.otp = '';
  }
}
