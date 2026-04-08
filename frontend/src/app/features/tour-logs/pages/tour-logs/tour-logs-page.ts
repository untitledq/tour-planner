import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { finalize } from 'rxjs';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { TourLogListResponse } from '../../models/tour-log.models';
import { TourLogApiService } from '../../services/tour-log-api.service';

@Component({
  selector: 'app-tour-logs-page',
  standalone: true,
  imports: [ButtonModule, AppShellComponent, ConfirmDialogComponent, PaginationComponent],
  templateUrl: './tour-logs-page.html',
  styleUrl: './tour-logs-page.css'
})
export class TourLogsPage {
  protected readonly rows = 5;
  protected isLoggingOut = false;
  protected readonly first = signal(0);
  protected readonly isLoadingLogs = signal(true);
  protected readonly logLoadError = signal('');
  protected readonly tourLogs = signal<TourLogListResponse[]>([]);
  protected readonly showDeleteDialog = signal(false);
  protected readonly isDeletingLog = signal(false);
  protected readonly selectedLogForDelete = signal<TourLogListResponse | null>(null);
  protected readonly hasLogs = computed(() => this.tourLogs().length > 0);
  protected readonly visibleLogs = computed(() => {
    const first = this.first();
    return this.tourLogs().slice(first, first + this.rows);
  });
  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);
  private readonly tourLogApi = inject(TourLogApiService);

  constructor() {
    this.tourLogApi
      .list()
      .pipe(finalize(() => this.isLoadingLogs.set(false)))
      .subscribe({
        next: (logs) => {
          this.first.set(0);
          this.tourLogs.set(logs);
        },
        error: () => {
          this.logLoadError.set('Tour logs could not be loaded.');
          this.tourLogs.set([]);
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

  protected onPageChange(event: { first?: number }): void {
    this.first.set(event.first ?? 0);
  }

  protected openSelectTour(): void {
    void this.router.navigateByUrl('/tour-logs/select-tour');
  }

  protected openLogDetails(logId: string): void {
    void this.router.navigate(['/tour-logs', logId]);
  }

  protected openEditLog(logId: string): void {
    void this.router.navigate(['/tour-logs', logId, 'edit']);
  }

  protected openDeleteDialog(log: TourLogListResponse): void {
    this.selectedLogForDelete.set(log);
    this.showDeleteDialog.set(true);
  }

  protected closeDeleteDialog(): void {
    this.showDeleteDialog.set(false);
    this.selectedLogForDelete.set(null);
  }

  protected confirmDeleteLog(): void {
    const selectedLog = this.selectedLogForDelete();

    if (!selectedLog || this.isDeletingLog()) {
      return;
    }

    this.isDeletingLog.set(true);

    this.tourLogApi
      .deleteById(selectedLog.id)
      .pipe(finalize(() => this.isDeletingLog.set(false)))
      .subscribe({
        next: () => {
          this.tourLogs.update((logs) => logs.filter((log) => log.id !== selectedLog.id));
          this.closeDeleteDialog();
        },
        error: () => {
          this.closeDeleteDialog();
          this.logLoadError.set('Tour log could not be deleted.');
        }
      });
  }
}
