export interface SalaryComparison {
    comparisonId: number;
    profileId: number;
    salaryAmount: number;
    location: string;
    status: string;
    updatedOn: Date;
    employeeMidPointSalaryForLocation: number;
}