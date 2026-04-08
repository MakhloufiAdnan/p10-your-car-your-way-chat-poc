import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../../features/auth/login/login-request';
import { LoginResponse } from '../../features/auth/login/login-response';
import { environment } from '../../../environments/environment';

const AUTH_STORAGE_KEY = 'chatpoc_current_user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;
  
  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, payload).pipe(
      tap((user) => {
        sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
      })
    );
  }

  getCurrentUser(): LoginResponse | null {
    const raw = sessionStorage.getItem(AUTH_STORAGE_KEY);
    return raw ? (JSON.parse(raw) as LoginResponse) : null;
  }

  isAuthenticated(): boolean {
    return this.getCurrentUser() !== null;
  }

  logout(): void {
    sessionStorage.removeItem(AUTH_STORAGE_KEY);
    this.router.navigate(['/login']);
  }
}
