import React, { useState } from 'react'
import dayjs from 'dayjs'
import { decodeToken, getToken } from '../util/TokenUtility';
import { useNavigate } from 'react-router';
import { applyForJob } from '../services/apiService';
import { UserToken } from '../models/UserToken';
import { toast } from 'react-toastify';

function JobCard(props: any) {
    // const skills = ["Javascript", "React", "Nodejs"];
    const date1 = dayjs(Date.now());
    const diffInDays = date1.diff(props.postedOn, 'day');
    const [applicationStatus, setApplicationStatus] = useState(props.applicationStatus);
    const navigate = useNavigate();

    const handleApplyJob = async () => {
        try {
            const token = getToken();

            if (!token) {
                navigate('/'); // navigate to login
            }

            let decodedToken: UserToken | null = decodeToken(token);
            if (decodedToken != null) {
                await applyForJob(props.jobId, decodedToken.userId, 'Applied', token);
                setApplicationStatus('Applied');
                toast.success(`${props.title} applied.`);
            }
        } catch (error) {
            setApplicationStatus('');
            toast.success(`${props.title} application failed. Please retry!`);
        }
    };

    return (
        <div className='mx-40 mb-4'>
            <div className='flex justify-between items-center px-6 py-4 bg-zinc-200 rounded-md border border-black shadow-lg hover:border-blue-500 hover:translate-y-1 hover:scale-103'>
                <div className='flex flex-col items-start gap-3'>
                    <h1 className='text-lg font-semibold'>{props.title} - {props.company}</h1>
                    <p>{props.jobType} &#x2022; {props.experience} &#x2022; {props.location}</p>
                    <div className='flex items-center gap-2'>
                        {props.skills.map((skill: string, i: number) => (
                            <p key={i} className='text-gray-500 py-1 px-2 rounded-md border border-black'>{skill}</p>
                        ))}
                    </div>
                </div>
                <div className='flex items-center gap-4'>
                    <p className='text-gray-500'>Posted {diffInDays > 1 ? `${diffInDays} days` : `${diffInDays} day`} ago</p>
                    <a href={props.jobLink} target="_blank" rel="noopener noreferrer">
                        {applicationStatus && applicationStatus === 'Applied' ? <p>{applicationStatus}</p> : <button className='text-blue-500 border border-blue-500 px-10 py-2 rounded-md' onClick={handleApplyJob}>Apply</button>}
                    </a>

                </div>
            </div>
        </div>
    )
}

export default JobCard