import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { finalize } from 'rxjs';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { SidebarComponent } from '../../../../shared/components/sidebar/sidebar';
import { AuthApiService } from '../../../auth/services/auth-api.service';

@Component({
  selector: 'app-tour-logs-page',
  standalone: true,
  imports: [ButtonModule, SidebarComponent],
  templateUrl: './tour-logs-page.html',
  styleUrl: './tour-logs-page.css'
})
export class TourLogsPage {
  protected isLoggingOut = false;
  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);

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
}
