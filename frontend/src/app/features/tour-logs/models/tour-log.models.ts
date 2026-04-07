export interface CreateTourLogRequest {
  tourId: string;
  dateTime: string;
  rating: number;
  difficulty: number;
  totalTime: number;
  comment: string;
}

export interface TourLogResponse {
  id: string;
  tourId: string;
  dateTime: string;
  rating: number;
  difficulty: number;
  totalTime: number;
  comment: string;
}
