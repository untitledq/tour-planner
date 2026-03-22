import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  token: string;
  expires_at: string;
  session_id: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/user';
  private readonly tokenStorageKey = 'tourplanner.auth.token';

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, payload)
      .pipe(tap((response) => localStorage.setItem(this.tokenStorageKey, response.token)));
  }

  logout(): Observable<void> {
    const token = this.getToken();
    const headers = token
      ? new HttpHeaders({
          Authorization: `Bearer ${token}`
        })
      : undefined;

    return this.http
      .post<void>(`${this.apiUrl}/logout`, {}, { headers })
      .pipe(tap(() => this.clearToken()));
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenStorageKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenStorageKey);
  }
}
