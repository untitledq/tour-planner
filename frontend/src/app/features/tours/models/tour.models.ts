export type TourType = 'BIKE' | 'HIKE' | 'RUNNING' | 'VACATION';

export interface CreateTourRequest {
  name: string;
  description: string;
  imagePath: string;
  tourType: TourType;
  startName: string;
  endName: string;
}

export interface TourResponse {
  id: string;
  name: string;
  description: string;
  imagePath: string;
  tourType: TourType;
  startName: string;
  endName: string;
}

export interface TourListResponse {
  id: string;
  name: string;
}

export interface CreateTourSaveResult {
  status: 'success' | 'error';
  title: string;
  message: string;
  tourId?: string;
}
