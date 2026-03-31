export interface CreateTourRequest {
  name: string;
  description: string;
  imagePath: string;
  transportType: string;
  startName: string;
  endName: string;
}

export interface TourResponse {
  id: string;
  name: string;
  description: string;
  imagePath: string;
  transportType: string;
  startName: string;
  endName: string;
}

export interface CreateTourSaveResult {
  status: 'success' | 'error';
  title: string;
  message: string;
  tourId?: string;
}
