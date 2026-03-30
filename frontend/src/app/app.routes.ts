import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { pendingChangesGuard } from './core/guards/pending-changes.guard';
import { LoginPage } from './features/auth/pages/login/login-page';
import { SigninPage } from './features/auth/pages/signin/signin-page';
import { DashboardPage } from './features/dashboard/pages/dashboard/dashboard-page';
import { ExportPage } from './features/export/pages/export/export-page';
import { ImportPage } from './features/import/pages/import/import-page';
import { TourLogsPage } from './features/tour-logs/pages/tour-logs/tour-logs-page';
import { CreateTourDetailsPage } from './features/tours/pages/create-tour/create-tour-details-page';
import { CreateTourSummaryPage } from './features/tours/pages/create-tour-summary/create-tour-summary-page';
import { ToursPage } from './features/tours/pages/tours/tours-page';

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
  },
  {
    path: 'tours',
    component: ToursPage,
    canActivate: [authGuard]
  },
  {
    path: 'tours/create/details',
    component: CreateTourDetailsPage,
    canActivate: [authGuard],
    canDeactivate: [pendingChangesGuard]
  },
  {
    path: 'tours/create/summary',
    component: CreateTourSummaryPage,
    canActivate: [authGuard],
    canDeactivate: [pendingChangesGuard]
  },
  {
    path: 'tour-logs',
    component: TourLogsPage,
    canActivate: [authGuard]
  },
  {
    path: 'export',
    component: ExportPage,
    canActivate: [authGuard]
  },
  {
    path: 'import',
    component: ImportPage,
    canActivate: [authGuard]
  }
];
