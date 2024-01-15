import { createBrowserRouter } from "react-router-dom";
import App from "../App";
import JobListing from "../job-listing/JobListing";
import Register from "../user/Register";
import UpdateUserProfileComponent from "../user-profile/UpdateUserProfileComponent";
import ErrorPage from "../util/ErrorPage";

function NoMatch() {
  return (
    <div style={{ padding: 20 }}>
      <h2>404: Page Not Found</h2>
      <p>Lorem ipsum dolor sit amet, consectetur adip.</p>
    </div>
  );
}


const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />
  },
  {
    path: "/home",
    element: <JobListing />,
    errorElement: <ErrorPage />
  },
  {
    path: "/register",
    element: <Register />,
    errorElement: <ErrorPage />
  },
  {
    path: "/login",
    element: <App />,
    errorElement: <ErrorPage />
  },
  {
    path: "/update-profile",
    element: <UpdateUserProfileComponent />,
    errorElement: <ErrorPage />
  },
  {
    path: "*",
    element: <NoMatch />,
    errorElement: <ErrorPage />
  },
]);

export default router; 