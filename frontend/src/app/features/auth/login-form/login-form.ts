import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { finalize } from 'rxjs';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login-form',
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
  templateUrl: './login-form.html',
  styleUrl: './login-form.css'
})
export class LoginForm {
  protected readonly loginForm;
  protected loginError = '';
  protected isSubmitting = false;
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

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
      return;
    }
    this.isSubmitting = true;

    this.authService
      .login(this.loginForm.getRawValue())
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/dashboard');
        },
        error: () => {
          this.loginError = 'Login fehlgeschlagen. Bitte pruefe Benutzername und Passwort.';
        }
      });
  }

  protected showRequiredError(controlName: 'username' | 'password'): boolean {
    const control = this.loginForm.controls[controlName];

    return control.touched && control.hasError('required');
  }
}
