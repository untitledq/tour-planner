import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import {
  CreateTourLogRequest,
  TourLogListResponse,
  TourLogResponse
} from '../models/tour-log.models';

@Injectable({ providedIn: 'root' })
export class TourLogApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/tour-logs`;

  list(): Observable<TourLogListResponse[]> {
    return this.http.get<TourLogListResponse[]>(this.apiUrl);
  }

  getById(id: string): Observable<TourLogResponse> {
    return this.http.get<TourLogResponse>(`${this.apiUrl}/${id}`);
  }

  create(payload: CreateTourLogRequest): Observable<TourLogResponse> {
    return this.http.post<TourLogResponse>(this.apiUrl, payload);
  }

  deleteById(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
