import { Chart } from "react-google-charts";
import React from 'react';
import { SalaryGraphProps } from '../models/SalaryGraphProps';
import { FaSync } from 'react-icons/fa';

const SalaryGraphComponent: React.FC<SalaryGraphProps> = ({ salaryComparisonData }) => {
  const handleRefreshButton = (e: any) => {
    e.preventDefault();
    window.location.reload();
  };

  if (salaryComparisonData[0]?.status === 'UPDATED') {

 const data = [
  ["Ratio", "Average", "Employee"],
  ["Salary", 1, salaryComparisonData[0].employeeMidPointSalaryForLocation / salaryComparisonData[0].salaryAmount],
  ["Salary", 1, salaryComparisonData[0].employeeMidPointSalaryForLocation / salaryComparisonData[0].salaryAmount],
  ["Salary", 1, salaryComparisonData[0].employeeMidPointSalaryForLocation / salaryComparisonData[0].salaryAmount]
];

const options = {
  title: "Salary Comp Ratio",
  curveType: "function",
  legend: { position: "bottom" },
};

  return (
    <>
    <Chart
      chartType="LineChart"
      width="100%"
      height="400px"
      data={data}
      options={options}
    />
    <br/>
    </>
  );
  } else {
    return (
        <>
        <p>Status: {salaryComparisonData[0]?.status} </p>
        <p>
          <button
            onClick={handleRefreshButton}
            className="bg-blue-500 text-white px-4 py-2 mt-4 rounded hover:bg-blue-600 focus:outline-none focus:shadow-outline-blue active:bg-blue-800 flex items-center"
          >
            <FaSync className="mr-2" /> Reload
          </button>{' '}
        </p>
        <br/>
      </>
    );
  }
};

export default SalaryGraphComponent;
