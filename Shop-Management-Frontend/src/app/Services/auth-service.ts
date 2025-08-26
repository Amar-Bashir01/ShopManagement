// src/app/auth/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private API_URL = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, data);
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.API_URL}/forgot-password?email=${email}`, {});
  }
verifyOtp(email: string, otp: string) {
  return this.http.post<any>(`${this.API_URL}/verify-otp?email=${email}&otp=${otp}`, {});
}

  resetPassword(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/reset-password`, data);
  }
}
