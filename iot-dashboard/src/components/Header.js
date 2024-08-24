import React from 'react';
import { Link } from 'react-router-dom';
import { Navbar, Nav } from 'react-bootstrap';
import { FaUserCircle } from 'react-icons/fa'; // Importing user icon from react-icons
import 'bootstrap/dist/css/bootstrap.min.css';
import './Header.styles.scss'; // Optional, for custom styles

function Header() {
    return (
        <Navbar bg="primary" variant="dark" expand="lg" fixed="top" className="header">
            <Navbar.Brand as={Link} to="/admin/dashboard">Dashboard</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="ms-auto"> {/* Aligns the Nav items to the right */}
                    <Nav.Link as={Link} to="/admin/profile">
                        <FaUserCircle className="me-2" /> {/* User icon */}
                        Profile
                    </Nav.Link>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    );
}

export default Header;
