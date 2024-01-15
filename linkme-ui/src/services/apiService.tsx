import { JobData } from "../models/JobData";

const API_BASE_URL = 'http://localhost:8080';

export const getAllJobs = (jwtToken: string, page: number, size: number) => {
    var myHeaders = new Headers();
    myHeaders.append("Accept", "application/json");
    setBearerToken(myHeaders, jwtToken);

    var requestOptions: RequestInit = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    return fetch(`${API_BASE_URL}/api/jobListings?page=${page}&size=${size}`, requestOptions)
        .then(response => response.json())
        .then(jsonResponse => convertToJobDataFromPagedResponse(jsonResponse))
        .catch(error => console.log('error', error));
}

const convertToJobDataFromPagedResponse = (responseArray: any): any => {
    try {
        if (responseArray) {
            const response: any = responseArray;

            // You can also extract other pagination-related information
            const totalPages = response.totalPages || 0;
            const totalElements = response.totalElements || 0;
            const currentPage = response.number || 0;

            console.log('Pagination Info:', { totalPages, totalElements, currentPage });

            return response;
        } else {
            console.error('Invalid JSON structure. Expected an array.');
            return [];
        }
    } catch (e) {
        console.error('Error while parsing JSON:', e);
        return [];
    }
};

const convertToJobData = (responseArray: any): JobData[] => {
    try {
        if (Array.isArray(responseArray)) {
            return responseArray;
        } else {
            console.error('Invalid JSON structure. Expected an array.');
            return [];
        }
    } catch (e) {
        console.error('Error while parsing JSON:', e);
        return [];
    }
};

export const getAllFilteredJobs = (jobRole: string, jobType: string, location: string, experience: string, skills: string[], jwtToken: string) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Accept", "application/json");
    setBearerToken(myHeaders, jwtToken);

    var searchFilter: any = JSON.stringify({
        skills: skills,
        jobRole: jobRole,
        jobType: jobType,
        location: location,
        experience: experience
    });

    var requestOptions: RequestInit = {
        method: 'POST',
        headers: myHeaders,
        body: searchFilter,
        redirect: 'follow'
    };

    return fetch(`${API_BASE_URL}/api/jobListings/search`, requestOptions)
        .then(response => response.json())
        .then(jsonResponse => convertToJobData(jsonResponse))
        .catch(error => console.log('error', error));
}

export const applyForJob = (jobId: number, userId: number, status: string, jwtToken: string) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Accept", "application/json");
    setBearerToken(myHeaders, jwtToken);

    var searchFilter: any = JSON.stringify({
        job: jobId,
        user: userId,
        status: status
    });

    var requestOptions: RequestInit = {
        method: 'POST',
        headers: myHeaders,
        body: searchFilter,
        redirect: 'follow'
    };

    return fetch(`${API_BASE_URL}/api/userJobApplications`, requestOptions)
        .then(response => response.json())
        .then(jsonResponse => convertToJobData(jsonResponse))
        .catch(error => console.log('error', error));
}

export const getSkillsOptions = async (jwtToken: any) => {
    try {
        var myHeaders = new Headers();
        myHeaders.append("Accept", "application/json");
        setBearerToken(myHeaders, jwtToken);

        var requestOptions: RequestInit = {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow'
        };

        return fetch(`${API_BASE_URL}/api/skills`, requestOptions)
            .then(response => response.json())
            .then(jsonResponse => jsonResponse.map((skill: any) => ({
                value: skill.skillName,
                label: skill.skillName,
            })))
            .catch(error => {
                console.log('error', error);
                return []; // Return an empty array in case of an error
            });

    } catch (error: any) {
        throw new Error(`Error in getSkillsOptions: ${error.message}`);
    }
};

export function setBearerToken(myHeaders: Headers, jwtToken: string) {
    const bearerToken = `Bearer ${jwtToken}`;
    console.log('bearer token passed', bearerToken);

    myHeaders.append("Authorization", bearerToken);
}

