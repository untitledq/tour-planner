import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { CreateTourLogRequest, TourLogResponse } from '../models/tour-log.models';

@Injectable({ providedIn: 'root' })
export class TourLogApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/tour-logs`;

  create(payload: CreateTourLogRequest): Observable<TourLogResponse> {
    return this.http.post<TourLogResponse>(this.apiUrl, payload);
  }
}
