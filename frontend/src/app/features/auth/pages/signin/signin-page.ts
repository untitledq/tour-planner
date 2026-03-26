import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CheckboxModule } from 'primeng/checkbox';
import { DividerModule } from 'primeng/divider';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';

@Component({
  selector: 'app-signin-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    CheckboxModule,
    DividerModule,
    FloatLabelModule,
    InputTextModule,
    PasswordModule
  ],
  templateUrl: './signin-page.html',
  styleUrl: './signin-page.css'
})
export class SigninPage {
  protected readonly signinForm;

  constructor(private readonly formBuilder: FormBuilder) {
    this.signinForm = this.formBuilder.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      rememberMe: [false]
    });
  }

  protected onSubmit(): void {
    this.signinForm.markAllAsTouched();

    if (this.signinForm.invalid) {
      return;
    }

    console.log('Signin form payload', this.signinForm.getRawValue());
  }

  protected showRequiredError(controlName: 'email' | 'password' | 'confirmPassword'): boolean {
    const control = this.signinForm.controls[controlName];

    return control.touched && control.hasError('required');
  }
}
