export const API_BASE_URL = 'http://localhost:8080/api';

// Mock user for authentication testing - set on false to disable mock authentication
export const USE_MOCK_AUTH = false;

export const MOCK_AUTH_USER = {
  username: 'testuser',
  password: 'test1234',
  email: 'test@tourplanner.local'
} as const;
