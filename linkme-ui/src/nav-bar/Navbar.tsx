import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { clearToken } from '../util/TokenUtility';

const Navbar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const navigate = useNavigate();

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  const closeDropdown = () => {
    setDropdownOpen(false);
  };

  const handleSignOut = () => {
    clearToken();

    setTimeout(() => {
      navigate('/');
    }, 500);
  };

  const handleChangePassword = () => {
    // Implement your change password logic here
  };

  const handleUpdateProfile = () => {
    closeDropdown(); // Close the dropdown when navigating
    navigate('/update-profile'); // Navigate to the UpdateUserProfileComponent
  };

  return (
    <div className='h-20 flex items-center w-full text-white'>
      <Link to="/home" className='text-3xl pl-20 font-bold company-logo'>
        <img src={process.env.PUBLIC_URL + '/LinkMeLogo.png'} alt="Profile" />
      </Link>

      <div className='ml-auto pr-20 relative'>
        <div className='profile-icon' onMouseDown={toggleDropdown}>
          <img src={process.env.PUBLIC_URL + '/Nadeem.png'} alt="Profile" />
        </div>

        {dropdownOpen && (
          <div className='dropdown'>
            <ul>
              <li onMouseDown={handleUpdateProfile}>Update Profile</li>
              <li onMouseDown={handleChangePassword}>Change Password</li>
              <li onMouseDown={handleSignOut}>Sign Out</li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default Navbar;