import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { API_BASE_URL, USE_MOCK_AUTH } from '../../../core/config/api.config';
import { CreateTourRequest, TourResponse } from '../models/tour.models';

@Injectable({ providedIn: 'root' })
export class TourApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/tours`;

  create(payload: CreateTourRequest): Observable<TourResponse> {
    if (USE_MOCK_AUTH) {
      return of({
        id: 'mock-tour-id',
        name: payload.name,
        description: payload.description,
        imagePath: payload.imagePath,
        transportType: payload.transportType,
        startName: payload.startName,
        endName: payload.endName
      }).pipe(delay(300));
    }

    return this.http.post<TourResponse>(this.apiUrl, payload);
  }
}
