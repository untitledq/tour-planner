import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';

export type SearchScope = 'all' | 'tours' | 'tour-logs';
export type SearchResultType = 'TOUR' | 'TOUR_LOG';

export interface SearchResultResponse {
  type: SearchResultType;
  id: string;
  tourId: string;
  title: string;
  snippet: string;
}

@Injectable({ providedIn: 'root' })
export class SearchApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/search`;

  search(query: string, scope: SearchScope): Observable<SearchResultResponse[]> {
    const params = new HttpParams().set('q', query).set('scope', scope);

    return this.http.get<SearchResultResponse[]>(this.apiUrl, { params });
  }
}
