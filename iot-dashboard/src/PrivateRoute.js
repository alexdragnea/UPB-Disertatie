import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "./AuthContext"; // Adjust the import path as necessary

const PrivateRoute = ({ Component }) => {
    const { isAuthenticated } = useContext(AuthContext); // Get authentication state from context

    return isAuthenticated ? <Component /> : <Navigate to="/login" />;
};

export default PrivateRoute;