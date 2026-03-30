import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { PendingChangesAware } from '../../../../core/guards/pending-changes.guard';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { SidebarComponent } from '../../../../shared/components/sidebar/sidebar';
import { AuthApiService } from '../../../auth/services/auth-api.service';

@Component({
  selector: 'app-create-tour-details-page',
  standalone: true,
  imports: [SidebarComponent, ConfirmDialogComponent],
  templateUrl: './create-tour-details-page.html',
  styleUrl: './create-tour-details-page.css'
})
export class CreateTourDetailsPage implements PendingChangesAware {
  protected readonly maxDescriptionLength = 500;
  protected isLoggingOut = false;
  protected showLeaveDialog = false;
  protected description = '';
  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
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

  protected cancelCreation(): void {
    void this.router.navigateByUrl('/dashboard');
  }

  protected updateDescription(event: Event): void {
    this.description = (event.target as HTMLTextAreaElement).value;
  }

  protected get descriptionLength(): number {
    return this.description.length;
  }

  protected get isDescriptionTooLong(): boolean {
    return this.descriptionLength > this.maxDescriptionLength;
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
