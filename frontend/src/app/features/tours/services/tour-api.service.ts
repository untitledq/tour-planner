import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { API_BASE_URL, USE_MOCK_AUTH } from '../../../core/config/api.config';
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

  update(id: string, payload: CreateTourRequest): Observable<TourResponse> {
    if (USE_MOCK_AUTH) {
      return of({
        id,
        name: payload.name,
        description: payload.description,
        imagePath: payload.imagePath,
        tourType: payload.tourType,
        startName: payload.startName,
        endName: payload.endName
      }).pipe(delay(300));
    }

    return this.http.put<TourResponse>(`${this.apiUrl}/${id}`, payload);
  }

  deleteById(id: string): Observable<void> {
    if (USE_MOCK_AUTH) {
      return of(void 0).pipe(delay(300));
    }

    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  create(payload: CreateTourRequest): Observable<TourResponse> {
    if (USE_MOCK_AUTH) {
      return of({
        id: 'mock-tour-id',
        name: payload.name,
        description: payload.description,
        imagePath: payload.imagePath,
        tourType: payload.tourType,
        startName: payload.startName,
        endName: payload.endName
      }).pipe(delay(300));
    }

    return this.http.post<TourResponse>(this.apiUrl, payload);
  }
}
