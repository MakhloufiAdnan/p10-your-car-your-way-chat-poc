import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../../features/auth/login/login-request';
import { LoginResponse } from '../../features/auth/login/login-response';
import { environment } from '../../../environments/environment';

const AUTH_STORAGE_KEY = 'chatpoc_current_user';

/**
* Gère l'état d'authentification côté frontend.
*
* Le backend reste la source de vérité via la session serveur.
* Le sessionStorage sert ici à conserver les informations utiles à l'UI
* pendant la navigation de la PoC.
*/
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;

  /**
  * Ouvre une session côté backend puis conserve les informations utilisateur
  * nécessaires à l'interface dans le stockage de session du navigateur.
  */
  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, payload).pipe(
      tap((user) => {
        sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
      }),
    );
  }

  /**
  * Retourne l'utilisateur courant vu par l'interface.
  *
  * Cette information reflète l'état stocké côté frontend,
  * pas une revalidation temps réel de la session serveur.
  */
  getCurrentUser(): LoginResponse | null {
    const raw = sessionStorage.getItem(AUTH_STORAGE_KEY);
    return raw ? (JSON.parse(raw) as LoginResponse) : null;
  }

  isAuthenticated(): boolean {
    return this.getCurrentUser() !== null;
  }

  /**
  * Nettoie l'état local d'authentification et redirige vers l'écran de login.
  *
  * Dans cette PoC, la déconnexion côté navigateur est suffisante pour la démonstration.
  */
  logout(): void {
    sessionStorage.removeItem(AUTH_STORAGE_KEY);
    void this.router.navigate(['/login']);
  }
}
