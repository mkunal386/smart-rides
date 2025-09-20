// src/components/Navbar.jsx
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css";

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // just redirect for now
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <h2 className="logo">Smart Rides</h2>
      <div className="nav-links">
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/host-ride">Host Ride</Link>
        {/* Updated link from Join Ride to Book Ride */}
        <Link to="/book-ride">Book Ride</Link>
        {/* New link for History */}
        <Link to="/history">History</Link>
        <button onClick={handleLogout} className="logout-btn">
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
