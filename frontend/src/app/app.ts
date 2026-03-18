import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HelloService, HelloResponse } from './services/hello.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  protected readonly title = signal('frontend');

  message = signal('Loading...');

  constructor(private helloService: HelloService) {}

  ngOnInit(): void {
    this.helloService.getHello().subscribe({
      next: (res: HelloResponse) => this.message.set(res.message),
      error: () => this.message.set('Error loading message')
    });
  }
}
