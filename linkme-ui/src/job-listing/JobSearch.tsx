import React, { useState } from 'react'
import { JobCriteria } from '../models/JobCriteria';

function JobSearch(props: any) {
    const [jobCriteria, setJobCriteria] = useState<JobCriteria>({
        jobRole: "",
        location: "",
        experience: "0.0",
        jobType: "",
        skills: [],
    });

    const handleChange = (e: any) => {
        const { name, value, options } = e.target;

        // Check if it's a multi-select
        const selectedValues = Array.from(options)
            .filter((option: any) => option.selected)
            .map((option: any) => option.value);

        setJobCriteria((prevState) => ({
            ...prevState,
            [name]: name === "skills" ? selectedValues : value,
        }));

        console.log('selected values', value);
        console.log('selected dropdown values', selectedValues);
    };

    const search = async () => {
        console.log('jobCriteria child', jobCriteria);
        await props.fetchJobsCustom(jobCriteria);
    };


    return (
        <div className='flex gap-4 my-10 justify-center px-10'>
            <select onChange={handleChange} name="jobRole" value={jobCriteria.jobRole} className='w-64 py-3 pl-4 bg-zinc-200 font-semibold rounded-md'>
                <option value="" disabled hidden>Job Role</option>
                <option value="Fullstack-Developer">Fullstack Developer</option>
                <option value="Frontend-Developer">Frontend Developer</option>
                <option value="Backend-Developer">Backend Developer</option>
                <option value="Android-Developer">Android Developer</option>
            </select>
            <select onChange={handleChange} name="jobType" value={jobCriteria.jobType} className='w-64 py-3 pl-4 bg-zinc-200 font-semibold rounded-md'>
                <option value="" disabled hidden>Job Type</option>
                <option value="Full-Time">Full Time</option>
                <option value="Part-Time">Part Time</option>
                <option value="Contract">Contract</option>
            </select>
            <select onChange={handleChange} name="location" value={jobCriteria.location} className='w-64 py-3 pl-4 bg-zinc-200 font-semibold rounded-md'>
                <option value="" disabled hidden>Location</option>
                <option value="Remote">Remote</option>
                <option value="In-Office">In-Office</option>
                <option value="Hybrid">Hybrid</option>
            </select>
            <select onChange={handleChange} name="experience" value={jobCriteria.experience} className='w-64 py-3 pl-4 bg-zinc-200 font-semibold rounded-md'>
                <option value="" disabled hidden>Experience</option>
                <option value="8">Experienced Level</option>
                <option value="0">Fresher</option>
                <option value="2">Junior Level</option>
                <option value="5">Mid Level</option>
                <option value="10">Senior Level</option>
            </select>
            <select onChange={handleChange} name="skills" value={jobCriteria.skills} className='w-64 py-3 pl-4 bg-zinc-200 font-semibold rounded-md' multiple>
                <option value="" disabled hidden>Skills</option>
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="c#">C#</option>
                <option value="html5">HTML5</option>
                <option value="css">CSS</option>
                <option value="golang">GOLANG</option>
                <option value="html">HTML</option>
                <option value="Java-Script">Java Script</option>
                <option value="json">JSON</option>
                <option value="team-management">Dev Manager</option>

            </select>
            <button onClick={search} className='w-64 bg-blue-500 text-white font-bold py-3 rounded-md'>Search</button>
        </div>
    )
}

export default JobSearch