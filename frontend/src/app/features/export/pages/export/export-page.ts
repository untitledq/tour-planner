import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { SearchApiService } from '../../../../core/services/search-api.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { TourListResponse } from '../../../tours/models/tour.models';
import { TourApiService } from '../../../tours/services/tour-api.service';

@Component({
  selector: 'app-export-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    AppShellComponent,
    PaginationComponent,
  ],
  templateUrl: './export-page.html',
  styleUrl: './export-page.css',
})
export class ExportPage {
  protected readonly rows = 6;
  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected readonly searchTerm = signal('');
  protected readonly first = signal(0);
  protected readonly isLoadingTours = signal(true);
  protected readonly isSearchingTours = signal(false);
  protected readonly toursLoadError = signal('');
  protected readonly toursSearchError = signal('');
  protected readonly exportError = signal('');
  protected readonly tours = signal<TourListResponse[]>([]);
  protected readonly searchResultIds = signal<Set<string> | null>(null);
  protected readonly selectedTour = signal<TourListResponse | null>(null);
  protected readonly isExporting = signal(false);
  protected readonly filteredTours = computed(() => {
    const normalizedSearch = this.searchTerm().trim();

    if (normalizedSearch === '') {
      return this.tours();
    }

    const resultIds = this.searchResultIds();

    if (resultIds === null) {
      return [];
    }

    return this.tours().filter((tour) => resultIds.has(tour.id));
  });
  protected readonly visibleTours = computed(() => {
    const first = this.first();
    return this.filteredTours().slice(first, first + this.rows);
  });
  protected isLoggingOut = false;

  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);
  private readonly searchApi = inject(SearchApiService);
  private readonly tourApi = inject(TourApiService);

  constructor() {
    this.searchControl.valueChanges
      .pipe(debounceTime(250), distinctUntilChanged())
      .subscribe((value) => {
        this.searchTerm.set(value);
        this.first.set(0);
        this.searchTours(value);
      });

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
        },
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
        },
      });
  }

  protected selectTour(tour: TourListResponse): void {
    this.selectedTour.set(tour);
    this.exportError.set('');
  }

  protected onPageChange(event: { first?: number }): void {
    this.first.set(event.first ?? 0);
  }

  private searchTours(value: string): void {
    const normalizedSearch = value.trim();

    this.toursSearchError.set('');

    if (!normalizedSearch) {
      this.searchResultIds.set(null);
      this.isSearchingTours.set(false);
      return;
    }

    this.isSearchingTours.set(true);

    this.searchApi
      .search(normalizedSearch, 'tours')
      .pipe(finalize(() => this.isSearchingTours.set(false)))
      .subscribe({
        next: (results) => {
          this.searchResultIds.set(
            new Set(results.filter((result) => result.type === 'TOUR').map((result) => result.id)),
          );
        },
        error: () => {
          this.searchResultIds.set(new Set());
          this.toursSearchError.set('Tours could not be searched.');
        },
      });
  }

  protected exportSelectedTour(): void {
    const tour = this.selectedTour();

    if (!tour || this.isExporting()) {
      return;
    }

    this.isExporting.set(true);
    this.exportError.set('');

    this.tourApi
      .exportTour(tour.id)
      .pipe(finalize(() => this.isExporting.set(false)))
      .subscribe({
        next: (xmlBlob) => {
          this.downloadXml(xmlBlob, tour);
        },
        error: () => {
          this.exportError.set('Tour could not be exported.');
        },
      });
  }

  private downloadXml(xmlBlob: Blob, tour: TourListResponse): void {
    const downloadUrl = URL.createObjectURL(xmlBlob);
    const link = document.createElement('a');

    link.href = downloadUrl;
    link.download = `tour-${this.safeFileName(tour.name)}-${tour.id}.xml`;
    link.click();

    URL.revokeObjectURL(downloadUrl);
  }

  private safeFileName(value: string): string {
    return (
      value
        .trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '') || 'tour'
    );
  }
}
