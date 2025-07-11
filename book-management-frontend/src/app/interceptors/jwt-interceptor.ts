import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    console.log('JWT Interceptor - Request URL:', req.url);
    console.log('JWT Interceptor - Token exists:', !!token);

    if (token) {
      console.log('JWT Interceptor - Adding Authorization header');
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(cloned);
    }

    console.log('JWT Interceptor - No token, proceeding without auth header');
    return next.handle(req);
  }
}
