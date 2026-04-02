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
import { TourType } from '../../models/tour.models';
import { CreateTourFlowService } from '../../services/create-tour-flow.service';

interface TourListItem {
  id: string;
  name: string;
  description: string;
  tourType: TourType;
  startName: string;
  endName: string;
  distanceKm: number;
  estimatedTime: string;
}

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

  protected readonly mockTours = signal<TourListItem[]>([
    {
      id: 'tour-001',
      name: 'Vienna City Center Tour',
      description: 'Historic city walk from Stephansdom to Schoenbrunn.',
      tourType: 'HIKE',
      startName: 'Stephansdom',
      endName: 'Schoenbrunn',
      distanceKm: 5.4,
      estimatedTime: '2h'
    },
    {
      id: 'tour-002',
      name: 'Danube Riverside Ride',
      description: 'Relaxed bike route along the Danube canal.',
      tourType: 'BIKE',
      startName: 'Schwedenplatz',
      endName: 'Donauinsel',
      distanceKm: 11.2,
      estimatedTime: '55 min'
    },
    {
      id: 'tour-003',
      name: 'Imperial Sights Drive',
      description: 'Short vacation route between the main imperial landmarks.',
      tourType: 'VACATION',
      startName: 'Belvedere',
      endName: 'Hofburg',
      distanceKm: 8.1,
      estimatedTime: '28 min'
    },
    {
      id: 'tour-004',
      name: 'Prater Evening Hike',
      description: 'Easy evening hike through the Prater area.',
      tourType: 'HIKE',
      startName: 'Praterstern',
      endName: 'Lusthaus',
      distanceKm: 4.8,
      estimatedTime: '1h 20 min'
    },
    {
      id: 'tour-005',
      name: 'Castle and Gardens Loop',
      description: 'Bike loop connecting palaces and urban green spots.',
      tourType: 'BIKE',
      startName: 'Schoenbrunn',
      endName: 'Volksgarten',
      distanceKm: 13.7,
      estimatedTime: '1h 10 min'
    },
    {
      id: 'tour-006',
      name: 'Ringstrasse Vacation Highlights',
      description: 'Compact vacation route around the classic Vienna landmarks.',
      tourType: 'VACATION',
      startName: 'Karlsplatz',
      endName: 'Rathausplatz',
      distanceKm: 6.3,
      estimatedTime: '24 min'
    },
    {
      id: 'tour-007',
      name: 'Old Town Running Tour',
      description: 'Compact running route through narrow streets and squares.',
      tourType: 'RUNNING',
      startName: 'Michaelerplatz',
      endName: 'Stadtpark',
      distanceKm: 3.6,
      estimatedTime: '1h'
    }
  ]);

  protected readonly filteredTours = computed(() => {
    const normalizedSearch = this.searchTerm().trim().toLowerCase();

    return this.mockTours().filter((tour) => {
      const matchesSearch =
        normalizedSearch === '' ||
        [
          tour.name,
          tour.description,
          tour.startName,
          tour.endName,
          this.tourTypeLabel(tour.tourType)
        ].some((value) => value.toLowerCase().includes(normalizedSearch));

      return matchesSearch;
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

  constructor() {
    this.searchControl.valueChanges.subscribe((value) => {
      this.searchTerm.set(value);
      this.first.set(0);
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

  protected openNewTour(): void {
    this.createTourFlow.startFlow();
    void this.router.navigateByUrl('/tours/create/details');
  }

  protected onPageChange(event: { first?: number; rows?: number }): void {
    this.first.set(event.first ?? 0);
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

  protected trackTour(_: number, tour: TourListItem): string {
    return tour.id;
  }
}
