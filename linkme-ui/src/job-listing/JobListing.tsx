
import { useEffect, useState } from "react";
import Navbar from "../nav-bar/Navbar";
import Header from "../header/Header";
import JobSearch from "./JobSearch";
import JobCard from "../job-card/JobCard";
import { useNavigate } from 'react-router-dom';
import { getAllFilteredJobs, getAllJobs } from "../services/apiService";
import { JobData } from "../models/JobData";
import { JobCriteria } from "../models/JobCriteria";
import RecruiterJobApplications from "../job-application/RecruiterJobApplications";
import { decodeToken, getToken } from "../util/TokenUtility";
import { UserToken } from "../models/UserToken";
import JobPagination from "../util/JobPagination";
import PopChat from "../chat/PopChat";


function JobListing() {
  const [jobs, setJobs] = useState([]);

  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10); // Set your desired page size here
  const [totalPages, setTotalPages] = useState(0);

  const [customSearch, setCustomSearch] = useState(false);
  const navigate = useNavigate();

  const dummyMessages = [
    'Hello!',
    'How are you?',
    'I am good, thank you!',
    'Nice to meet you!',
  ];

  const [messages, setMessages] = useState(dummyMessages);

  const getMessage = (newMessage: any) => {
    // Logic to handle new messages
    setMessages([...messages, newMessage]);
  };

  const recruiterRole: string = "RECRUITER";

  const fetchJobs = async () => {
    const token = getToken();
    console.log('token passed in listing', token);

    if (!token) {
      navigate('/'); // navigate to login
    }

    setCustomSearch(false);
    const tempJobs: any = [];

    const response: any = await getAllJobs(token, currentPage, pageSize);

    console.log('got response', response);
    if (response && response.content) {
      let allJobs = response.content;

      allJobs.forEach((job: JobData) => {
        tempJobs.push({
          ...job,
          id: job.jobId,
          postedOn: new Date(job.postedOn)
        })
      });
      setJobs(tempJobs);
      setTotalPages(response.totalPages);

    }
  }

  const getUserRole = () => {
    const token = getToken();
    console.log('token passed in listing', token);

    if (!token) {
      navigate('/'); // navigate to login
    }

    let decodedToken: UserToken | null = decodeToken(token);

    let roles: string[] = [];

    if (decodedToken) {
      roles = decodedToken?.roles;
    }

    console.log('listing roles', roles);

    return roles;
  }

  const fetchJobsCustom = async (jobCriteria: JobCriteria) => {
    console.log('jobCriteria parent', jobCriteria);
    const token = getToken();
    console.log('token passed in listing', token);

    if (!token) {
      navigate('/'); // navigate to login
    }

    setCustomSearch(true);
    const tempJobs: any = [];

    const allJobs = await getAllFilteredJobs(jobCriteria.jobRole, jobCriteria.jobType, jobCriteria.location, jobCriteria.experience, jobCriteria.skills, token);

    if (allJobs) {
      allJobs.forEach((job: JobData) => {
        tempJobs.push({
          ...job,
          id: job.jobId,
          postedOn: new Date(job.postedOn)
        })
      });
      setJobs(tempJobs);
    }
  }


  useEffect(() => {
    fetchJobs()
  }
    , [currentPage, pageSize]
  );

  const handlePageChange = (page: any) => {
    setCurrentPage(page);
  };


  return (
    <div>
      <Navbar />
      {getUserRole().includes(recruiterRole) ? (
        <RecruiterJobApplications />
      ) : (
        <>
          <Header />
          <JobSearch fetchJobsCustom={fetchJobsCustom} />
          {customSearch && (
            <button onClick={fetchJobs} className="flex mb-2">
              <p className="bg-blue-500 px-10 py-2 rounded-md text-white">
                Clear Filters
              </p>
            </button>
          )}
          {jobs.map((job: any) => (
            <JobCard key={job.id} {...job} />
          ))}

          {/* Pagination controls */}
          <JobPagination
            totalPages={totalPages}
            currentPage={currentPage}
            onPageChange={handlePageChange}
          />
        </>
      )}
      <PopChat messages={messages} getMessage={getMessage} userType={getUserRole().includes(recruiterRole) ? "Candidate" : "Recruiter"} />
    </div>
  );


}

export default JobListing
