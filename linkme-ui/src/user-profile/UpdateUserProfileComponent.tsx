// UpdateUserProfileComponent.jsx
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import Select from 'react-select';
import { getSkillsOptions } from '../services/apiService';
import { updateProfile, downloadResume, loadProfileByEmail, getSalaryComparisonData } from '../services/userService';
import { decodeToken, getToken } from '../util/TokenUtility';
import { UserToken } from '../models/UserToken';
import Navbar from '../nav-bar/Navbar';
import SalaryGraphComponent from './SalaryGraphComponent';

const UpdateUserProfileComponent = () => {
    const navigate = useNavigate();
    let ref = useRef(null);

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        skills: [],
        workExperience: '',
        education: '',
        resume: {},
        email: '',
        location: '',
        salary: '',
        currencyCode: ''
    });

    const [skillsOptions, setSkillsOptions] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [profileExists, setProfileExists] = useState(false);
    const [fileUrl, setFileUrl] = useState('');
    const [email, setEmail] = useState('');
    const [profileId, setProfileId] = useState('');
    const [userId, setUserId] = useState(0);
    const [salaryComparisonData, setSalaryComparisonData] = useState([]);

    const [currencyCodeOptions, setCurrencyCodeOptions] = useState([
        { value: 'USD', label: '$' },
        { value: 'EUR', label: '€' },
        { value: 'INR', label: '₹' },
        // Add more currency codes with symbols as needed
    ]);

    useEffect(() => {
        const token = getToken();

        if (!token) {
            navigate('/'); // navigate to login
        }

        let decodedToken: UserToken | null = decodeToken(token);

        if (decodedToken) {
            setEmail(decodedToken.sub);
            setUserId(decodedToken.userId);
            let emailText = decodedToken.sub;
            setFormData((prevData) => ({ ...prevData, email: emailText }));
        }

        // Fetch skills options from API
        const fetchSkillsOptions = async () => {
            try {
                const options = await getSkillsOptions(token);
                setSkillsOptions(options);
            } catch (error) {
                // console.error('Error fetching skills options', error);
            }
        };

        // Load profile and set state
        const loadProfile = async () => {
            try {
                const token = getToken();

                if (!token) {
                    navigate('/'); // navigate to login
                }

                let decodedToken: UserToken | null = decodeToken(token);

                if (decodedToken) {
                    setEmail(decodedToken.sub);
                    setUserId(decodedToken.userId);
                    let emailText = decodedToken.sub;
                    setFormData((prevData) => ({ ...prevData, email: emailText }));

                    const existingProfile = await loadProfileByEmail(token, emailText);
                    if (existingProfile) {
                        setProfileExists(true);
                        setFormData(existingProfile);
                        setProfileId(existingProfile.profileId);
                        setSelectedSkills(existingProfile.skills);
                        setFileUrl(existingProfile.fileUrl);
                    }

                }
            } catch (error) {
                console.error('Error loading profile', error);
            }
        };

        fetchSkillsOptions();
        loadProfile();

        handleFileChange();
    }, [email, navigate]);

    const handleChange = (e: any) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const handleFileChange = () => {
        const fileInput = document.getElementById('requestFile');
        fileInput?.addEventListener('change', (event: any) => {
            const files = event?.target?.files;

            const file = files[0];

            console.log('file changed', file);

            ref.current = file;
            //setFormData((prevData) => ({ ...prevData, resume: file }));
        });
        //const file = e.target.files[0];
    };

    const handleDownloadResume = async () => {
        try {
            const token = getToken();

            if (!token) {
                navigate('/'); // navigate to login
            }

            await downloadResume(fileUrl, token);
            toast.success('file downloaded successfully.');
        } catch (error) {
            console.error('Error downloading resume', error);
            toast.error('file download failed. Please retry!');
        }
    };

    const handleCompareSalaries = async () => {
        try {
            // Call the API to get salary comparison data
            const token = getToken();

            if (!token) {
                navigate('/'); // navigate to login
            }

            const response = await getSalaryComparisonData(profileId, token);
            let salaryResponse: any = [];
            salaryResponse.push(response);
            console.log('salary compare data is', salaryResponse);
            setSalaryComparisonData(salaryResponse);
            // Show the salary comparison graph using a chart library (e.g., Chart.js)
            // You can update this part based on the chart library you choose
            // ...

        } catch (error) {
            console.error('Error comparing salaries', error);
            toast.error('Failed to compare salaries. Please retry!');
        }
    };

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        // Check for mandatory fields
        if (!formData.firstName || !formData.lastName || !formData.skills || !formData.workExperience || !formData.education || !formData.resume || !formData.location || !formData.salary || !formData.currencyCode) {
            toast.error('Please fill in all mandatory fields.');
            return;
        }

        try {
            // Call the update profile service
            const token = getToken();

            if (!token) {
                navigate('/'); // navigate to login
            }
            await updateProfile(formData, token, ref.current, userId);

            // Reload the profile after updating
            window.location.reload();


            // Handle success (e.g., show a success message)
            toast.success('Profile updated successfully');

        } catch (error: any) {
            // Handle error (e.g., show an error message)
            console.error('Error updating profile', error);
            toast.error("Failed to update profile");
        }
    };

    return (
        <>
            <Navbar />
            <div className="container mx-auto mt-8 px-4 update-profile">
                {profileExists ? (
                    <div className="mb-4 show-profile-data">
                        {/* Display existing profile details */}
                        <h3 className="text-xl font-semibold mb-2">Your Profile</h3>
                        <p>First Name: {formData.firstName}</p>
                        <p>Last Name: {formData.lastName}</p>
                        <p>Skills: {formData.skills.join(', ')}</p>
                        <p>Experience: {formData.workExperience}</p>
                        <p>
                            Salary: {formData.currencyCode}
                            {formData.salary}{' '}
                            <span
                                className="cursor-pointer text-blue-500"
                                onClick={handleCompareSalaries}
                            >
                                (Compare Salaries)
                            </span>
                        </p>

                        {salaryComparisonData.length > 0 && (
                            <div>
                                <h3 className="text-xl font-semibold mb-2">Compensation Ratio Graph</h3>
                                <SalaryGraphComponent salaryComparisonData={salaryComparisonData} />
                            </div>
                        )}

                        <p>Education: {formData.education}</p>
                        <p>Location: {formData.location}</p>

                        {/* Display download button for resume */}
                        <button
                            onClick={handleDownloadResume}
                            className="bg-blue-500 text-white px-4 py-2 mt-4 rounded hover:bg-blue-600 focus:outline-none focus:shadow-outline-blue active:bg-blue-800"
                        >
                            Download Resume
                        </button>
                    </div>

                ) : (
                    <>
                        <h2 className="text-2xl font-bold mb-4">Update Profile</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">First Name:</label>
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Last Name:</label>
                                    <input
                                        type="text"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                    />
                                </div>
                            </div>

                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Skills:</label>
                                <Select
                                    isMulti
                                    name="skills"
                                    options={skillsOptions}
                                    value={selectedSkills}
                                    onChange={(selectedOptions: any) => {
                                        let selectedValues: any = [];
                                        for (var key in selectedOptions) {
                                            //Or we can access the specific properties using
                                            console.log(selectedOptions[key].label);
                                            selectedValues.push(selectedOptions[key].label);
                                        }
                                        console.log('selected skills', selectedValues);
                                        setSelectedSkills(selectedOptions);
                                        setFormData((prevData: any) => ({ ...prevData, skills: selectedValues }));
                                    }}
                                    className="mt-1"
                                />
                            </div>

                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Experience:</label>
                                <input
                                    type="text"
                                    name="workExperience"
                                    value={formData.workExperience}
                                    onChange={handleChange}
                                    className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                />
                            </div>
                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Salary:</label>
                                <div className="flex items-center mt-1">
                                    <Select
                                        name="currencyCode"
                                        options={currencyCodeOptions}
                                        value={currencyCodeOptions.find(option => option.value === formData.currencyCode)}
                                        onChange={(selectedOption: any) => {
                                            setFormData((prevData) => ({ ...prevData, currencyCode: selectedOption.value }));
                                        }}
                                        className="w-1/4 mr-2"
                                        required
                                    />
                                    <input
                                        type="text"
                                        name="salary"
                                        value={formData.salary}
                                        onChange={handleChange}
                                        className="flex-1 p-2 border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                        placeholder="Enter salary"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Education:</label>
                                <input
                                    type="text"
                                    name="education"
                                    value={formData.education}
                                    onChange={handleChange}
                                    className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                />
                            </div>

                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Resume:</label>
                                <input
                                    id="requestFile"
                                    type="file"
                                    name="resume"
                                    //  onChange={handleFileChange}
                                    className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                />
                            </div>

                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700">Location:</label>
                                <input
                                    type="text"
                                    name="location"
                                    value={formData.location}
                                    onChange={handleChange}
                                    className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                />

                            </div>

                            <input
                                type="hidden"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                className="mt-1 p-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                            />

                            <div className="mt-6">
                                <button
                                    type="submit"
                                    className="inline-block w-full bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 focus:outline-none focus:shadow-outline-blue active:bg-blue-800"
                                >
                                    Update Profile
                                </button>
                            </div>
                        </form>
                    </>
                )}
            </div>
        </>
    );
};

export default UpdateUserProfileComponent;
