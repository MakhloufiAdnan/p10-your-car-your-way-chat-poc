import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
* Ajoute les credentials uniquement sur les appels API du backend.
*
* Le backend utilise une session HTTP ; il faut donc transmettre les cookies
* sur les requêtes REST pour conserver l'authentification côté navigateur.
*/
export const apiCredentialsInterceptor: HttpInterceptorFn = (request, next) => {
  const isApiRequest = request.url.startsWith(environment.apiUrl);

  if (!isApiRequest) {
    return next(request);
  }

  return next(
    // `withCredentials` permet au navigateur d'envoyer le cookie de session Spring Security.
    request.clone({
      withCredentials: true,
    }),
  );
};
