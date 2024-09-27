import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "./AuthContext"; // Adjust the import path as necessary

const PrivateRoute = ({ Component }) => {
    const { isAuthenticated, loading } = useContext(AuthContext); // Get authentication state from context

    if (loading) {
        return <div>Loading...</div>; // Optional loading indicator
    }

    return isAuthenticated ? <Component /> : <Navigate to="/login" />;
};

export default PrivateRoute;
