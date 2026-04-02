import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { finalize } from 'rxjs';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { CreateTourFlowService } from '../../services/create-tour-flow.service';
import { TourListResponse } from '../../models/tour.models';
import { TourApiService } from '../../services/tour-api.service';

@Component({
  selector: 'app-tours-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    PaginatorModule,
    AppShellComponent
  ],
  templateUrl: './tours-page.html',
  styleUrl: './tours-page.css'
})
export class ToursPage {
  protected readonly rows = 5;
  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected readonly searchTerm = signal('');
  protected readonly first = signal(0);
  protected isLoggingOut = false;
  protected readonly isLoadingTours = signal(true);
  protected readonly toursLoadError = signal('');

  protected readonly tours = signal<TourListResponse[]>([]);

  protected readonly filteredTours = computed(() => {
    const normalizedSearch = this.searchTerm().trim().toLowerCase();

    return this.tours().filter((tour) => {
      return normalizedSearch === '' || tour.name.toLowerCase().includes(normalizedSearch);
    });
  });

  protected readonly visibleTours = computed(() => {
    const first = this.first();
    return this.filteredTours().slice(first, first + this.rows);
  });

  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);
  private readonly createTourFlow = inject(CreateTourFlowService);
  private readonly tourApi = inject(TourApiService);

  constructor() {
    this.searchControl.valueChanges.subscribe((value) => {
      this.searchTerm.set(value);
      this.first.set(0);
    });

    this.loadTours();
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

  protected openNewTour(): void {
    this.createTourFlow.startFlow();
    void this.router.navigateByUrl('/tours/create/details');
  }

  protected onPageChange(event: { first?: number; rows?: number }): void {
    this.first.set(event.first ?? 0);
  }

  protected trackTour(_: number, tour: TourListResponse): string {
    return tour.id;
  }

  private loadTours(): void {
    this.isLoadingTours.set(true);
    this.toursLoadError.set('');

    this.tourApi
      .list()
      .pipe(finalize(() => this.isLoadingTours.set(false)))
      .subscribe({
        next: (tours) => {
          this.tours.set(tours);
        },
        error: () => {
          this.toursLoadError.set('Tours could not be loaded.');
          this.tours.set([]);
        }
      });
  }
}
