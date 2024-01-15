import { setBearerToken } from "./apiService";

const API_BASE_URL = 'http://localhost:8080';

export const loginUser = (email: string, password: string) => {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  myHeaders.append("Accept", "*/*");

  var raw = JSON.stringify({
    "email": email,
    "password": password
  });

  var requestOptions: RequestInit = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch(`${API_BASE_URL}/rest/auth/login`, requestOptions)
    .then(response => response.json())
    .catch(error => console.log('error', error));
}

export const register = (email: string, password: string, name: string, isRecruiter: boolean) => {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  myHeaders.append("Accept", "application/json");

  var raw = JSON.stringify({
    "email": email,
    "password": password,
    "recruiter": isRecruiter,
    "name": name,
  });

  var requestOptions: RequestInit = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch(`${API_BASE_URL}/rest/auth/register`, requestOptions)
    .then(response => response.json())
    .catch(error => console.log('error', error));
}

export const updateProfile = async (formData: any, jwtToken: any, file: any, userId: any) => {
  const url = `${API_BASE_URL}/api/profiles`;

  const firstName: string = formData.firstName;
  const lastName: string = formData.lastName;
  const skills: [] = formData.skills;
  const workExperience: string = formData.workExperience;
  const education: string = formData.education;
  const email: string = formData.email;

  var formInputData = new FormData();

  formInputData.append("file", file, file.name);
  formInputData.append("body", new Blob([JSON.stringify({
    firstName: firstName,
    lastName: lastName,
    email: email,
    skills: skills,
    workExperience: workExperience,
    education: education,
    location: formData.location,
    userId: userId
  })], { type: "application/json" }));

  var myHeaders = new Headers();
  myHeaders.append("Accept", "*/*");

  setBearerToken(myHeaders, jwtToken);

  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: formInputData
  };

  const response = await fetch(url, requestOptions);
  if (!response.ok) {
    let error: any = response.json();
    throw new Error(error?.message || 'Failed to update profile');
  }

  return response;
}

export const loadProfileByEmail = (jwtToken: string, email: string) => {
  var myHeaders = new Headers();
  myHeaders.append("Accept", "application/json");  // multipart/form-data
  setBearerToken(myHeaders, jwtToken);

  var requestOptions: RequestInit = {
    method: 'GET',
    headers: myHeaders,
    redirect: 'follow'
  };

  return fetch(`${API_BASE_URL}/api/profiles/${email}`, requestOptions)
    .then(response => response ? response.json() : '')
    .catch(error => {
      console.log('no profile', error);
    });
}


export const downloadResume = async (fileUrl: any, jwtToken: any) => {
  console.log('fileUrl', fileUrl);
  try {
    var myHeaders = new Headers();
    myHeaders.append("Accept", "*/*");
    myHeaders.append("Content-Type", "application/json");
    setBearerToken(myHeaders, jwtToken);

    let uri: any = new URL(fileUrl);
    const key: any = uri.pathname.split('/').pop();

    const response = await fetch(`${API_BASE_URL}/api/profiles/resume/${key}`, {
      method: 'GET',
      headers: myHeaders,
      redirect: 'follow'
    });

    if (!response.ok) {
      // Handle the error, e.g., show an error message
      console.error(`Failed to download resume. Status: ${response.status}`);
      return;
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);

    // Create a temporary link and trigger the download
    const link = document.createElement('a');
    link.href = url;
    link.download = 'resume.pdf';  // Set the desired file name
    document.body.appendChild(link);
    link.click();

    // Clean up and remove the link
    document.body.removeChild(link);
  } catch (error) {
    // Handle other errors, e.g., show an error message
    console.error('Error downloading resume', error);
  }
};