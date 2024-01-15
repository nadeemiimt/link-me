// Import React if not already imported
import React from 'react';

interface JobPaginationProps {
    totalPages: number;
    currentPage: number;
    onPageChange: (page: number) => void;
}

const JobPagination: React.FC<JobPaginationProps> = ({ totalPages, currentPage, onPageChange }) => {
    const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1);

    return (
        <div className="pagination-container">
            {pageNumbers.map((page) => (
                <button
                    key={page}
                    className="pagination-btn"
                    onClick={() => onPageChange(page)}
                    disabled={page === currentPage}
                >
                    {page}
                </button>
            ))}
        </div>
    );
};

export default JobPagination;