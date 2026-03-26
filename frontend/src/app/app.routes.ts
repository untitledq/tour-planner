import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginPage } from './features/auth/pages/login/login-page';
import { SigninPage } from './features/auth/pages/signin/signin-page';
import { DashboardPage } from './features/dashboard/pages/dashboard/dashboard-page';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: 'login',
    component: LoginPage
  },
  {
    path: 'signin',
    component: SigninPage
  },
  {
    path: 'dashboard',
    component: DashboardPage,
    canActivate: [authGuard]
  }
];
