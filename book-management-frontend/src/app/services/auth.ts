import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = '/api'; // Use proxy configuration

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }) {
    console.log('Attempting login with:', credentials.username);
    return this.http.post<{ token: string }>(`${this.baseUrl}/login`, credentials);
  }

  storeToken(token: string) {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/login']);
  }
}
