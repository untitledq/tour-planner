import { afterNextRender, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { PendingChangesAware } from '../../../../core/guards/pending-changes.guard';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { SidebarComponent } from '../../../../shared/components/sidebar/sidebar';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { CreateTourDraftService } from '../../services/create-tour-draft.service';

@Component({
  selector: 'app-create-tour-summary-page',
  standalone: true,
  imports: [SidebarComponent, ConfirmDialogComponent],
  templateUrl: './create-tour-summary-page.html',
  styleUrl: './create-tour-summary-page.css'
})
export class CreateTourSummaryPage implements PendingChangesAware {
  protected readonly maxDescriptionLength = 500;
  protected readonly draftService = inject(CreateTourDraftService);
  protected isLoggingOut = false;
  protected showLeaveDialog = false;
  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);
  private pendingLeaveResolver: ((value: boolean) => void) | null = null;
  private allowRouteChange = false;

  constructor() {
    afterNextRender(() => {
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
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

  protected confirmLeave(): void {
    this.draftService.reset();
    this.showLeaveDialog = false;
    this.pendingLeaveResolver?.(true);
    this.pendingLeaveResolver = null;
  }

  protected stayOnPage(): void {
    this.showLeaveDialog = false;
    this.pendingLeaveResolver?.(false);
    this.pendingLeaveResolver = null;
  }

  protected editTour(): void {
    this.allowRouteChange = true;
    void this.router.navigateByUrl('/tours/create/details');
  }

  protected get draft() {
    return this.draftService.draft();
  }

  protected get descriptionLength(): number {
    return this.draft.description.length;
  }

  canDeactivate(): boolean | Promise<boolean> {
    if (this.allowRouteChange) {
      this.allowRouteChange = false;
      return true;
    }

    if (this.pendingLeaveResolver) {
      return false;
    }

    this.showLeaveDialog = true;

    return new Promise<boolean>((resolve) => {
      this.pendingLeaveResolver = resolve;
    });
  }
}
