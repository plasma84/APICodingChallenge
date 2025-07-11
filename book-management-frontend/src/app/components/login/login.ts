import { Component } from '@angular/core';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html'
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    console.log('Login attempt with username:', this.username);
    this.errorMessage = ''; // Clear previous errors
    
    this.authService.login({ username: this.username, password: this.password })
      .subscribe({
        next: (res: { token: string }) => {
          console.log('Login successful, token received');
          this.authService.storeToken(res.token);
          this.router.navigate(['/books']); // Redirect to book list
        },
        error: (err: any) => {
          console.error('Login error:', err);
          this.errorMessage = err.error?.message || 'Invalid username or password';
        }
      });
  }
}
