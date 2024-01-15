import { jwtDecode } from "jwt-decode";
import { UserToken } from '../models/UserToken';


export function validateToken(jwtToken: any) {
  if (jwtToken) {
    const decoded = jwtDecode<UserToken>(jwtToken);

    if (decoded) {
      const expirationTimestamp = decoded.exp;

      // Get the current timestamp
      const currentTimestamp = Math.floor(Date.now() / 1000);

      // Check if the token has expired
      if (expirationTimestamp < currentTimestamp) {
        return true;
      } else {
        console.log('session expired');
      }
    }
  }

  return false;
}

export function decodeToken(jwtToken: any): UserToken | null {
  if (jwtToken) {
    const decoded = jwtDecode<UserToken>(jwtToken);
    return decoded;
  }

  return null;
}

export function clearToken() {
  localStorage.removeItem('token');
}

export function getToken() {
  const tokenString: string | null = localStorage.getItem('token')
  if (tokenString) {
    const userToken = JSON.parse(tokenString)
    return userToken?.token;
  }
}

export function saveToken(token: any) {
  localStorage.setItem('token', JSON.stringify({ "token": token }));
}