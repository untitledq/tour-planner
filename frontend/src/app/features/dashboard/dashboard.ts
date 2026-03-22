import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { finalize } from 'rxjs';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ButtonModule],
  template: `
    <section class="dashboard-shell">
      <div class="dashboard-header">
        <div>
          <h1>Dashboard</h1>
          <p>Das ist mein Dashboard.</p>
        </div>

        <p-button
          label="Logout"
          severity="secondary"
          [loading]="isLoggingOut"
          (onClick)="logout()"
        />
      </div>
    </section>
  `,
  styleUrl: './dashboard.css'
})
export class Dashboard {
  protected isLoggingOut = false;
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected logout(): void {
    this.isLoggingOut = true;

    this.authService
      .logout()
      .pipe(finalize(() => (this.isLoggingOut = false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/login');
        },
        error: () => {
          this.authService.clearToken();
          void this.router.navigateByUrl('/login');
        }
      });
  }
}
