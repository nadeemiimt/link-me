
# Link Me - Job Portal

Link Me is a Job portal application suite where Recruiter can post job and Job Seeker can apply for the job.


![Logo](https://i.ibb.co/RpNxvzr/Link-Me-Logo.png)


## Installation (In Order)

### 1. Clone the project

```bash
  git clone git@github.com:nadeemiimt/link-me.git
```

Go to the project directory

### 2. Link Me API 
#### 1. Generae token from file.io 
#### 2. Replace the token in application.properties for below key
`
temp.file.io.key 
`

#### 3. Run the application

`LinkmeApplication is main spring boot entry point`

### 3. Link Me UI
#### 1. run below commands

```bash
  cd linkme-ui
  npm install
  npm run build
  npm start
```

### 4. Link Me Scheduler(Optional)

#### 1. Generate token from https://api.courier.com
#### 2. Replace the token in application.properties for below key
`
courier.api.token 
`

#### 3. Run the application

## Run Locally

### 1. Open http://localhost:3000/ in browser (Chrome)
### 2. Login with below 2 type of credentials
#### 1. For Job Seeker

`
user name: nadeemiimt@gmail.com
`
`
password: test1
`

#### 1. For Recruiter

`
user name: companyhr@gmail.com
`
`
password: test2
`



## Demo

https://youtu.be/eMELJ7ISPGg




## Screenshots

![App Screenshot](https://i.ibb.co/5x3v86t/Screenshot-2024-01-15-at-10-47-03-PM.png)

![App Screenshot](https://i.ibb.co/SfyGhqs/Screenshot-2024-01-15-at-10-47-18-PM.png)

![App Screenshot](https://i.ibb.co/D4t44vz/Screenshot-2024-01-15-at-10-48-23-PM.png)

![App Screenshot](https://i.ibb.co/j5nvsGm/Screenshot-2024-01-15-at-10-48-39-PM.png)


## Features

- Login / Register
- JWT Token validation
- Role based features
- Hazelcast cache for frequent requests
- Elastic search query for better search performance
- Kafka Producer and Consumer for event based , non blocking operations
- Upload resume and download resume from external file storage (File.IO)
- Scheduler for recommendation engine with email integration
- Dummy chat UI


## Roadmap

- Add feature for Recruiter
- UI/UX improvements
- Unit test coverage for UI and backend
- More validations in UI and Backend
- Live chat with WebSockets
- CI/ CD pipelines


## API Reference

# LinkMe API
Job Portal.

## Version: 1.0

**Contact information:**  
Mohd Nadeem  
www.mohdnadeem.in  
nadeemiimt@gmail.com  

**License:** License of API

### /api/users/{userId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| userId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| userId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| userId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /api/userJobApplications/{applicationId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| applicationId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| applicationId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| applicationId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /api/skills/{skillId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| skillId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| skillId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| skillId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /api/salaryComparisons/{comparisonId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| comparisonId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| comparisonId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| comparisonId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /api/messages/{messageId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| messageId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| messageId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| messageId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /api/jobListings/{jobId}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| jobId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### PUT
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| jobId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### DELETE
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| jobId | path |  | Yes | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 204 | No Content |
| 400 | Error |

### /rest/auth/register

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

### /rest/auth/login

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

### /api/userJobApplications

#### GET
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Error |

### /api/skills

#### GET
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Error |

### /api/salaryComparisons

#### GET
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Error |

### /api/profiles

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

### /api/messages

#### GET
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Error |

### /api/jobListings

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| page | query |  | No | integer |
| size | query |  | No | integer |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Error |

### /api/jobListings/search

#### POST
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

### /api/users

#### GET
##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

### /api/profiles/{email}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| email | path |  | Yes | string |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |

### /api/profiles/resume/{fileKey}

#### GET
##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| fileKey | path |  | Yes | string |

##### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |
| 400 | Error |



## License

[MIT](https://choosealicense.com/licenses/mit/)


## Authors

### Mohd Nadeem
- [@github](https://www.github.com/nadeemiimt)
- [@linkedin](https://www.linkedin.com/in/nadeemiimt)

