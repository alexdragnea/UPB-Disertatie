import React, { useState, useContext } from "react";
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  FormGroup,
  Form,
  Input,
  Row,
  Col,
  Alert,
} from "reactstrap";
import { useNavigate } from "react-router-dom"; // Use useNavigate for redirection
import { AuthContext } from "../AuthContext"; // Import AuthContext for user details
import '../assets/css/UserProfile.css'; // Import a CSS file for custom styles

function UserProfile() {
  const { user, refreshToken } = useContext(AuthContext); // Access user and refreshToken from AuthContext
  const navigate = useNavigate(); // Get navigate for redirection
  const [firstName, setFirstName] = useState(user?.firstName || "");
  const [lastName, setLastName] = useState(user?.lastName || "");
  const [email, setEmail] = useState(user?.email || "");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [successMessage, setSuccessMessage] = useState(""); // State for success messages
  const [profileSuccess, setProfileSuccess] = useState(""); // State for profile update success message
  const [passwordSuccess, setPasswordSuccess] = useState(""); // State for password change success message

  // Validate form fields
  const isValidForm = () => {
    setErrorMessage(""); // Reset error message
    setPasswordError(""); // Reset password error message
    let isValid = true;

    if (!firstName || !lastName || !email) {
      setErrorMessage("All fields are required.");
      isValid = false;
    }

    if (newPassword && newPassword !== confirmPassword) {
      setPasswordError("New password and confirm password do not match.");
      isValid = false;
    }

    return isValid;
  };

  // Handle profile update
  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    
    if (!isValidForm()) return; // Validate before submitting

    const token = localStorage.getItem("accessToken");

    try {
      const response = await fetch("https://localhost:8888/v1/iot-user/profile", {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email,
          firstName,
          lastName,
        }),
      });

      console.log("Profile update response:", response);

      if (!response.ok) {
        if (response.status === 401) { // Unauthorized access
          await refreshToken(); // Attempt to refresh token
          return handleProfileSubmit(e); // Retry profile update after token refresh
        }
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update profile");
      }

      await refreshToken(); // Refresh token to get new user data
      setProfileSuccess("Profile updated successfully!"); // Set success message
      setSuccessMessage(""); // Clear any previous success messages
      navigate('/success'); // Redirect to the success page after successful update
    } catch (error) {
      console.error("Profile update error:", error);
      setErrorMessage(error.message);
    }
  };

  // Handle password change
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    
    if (!isValidForm()) return; // Validate before submitting

    const token = localStorage.getItem("accessToken");

    try {
      const response = await fetch("https://localhost:8888/v1/iot-user/password", {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          password: newPassword, // Send the new password to the server
        }),
      });

      console.log("Password change response:", response);

      if (!response.ok) {
        if (response.status === 401) { // Unauthorized access
          await refreshToken(); // Attempt to refresh token
          return handlePasswordSubmit(e); // Retry password change after token refresh
        }
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to change password");
      }

      await refreshToken(); // Refresh token to get new user data
      setPasswordSuccess("Password changed successfully!"); // Set success message
      setSuccessMessage(""); // Clear any previous success messages
      navigate('/success'); // Redirect to the success page after successful update
    } catch (error) {
      console.error("Password change error:", error);
      setPasswordError(error.message);
    }
  };

  return (
    <>
      <div className="content">
        <Row>
          <Col md="8">
            <Card className="profile-card">
              <CardHeader>
                <h5 className="user-title">Edit Profile</h5>
              </CardHeader>
              <CardBody>
                <Form onSubmit={handleProfileSubmit}>
                  <Row>
                    <Col className="pr-md-1" md="6">
                      <FormGroup>
                        <label>First Name</label>
                        <Input
                          value={firstName}
                          placeholder="First Name"
                          type="text"
                          onChange={(e) => setFirstName(e.target.value)}
                          required
                        />
                      </FormGroup>
                    </Col>
                    <Col className="pl-md-1" md="6">
                      <FormGroup>
                        <label>Last Name</label>
                        <Input
                          value={lastName}
                          placeholder="Last Name"
                          type="text"
                          onChange={(e) => setLastName(e.target.value)}
                          required
                        />
                      </FormGroup>
                    </Col>
                  </Row>
                  <Row>
                    <Col md="12">
                      <FormGroup>
                        <label>Email address</label>
                        <Input
                          value={email}
                          placeholder="Email address"
                          type="email"
                          onChange={(e) => setEmail(e.target.value)}
                          required
                        />
                      </FormGroup>
                    </Col>
                  </Row>
                  {errorMessage && <Alert color="danger">{errorMessage}</Alert>}
                  {profileSuccess && <Alert color="success">{profileSuccess}</Alert>}
                  <Button className="btn-fill" color="primary" type="submit">
                    Save Profile
                  </Button>
                </Form>
              </CardBody>
            </Card>
          </Col>

          {/* Password Change Section */}
          <Col md="4">
            <Card className="password-card">
              <CardHeader>
                <h5 className="user-title">Change Password</h5>
              </CardHeader>
              <CardBody>
                <Form onSubmit={handlePasswordSubmit}>
                  <FormGroup>
                    <label>New Password</label>
                    <Input
                      placeholder="New Password"
                      type="password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      required
                    />
                  </FormGroup>
                  <FormGroup>
                    <label>Confirm New Password</label>
                    <Input
                      placeholder="Confirm New Password"
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      required
                    />
                  </FormGroup>
                  {passwordError && (
                    <Alert color="danger">{passwordError}</Alert>
                  )}
                  {passwordSuccess && <Alert color="success">{passwordSuccess}</Alert>}
                  <Button className="btn-fill" color="primary" type="submit">
                    Save Password
                  </Button>
                </Form>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    </>
  );
}

export default UserProfile;
