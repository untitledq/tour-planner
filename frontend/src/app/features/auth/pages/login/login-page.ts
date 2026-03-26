import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { finalize } from 'rxjs';

import { AuthApiService } from '../../services/auth-api.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    DividerModule,
    FloatLabelModule,
    InputTextModule,
    PasswordModule
  ],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css'
})
export class LoginPage {
  protected readonly loginForm;
  protected loginError = '';
  protected isSubmitting = false;
  private readonly authApi = inject(AuthApiService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  constructor(private readonly formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.nonNullable.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  protected onSubmit(): void {
    this.loginForm.markAllAsTouched();
    this.loginError = '';

    if (this.loginForm.invalid) {
      this.loginError = 'Bitte fuelle alle Felder korrekt aus.';
      this.changeDetectorRef.detectChanges();
      return;
    }
    this.isSubmitting = true;

    this.authApi
      .login(this.loginForm.getRawValue())
      .pipe(finalize(() => {
        this.isSubmitting = false;
        this.changeDetectorRef.detectChanges();
      }))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/dashboard');
        },
        error: () => {
          this.loginError = 'Login fehlgeschlagen. Bitte pruefe Benutzername und Passwort.';
          this.changeDetectorRef.detectChanges();
        }
      });
  }

  protected showRequiredError(controlName: 'username' | 'password'): boolean {
    const control = this.loginForm.controls[controlName];

    return control.touched && control.hasError('required');
  }
}
