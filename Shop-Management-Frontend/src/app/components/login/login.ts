import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../Services/auth-service';
import { MatSnackBar } from '@angular/material/snack-bar';

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

  constructor(private authService: AuthService, private router: Router,private snackBar: MatSnackBar,private cdr: ChangeDetectorRef) {}

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
           this.snackBar.open('Login successful!', 'Close', { duration: 3000 });
          this.router.navigate(['/dashboard']);
        },
        error: () => alert('Invalid credentials')
      });
  }

  // Register
onRegister() {
  if (!this.name || !this.email || !this.password) {
     this.snackBar.open('All fields manatory!', 'Close', { duration: 3000 });
    return;
  }

  this.authService.register({
    username: this.name.trim(),   // send 'username', not 'name'
    email: this.email.trim(),
    password: this.password
  }).subscribe({
    next: () => {
      this.snackBar.open('User registered successfully!','Close',{duration:3000});
      this.switchTab('login');
      this.cdr.detectChanges();
    },
    error: () => this.snackBar.open('Registration failed','Close',{duration:3000})
  });
}

  // Forgot Password
  onForgotPassword() {
    if (!this.email) {
      this.snackBar.open('Email required','Close',{duration:3000});
      return;
    }
    this.authService.forgotPassword(this.email)
      .subscribe({
        next: (res) => {
          this.message = res.message;
          this.switchTab('verify');
          this.snackBar.open('OTP sent to your email','Close', {duration:3000});
          this.cdr.detectChanges();
        },
        error: () => alert('Failed to send OTP')
      });
  }
//verify Otp
onVerifyOtp() {
  if (!this.email || !this.otp) {
    this.snackBar.open('Enter email and OTP','Close',{duration:3000});
    return;
  }
  this.authService.verifyOtp(this.email, this.otp).subscribe({
    next: () => {
      this.switchTab('reset');
      this.snackBar.open('OTP verified successfully!','Close',{duration:3000});
      this.cdr.detectChanges();
    },
    error: () => alert('Invalid or expired OTP')
  });
}
  // Reset Password
  onResetPassword() {
    if (!this.email || !this.otp || !this.newPassword) {
      this.snackBar.open('All fields required','Close',{duration:3000});
      return;
    }
    this.authService.resetPassword({ email: this.email, otp: this.otp, newPassword: this.newPassword })
      .subscribe({
        next: () => {
          this.switchTab('login');
          this.snackBar.open('Password reset successful!','Close',{duration:3000});
          this.cdr.detectChanges();
        },
        error: () => alert('Reset failed')
      });
  }

  switchTab(tab: string) {
    console.log('Switch tab:', tab);
    this.currentTab = tab;
    this.message = '';
    this.password = '';
    this.newPassword = '';
    this.otp = '';
  }
}

// Problem : whereever 'alert' and 'switch tab' are together, the alert being synchronous operation blocks 'switch tab' from executing.
//Solutions: 1. Add timeout for alerts., 2. Use toasts instead of alerts.

//Problem : though using toast with timer helps log the switch tab, it is suppressed by the toast message and change of route goes undetected.
//Solution : use cdr(changeDetectOrRef) -> this.cdr.detectChanges()