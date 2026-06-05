import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, from, map, switchMap } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { AppShellComponent } from '../../../../shared/components/app-shell/app-shell';
import { AuthApiService } from '../../../auth/services/auth-api.service';
import { TourApiService } from '../../../tours/services/tour-api.service';

@Component({
  selector: 'app-import-page',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, InputTextModule, AppShellComponent],
  templateUrl: './import-page.html',
  styleUrl: './import-page.css',
})
export class ImportPage {
  protected readonly tourNameControl = new FormControl('', { nonNullable: true });
  protected readonly importError = signal('');
  protected readonly importSuccess = signal('');
  protected readonly selectedFile = signal<File | null>(null);
  protected readonly isDraggingFile = signal(false);
  protected readonly isImporting = signal(false);
  protected readonly selectedFileDetails = computed(() => {
    const file = this.selectedFile();

    if (!file) {
      return null;
    }

    return `${file.name} - ${this.formatFileSize(file.size)}`;
  });
  protected isLoggingOut = false;

  private readonly authApi = inject(AuthApiService);
  private readonly authSession = inject(AuthSessionService);
  private readonly router = inject(Router);
  private readonly tourApi = inject(TourApiService);

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
        },
      });
  }

  protected onFileInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.setSelectedFile(input.files?.[0] ?? null);
    input.value = '';
  }

  protected onFileDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDraggingFile.set(true);
  }

  protected onFileDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDraggingFile.set(false);
  }

  protected onFileDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDraggingFile.set(false);
    this.setSelectedFile(event.dataTransfer?.files?.[0] ?? null);
  }

  protected clearSelectedFile(): void {
    this.selectedFile.set(null);
    this.tourNameControl.setValue('');
    this.importError.set('');
    this.importSuccess.set('');
  }

  protected importSelectedFile(): void {
    const file = this.selectedFile();

    if (!file || this.isImporting()) {
      return;
    }

    this.isImporting.set(true);
    this.importError.set('');
    this.importSuccess.set('');

    from(file.text())
      .pipe(
        map((xml) => this.withImportedTourName(xml)),
        switchMap((xml) => this.tourApi.importTourXml(xml)),
        finalize(() => this.isImporting.set(false)),
      )
      .subscribe({
        next: (importedTour) => {
          this.importSuccess.set(`${importedTour.name} was imported.`);
          void this.router.navigate(['/tours', importedTour.id]);
        },
        error: () => {
          this.importError.set('Tour could not be imported.');
        },
      });
  }

  private setSelectedFile(file: File | null): void {
    this.importError.set('');
    this.importSuccess.set('');

    if (!file) {
      this.selectedFile.set(null);
      this.tourNameControl.setValue('');
      return;
    }

    if (!this.isXmlFile(file)) {
      this.selectedFile.set(null);
      this.tourNameControl.setValue('');
      this.importError.set('Only XML files can be imported.');
      return;
    }

    this.selectedFile.set(file);
    void this.loadTourNameFromFile(file);
  }

  private async loadTourNameFromFile(file: File): Promise<void> {
    try {
      const xml = await file.text();
      const parser = new DOMParser();
      const document = parser.parseFromString(xml, 'application/xml');
      const parseError = document.querySelector('parsererror');

      if (parseError) {
        return;
      }

      const nameElement = document.querySelector('tour > name') ?? document.querySelector('name');
      this.tourNameControl.setValue(nameElement?.textContent?.trim() ?? '');
    } catch {
      this.tourNameControl.setValue('');
    }
  }

  private withImportedTourName(xml: string): string {
    const newTourName = this.tourNameControl.value.trim();

    if (!newTourName) {
      return xml;
    }

    const parser = new DOMParser();
    const document = parser.parseFromString(xml, 'application/xml');
    const parseError = document.querySelector('parsererror');

    if (parseError) {
      throw new Error('Invalid XML file');
    }

    const nameElement = document.querySelector('tour > name') ?? document.querySelector('name');

    if (!nameElement) {
      throw new Error('XML file does not contain a tour name');
    }

    nameElement.textContent = newTourName;

    return new XMLSerializer().serializeToString(document);
  }

  private isXmlFile(file: File): boolean {
    return (
      file.name.toLowerCase().endsWith('.xml') ||
      file.type === 'application/xml' ||
      file.type === 'text/xml'
    );
  }

  private formatFileSize(sizeInBytes: number): string {
    if (sizeInBytes < 1024) {
      return `${sizeInBytes} B`;
    }

    if (sizeInBytes < 1024 * 1024) {
      return `${(sizeInBytes / 1024).toFixed(1)} KB`;
    }

    return `${(sizeInBytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
