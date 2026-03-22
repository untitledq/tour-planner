import { Routes } from '@angular/router';
import { LoginForm } from './features/auth/login-form/login-form';
import { Dashboard } from './features/dashboard/dashboard';
import { SigninForm } from './features/auth/signin-form/signin-form';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: 'login',
    component: LoginForm
  },
  {
    path: 'signin',
    component: SigninForm
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard]
  }
];
