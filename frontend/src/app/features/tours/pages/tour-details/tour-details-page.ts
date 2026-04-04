import { Component, ElementRef, OnDestroy, ViewChild, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { finalize } from 'rxjs';
import type * as Leaflet from 'leaflet';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { PageBreadcrumbComponent } from '../../../../shared/components/page-breadcrumb/page-breadcrumb';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { TourResponse, TourRouteResponse, TourType } from '../../models/tour.models';
import { TourApiService } from '../../services/tour-api.service';

@Component({
  selector: 'app-tour-details-page',
  standalone: true,
  imports: [ButtonModule, AppShellComponent, ConfirmDialogComponent, PageBreadcrumbComponent],
  templateUrl: './tour-details-page.html',
  styleUrl: './tour-details-page.css'
})
export class TourDetailsPage implements OnDestroy {
  @ViewChild('mapContainer')
  private set mapContainer(element: ElementRef<HTMLDivElement> | undefined) {
    this.mapContainerRef = element;

    if (element) {
      void this.ensureMapIsReady();
    }
  }

  private leaflet?: typeof Leaflet;
  private mapContainerRef?: ElementRef<HTMLDivElement>;
  private map?: Leaflet.Map;
  private routeLayer?: Leaflet.Polyline;
  private startMarker?: Leaflet.CircleMarker;
  private endMarker?: Leaflet.CircleMarker;

  protected readonly isLoadingTour = signal(true);
  protected readonly isLoadingRoute = signal(true);
  protected readonly tourLoadError = signal('');
  protected readonly routeLoadError = signal('');
  protected readonly tour = signal<TourResponse | null>(null);
  protected readonly routeDetails = signal<TourRouteResponse | null>(null);
  protected readonly showDeleteDialog = signal(false);
  protected readonly isDeletingTour = signal(false);
  protected readonly canShowTourActions = computed(() => {
    return !this.isLoadingTour() && !this.tourLoadError() && this.tour() !== null;
  });
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

    this.tourApi
      .getRouteByTourId(tourId)
      .pipe(finalize(() => this.isLoadingRoute.set(false)))
      .subscribe({
        next: (route) => {
          this.routeDetails.set(route);
          void this.ensureMapIsReady();
        },
        error: () => {
          this.routeLoadError.set('Route details could not be loaded.');
          this.routeDetails.set(null);
        }
      });
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private async ensureMapIsReady(): Promise<void> {
    await this.loadLeaflet();
    this.initializeMap();
    this.renderRouteOnMap();
  }

  private async loadLeaflet(): Promise<void> {
    if (this.leaflet) {
      return;
    }

    const leafletModule = await import('leaflet');
    this.leaflet = leafletModule;
  }

  private initializeMap(): void {
    const container = this.mapContainerRef?.nativeElement;
    const leaflet = this.leaflet;

    if (!container || this.map || !leaflet) {
      return;
    }

    this.map = leaflet.map(container, {
      zoomControl: true
    });

    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.map.setView([48.2082, 16.3738], 13);
  }

  private renderRouteOnMap(): void {
    const route = this.routeDetails();
    const leaflet = this.leaflet;

    if (!this.map || !leaflet || !route?.coordinates?.length) {
      return;
    }

    const latLngs = route.coordinates.map(([lat, lng]) => leaflet.latLng(lat, lng));

    if (this.routeLayer) {
      this.map.removeLayer(this.routeLayer);
    }

    if (this.startMarker) {
      this.map.removeLayer(this.startMarker);
    }

    if (this.endMarker) {
      this.map.removeLayer(this.endMarker);
    }

    this.routeLayer = leaflet.polyline(latLngs, {
      color: '#2f7d6b',
      weight: 6,
      smoothFactor: 1.5,
    lineCap: 'round',
    lineJoin: 'round'
    }).addTo(this.map);

    this.startMarker = leaflet.circleMarker(latLngs[0], {
      radius: 9,
      color: '#2563eb',
      weight: 3,
      fillColor: '#60a5fa',
      fillOpacity: 1
    })
      .addTo(this.map)
      .bindPopup(`From: ${this.tour()?.startName ?? 'Start'}`);
    this.endMarker = leaflet.circleMarker(latLngs[latLngs.length - 1], {
      radius: 9,
      color: '#ea580c',
      weight: 3,
      fillColor: '#fb923c',
      fillOpacity: 1
    })
      .addTo(this.map)
      .bindPopup(`To: ${this.tour()?.endName ?? 'End'}`);

    this.map.fitBounds(this.routeLayer.getBounds(), {
      padding: [24, 24]
    });

    this.map.invalidateSize();
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

  protected openEditTour(): void {
    const selectedTour = this.tour();

    if (!selectedTour) {
      return;
    }

    void this.router.navigate(['/tours', selectedTour.id, 'edit']);
  }

  protected openDeleteDialog(): void {
    this.showDeleteDialog.set(true);
  }

  protected closeDeleteDialog(): void {
    this.showDeleteDialog.set(false);
  }

  protected confirmDeleteTour(): void {
    const selectedTour = this.tour();

    if (!selectedTour || this.isDeletingTour()) {
      return;
    }

    this.isDeletingTour.set(true);

    this.tourApi
      .deleteById(selectedTour.id)
      .pipe(finalize(() => this.isDeletingTour.set(false)))
      .subscribe({
        next: () => {
          this.showDeleteDialog.set(false);
          void this.router.navigateByUrl('/tours');
        },
        error: () => {
          this.showDeleteDialog.set(false);
        }
      });
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
  

  protected formatDistance(distanceInMeters: number): string {
    return `${(distanceInMeters / 1000).toFixed(1)} km`;
  }

  protected formatDuration(durationInSeconds: number): string {
    const totalMinutes = Math.round(durationInSeconds / 60);
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;

    if (hours === 0) {
      return `${minutes} min`;
    }

    if (minutes === 0) {
      return `${hours} h`;
    }

    return `${hours} h ${minutes} min`;
  }
}
