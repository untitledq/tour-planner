import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import {
  CreateTourRequest,
  TourListResponse,
  TourResponse,
  TourRouteResponse
} from '../models/tour.models';

@Injectable({ providedIn: 'root' })
export class TourApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/tours`;

  list(): Observable<TourListResponse[]> {
    return this.http.get<TourListResponse[]>(this.apiUrl);
  }

  getById(id: string): Observable<TourResponse> {
    return this.http.get<TourResponse>(`${this.apiUrl}/${id}`);
  }

  getRouteByTourId(id: string): Observable<TourRouteResponse> {
    return this.http.get<TourRouteResponse>(`${this.apiUrl}/${id}/route`);
  }

  uploadImage(id: string, file: File): Observable<TourResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<TourResponse>(`${this.apiUrl}/${id}/image`, formData);
  }

  downloadImage(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/image`, {
      responseType: 'blob'
    });
  }

  update(id: string, payload: CreateTourRequest): Observable<TourResponse> {
    return this.http.put<TourResponse>(`${this.apiUrl}/${id}`, payload);
  }

  deleteById(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  create(payload: CreateTourRequest): Observable<TourResponse> {
    return this.http.post<TourResponse>(this.apiUrl, payload);
  }
}
