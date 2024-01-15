export interface UserToken {
    sub: string
    name: string
    userId: number
    roles: string[]
    exp: number
  }