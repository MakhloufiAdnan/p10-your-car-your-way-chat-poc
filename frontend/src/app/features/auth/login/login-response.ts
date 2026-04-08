export interface LoginResponse {
  userId: string;
  username: string;
  role: 'CLIENT' | 'AGENT';
  message: string;
}
