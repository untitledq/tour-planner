export type TourType = 'BIKE' | 'HIKE' | 'RUNNING' | 'VACATION';
export type TourPopularity = 'UNKNOWN' | 'LOW' | 'POPULAR' | 'VERY_POPULAR';
export type TourChildFriendliness =
  | 'UNKNOWN'
  | 'NOT_CHILD_FRIENDLY'
  | 'MODERATE'
  | 'CHILD_FRIENDLY'
  | 'VERY_CHILD_FRIENDLY';
export type TourSortOption =
  | 'POPULARITY_DESC'
  | 'POPULARITY_ASC'
  | 'CHILD_FRIENDLINESS_DESC'
  | 'CHILD_FRIENDLINESS_ASC'
  | 'NAME_ASC'
  | 'NAME_DESC'
  | 'NEWEST_FIRST'
  | 'OLDEST_FIRST';

export interface TourComputedAttributes {
  logCount: number;
  averageDifficulty?: number | null;
  averageTotalDistance?: number | null;
  averageTotalTime?: number | null;
  popularity: TourPopularity;
  childFriendliness: TourChildFriendliness;
}

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
  computedAttributes?: TourComputedAttributes;
}

export interface TourWithFavoriteResponse extends TourResponse {
  favorite: boolean;
}

export interface TourRouteResponse {
  distance: number;
  duration: number;
  coordinates: number[][];
}

export interface TourListResponse {
  id: string;
  name: string;
  computedAttributes?: TourComputedAttributes;
}

export interface TourFilteredListResponse {
  id: string;
  name: string;
  tourType: TourType;
  computedAttributes?: TourComputedAttributes;
}

export interface TourListItem {
  id: string;
  name: string;
  tourType?: TourType;
  isFavorite: boolean;
  computedAttributes?: TourComputedAttributes;
}

export interface CreateTourSaveResult {
  status: 'success' | 'error';
  title: string;
  message: string;
  tourId?: string;
}
