import { Component } from '@angular/core';
import { LoginForm } from './login-form/login-form';

@Component({
  selector: 'app-root',
  imports: [LoginForm],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
