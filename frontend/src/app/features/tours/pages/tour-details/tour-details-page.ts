import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { finalize } from 'rxjs';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { TourResponse, TourType } from '../../models/tour.models';
import { TourApiService } from '../../services/tour-api.service';

@Component({
  selector: 'app-tour-details-page',
  standalone: true,
  imports: [ButtonModule, BreadcrumbModule, AppShellComponent],
  templateUrl: './tour-details-page.html',
  styleUrl: './tour-details-page.css'
})
export class TourDetailsPage {
  protected readonly isLoadingTour = signal(true);
  protected readonly tourLoadError = signal('');
  protected readonly tour = signal<TourResponse | null>(null);
  protected readonly breadcrumbItems = signal<MenuItem[]>([
    { label: 'Tours', routerLink: '/tours' }
  ]);
  protected isLoggingOut = false;

  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tourApi = inject(TourApiService);

  constructor() {
    const tourId = this.route.snapshot.paramMap.get('id');

    if (!tourId) {
      this.tourLoadError.set('Tour ID is missing.');
      this.isLoadingTour.set(false);
      return;
    }

    this.tourApi
      .getById(tourId)
      .pipe(finalize(() => this.isLoadingTour.set(false)))
      .subscribe({
        next: (tour) => {
          this.tour.set(tour);
          this.breadcrumbItems.set([
            { label: 'Tours', routerLink: '/tours' },
            { label: tour.name || tour.id }
          ]);
        },
        error: () => {
          this.tourLoadError.set('Tour details could not be loaded.');
          this.tour.set(null);
        }
      });
  }

  protected logout(): void {
    this.isLoggingOut = true;

    this.authApi
      .logout()
      .pipe(finalize(() => (this.isLoggingOut = false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/login');
        },
        error: () => {
          this.authSession.clearToken();
          void this.router.navigateByUrl('/login');
        }
      });
  }

  protected goBack(): void {
    void this.router.navigateByUrl('/tours');
  }

  protected tourTypeLabel(value: TourType): string {
    switch (value) {
      case 'BIKE':
        return 'Bike';
      case 'HIKE':
        return 'Hike';
      case 'RUNNING':
        return 'Running';
      case 'VACATION':
        return 'Vacation';
    }
  }
}
