import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { ChatPageComponent } from './features/chat/chat-page/chat-page.component';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'chat', component: ChatPageComponent, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
