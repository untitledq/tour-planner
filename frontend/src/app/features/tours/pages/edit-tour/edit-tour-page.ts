import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';

import { PendingChangesAware } from '../../../../core/guards/pending-changes.guard';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { AuthApiService } from '../../../auth/services/auth-api.service';

@Component({
  selector: 'app-edit-tour-page',
  standalone: true,
  imports: [AppShellComponent, ConfirmDialogComponent],
  templateUrl: './edit-tour-page.html',
  styleUrl: './edit-tour-page.css'
})
export class EditTourPage implements PendingChangesAware {
  protected isLoggingOut = false;
  protected showLeaveDialog = false;

  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private pendingLeaveResolver: ((value: boolean) => void) | null = null;

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
    const tourId = this.route.snapshot.paramMap.get('id');

    if (!tourId) {
      void this.router.navigateByUrl('/tours');
      return;
    }

    void this.router.navigate(['/tours', tourId]);
  }

  protected confirmLeave(): void {
    this.showLeaveDialog = false;
    this.pendingLeaveResolver?.(true);
    this.pendingLeaveResolver = null;
  }

  protected stayOnPage(): void {
    this.showLeaveDialog = false;
    this.pendingLeaveResolver?.(false);
    this.pendingLeaveResolver = null;
  }

  canDeactivate(): boolean | Promise<boolean> {
    if (this.pendingLeaveResolver) {
      return false;
    }

    this.showLeaveDialog = true;

    return new Promise<boolean>((resolve) => {
      this.pendingLeaveResolver = resolve;
    });
  }
}
