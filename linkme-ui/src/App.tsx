import JobListing from "./job-listing/JobListing";
import Login from "./user/Login";
import { getToken, validateToken } from "./util/TokenUtility";

export default function App() {
  //const {token, setToken} = useToken();

  return (
    <>
      {validateToken(getToken()) ? <JobListing /> : <Login />}
    </>
  );
}