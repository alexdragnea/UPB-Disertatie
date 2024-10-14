import React from 'react';
import { Card, CardBody, CardHeader, Button } from 'reactstrap';
import { useNavigate } from 'react-router-dom';

const SuccessPage = () => {
  const navigate = useNavigate();

  const handleRedirect = () => {
    // Redirect to the login page
    navigate('/login'); // Change this to your actual login route
  };

  return (
    <div className="content">
      <Card className="success-card">
        <CardHeader>
          <h5 className="title">Success!</h5>
        </CardHeader>
        <CardBody>
          <h4>Your profile/password has been updated successfully!</h4>
          <p>Please log in again to continue.</p>
          <Button color="primary" onClick={handleRedirect}>
            Go to Login
          </Button>
        </CardBody>
      </Card>
    </div>
  );
};

export default SuccessPage;
