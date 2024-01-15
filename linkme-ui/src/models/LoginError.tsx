export interface LoginError {
    httpStatus: number
    exception: any
    message: string
    fieldErrors: any
  }