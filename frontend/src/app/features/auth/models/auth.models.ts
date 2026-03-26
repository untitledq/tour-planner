export interface LoginResponse {
  token: string;
  expires_at: string;
  session_id: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}
