import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { finalize, forkJoin } from 'rxjs';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { SearchApiService } from '../../../../core/services/search-api.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { FavoriteToggleComponent } from '../../../../shared/components/favorite-toggle/favorite-toggle';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { CreateTourFlowService } from '../../services/create-tour-flow.service';
import {
  TourChildFriendliness,
  TourListItem,
  TourPopularity,
  TourSortOption,
  TourType
} from '../../models/tour.models';
import { TourApiService } from '../../services/tour-api.service';

type TourListFilter = 'all' | 'favorites' | 'not-favorites';

interface TourSortSelectOption {
  value: TourSortOption;
  label: string;
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
    AppShellComponent,
    ConfirmDialogComponent,
    FavoriteToggleComponent,
    PaginationComponent
  ],
  templateUrl: './tours-page.html',
  styleUrl: './tours-page.css'
})
export class ToursPage {
  protected readonly rows = 5;
  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected readonly filterControl = new FormControl<TourListFilter>('all', { nonNullable: true });
  protected readonly sortControl = new FormControl<TourSortOption>('POPULARITY_DESC', {
    nonNullable: true
  });
  protected readonly sortOptions: TourSortSelectOption[] = [
    { value: 'POPULARITY_DESC', label: 'Popularity ↓' },
    { value: 'POPULARITY_ASC', label: 'Popularity ↑' },
    { value: 'CHILD_FRIENDLINESS_DESC', label: 'Child-friendly ↓' },
    { value: 'CHILD_FRIENDLINESS_ASC', label: 'Child-friendly ↑' },
    { value: 'NAME_ASC', label: 'Name A-Z' },
    { value: 'NAME_DESC', label: 'Name Z-A' },
    { value: 'NEWEST_FIRST', label: 'Newest ↓' },
    { value: 'OLDEST_FIRST', label: 'Newest ↑' }
  ];
  protected readonly searchTerm = signal('');
  protected readonly activeFilter = signal<TourListFilter>('all');
  protected readonly activeSort = signal<TourSortOption>('POPULARITY_DESC');
  protected readonly first = signal(0);
  protected isLoggingOut = false;
  protected readonly isLoadingTours = signal(true);
  protected readonly toursLoadError = signal('');
  protected readonly showDeleteDialog = signal(false);
  protected readonly isDeletingTour = signal(false);
  protected readonly favoriteTourId = signal<string | null>(null);
  protected readonly selectedTourForDelete = signal<TourListItem | null>(null);

  protected readonly tours = signal<TourListItem[]>([]);

  protected readonly filteredTours = computed(() => {
    const normalizedSearch = this.searchTerm().trim().toLowerCase();

    if (normalizedSearch !== '') {
      return this.tours();
    }

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
  private readonly searchApi = inject(SearchApiService);
  private readonly tourApi = inject(TourApiService);

  constructor() {
    this.filterControl.valueChanges.subscribe((value) => {
      this.activeFilter.set(value);
      this.first.set(0);
      this.loadTours();
    });

    this.sortControl.valueChanges.subscribe((value) => {
      this.activeSort.set(value);
      this.first.set(0);
      this.loadTours();
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

  protected submitSearch(): void {
    this.searchTerm.set(this.searchControl.value);
    this.first.set(0);
    this.loadTours();
  }

  protected openTourDetails(tourId: string): void {
    void this.router.navigate(['/tours', tourId]);
  }

  protected openEditTour(tourId: string): void {
    void this.router.navigate(['/tours', tourId, 'edit']);
  }

  protected openDeleteDialog(tour: TourListItem): void {
    this.selectedTourForDelete.set(tour);
    this.showDeleteDialog.set(true);
  }

  protected toggleFavorite(tour: TourListItem): void {
    if (this.favoriteTourId()) {
      return;
    }

    this.favoriteTourId.set(tour.id);
    this.toursLoadError.set('');

    const favoriteRequest = tour.isFavorite
      ? this.tourApi.removeFavorite(tour.id)
      : this.tourApi.addFavorite(tour.id);

    favoriteRequest
      .pipe(finalize(() => this.favoriteTourId.set(null)))
      .subscribe({
        next: () => {
          this.loadTours();
        },
        error: () => {
          this.toursLoadError.set('Favorite status could not be updated.');
        }
      });
  }

  protected closeDeleteDialog(): void {
    this.showDeleteDialog.set(false);
    this.selectedTourForDelete.set(null);
  }

  protected confirmDeleteTour(): void {
    const selectedTour = this.selectedTourForDelete();

    if (!selectedTour || this.isDeletingTour()) {
      return;
    }

    this.isDeletingTour.set(true);

    this.tourApi
      .deleteById(selectedTour.id)
      .pipe(finalize(() => this.isDeletingTour.set(false)))
      .subscribe({
        next: () => {
          this.tours.update((tours) => tours.filter((tour) => tour.id !== selectedTour.id));

          const remainingTours = this.filteredTours().length;
          if (this.first() >= remainingTours && this.first() > 0) {
            this.first.set(Math.max(0, this.first() - this.rows));
          }

          this.closeDeleteDialog();
        },
        error: () => {
          this.closeDeleteDialog();
          this.toursLoadError.set('Tour could not be deleted.');
        }
      });
  }

  protected trackTour(_: number, tour: TourListItem): string {
    return tour.id;
  }

  protected currentFilterLabel(): string {
    switch (this.activeFilter()) {
      case 'favorites':
        return 'favorite';
      case 'not-favorites':
        return 'not favorite';
      default:
        return '';
    }
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

  protected isFavoriteBusy(tourId: string): boolean {
    return this.favoriteTourId() === tourId;
  }

  protected popularityBadgeLabel(value: TourPopularity | undefined): string {
    switch (value) {
      case 'VERY_POPULAR':
        return 'Very popular';
      case 'POPULAR':
        return 'Popular';
      default:
        return '';
    }
  }

  protected childFriendlinessBadgeLabel(value: TourChildFriendliness | undefined): string {
    switch (value) {
      case 'VERY_CHILD_FRIENDLY':
        return 'Very child-friendly';
      case 'CHILD_FRIENDLY':
        return 'Child-friendly';
      default:
        return '';
    }
  }

  private loadTours(): void {
    this.isLoadingTours.set(true);
    this.toursLoadError.set('');

    const normalizedSearch = this.searchTerm().trim();

    if (normalizedSearch) {
      const sortedToursRequest =
        this.activeFilter() === 'favorites'
          ? this.tourApi.listFavorites(this.activeSort())
          : this.activeFilter() === 'not-favorites'
            ? this.tourApi.listNotFavorites(this.activeSort())
            : this.tourApi.list(this.activeSort());

      forkJoin({
        results: this.searchApi.search(normalizedSearch, 'tours'),
        tours: sortedToursRequest,
        favorites: this.tourApi.listFavorites()
      })
        .pipe(finalize(() => this.isLoadingTours.set(false)))
        .subscribe({
          next: ({ results, tours, favorites }) => {
            const resultIds = new Set(
              results.filter((result) => result.type === 'TOUR').map((result) => result.id)
            );
            const favoriteIds = new Set(favorites.map((tour) => tour.id));
            const searchedTours = tours
              .filter((tour) => resultIds.has(tour.id))
              .map((tour) => ({
                ...this.tourApi.toListItem(tour),
                isFavorite: favoriteIds.has(tour.id)
              }));

            this.tours.set(searchedTours);
          },
          error: () => {
            this.toursLoadError.set('Tours could not be searched.');
            this.tours.set([]);
          }
        });
      return;
    }

    if (this.activeFilter() === 'all') {
      forkJoin({
        tours: this.tourApi.list(this.activeSort()),
        favorites: this.tourApi.listFavorites(this.activeSort())
      })
        .pipe(finalize(() => this.isLoadingTours.set(false)))
        .subscribe({
          next: ({ tours, favorites }) => {
            const favoriteIds = new Set(favorites.map((tour) => tour.id));
            this.tours.set(
              tours.map((tour) => ({
                ...this.tourApi.toListItem(tour),
                isFavorite: favoriteIds.has(tour.id)
              }))
            );
          },
          error: () => {
            this.toursLoadError.set('Tours could not be loaded.');
            this.tours.set([]);
          }
        });
      return;
    }

    const toursRequest =
      this.activeFilter() === 'favorites'
        ? this.tourApi.listFavorites(this.activeSort())
        : this.tourApi.listNotFavorites(this.activeSort());

    toursRequest
      .pipe(finalize(() => this.isLoadingTours.set(false)))
      .subscribe({
        next: (tours) => {
          this.tours.set(
            tours.map((tour) => ({
              ...this.tourApi.toListItem(tour),
              isFavorite: this.activeFilter() === 'favorites'
            }))
          );
        },
        error: () => {
          this.toursLoadError.set('Tours could not be loaded.');
          this.tours.set([]);
        }
      });
  }

  private applyFavoriteFilter(tours: TourListItem[]): TourListItem[] {
    switch (this.activeFilter()) {
      case 'favorites':
        return tours.filter((tour) => tour.isFavorite);
      case 'not-favorites':
        return tours.filter((tour) => !tour.isFavorite);
      default:
        return tours;
    }
  }
}
