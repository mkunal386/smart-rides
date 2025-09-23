// src/App.js
import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

import SignupPage from "./pages/SignupPage";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import HostRidePage from "./pages/HostRidePage";
import BookRidePage from "./pages/BookRidePage";
import PaymentPage from "./pages/PaymentPage";

// --- CHANGED: The old HistoryPage is removed ---
// import HistoryPage from "./pages/HistoryPage"; 

// --- ADDED: Import the new, specific history pages ---
import MyHostedRidesPage from "./pages/MyHostedRidesPage";
import MyBookedRidesPage from "./pages/MyBookedRidesPage";

import Navbar from "./components/Navbar";
import AdminDashboard from "./pages/AdminDashboard"; // Change: Import the AdminDashboard component

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirect root (/) to /signup */}
        <Route path="/" element={<Navigate to="/signup" />} />

        {/* Pages WITHOUT Navbar */}
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />

        {/* Pages WITH Navbar */}
        <Route
          path="/dashboard"
          element={
            <>
              <Navbar />
              <DashboardPage />
            </>
          }
        />
        <Route
          path="/host-ride"
          element={
            <>
              <Navbar />
              <HostRidePage />
            </>
          }
        />
        <Route
          path="/book-ride"
          element={
            <>
              <Navbar />
              <BookRidePage />
            </>
          }
        />
        <Route
          path="/payment"
          element={
            <>
              <Navbar />
              <PaymentPage />
            </>
          }
        />
        
        {/* --- REMOVED: The old generic history route is gone --- */}
        {/* <Route
          path="/history"
          element={
            <>
              <Navbar />
              <HistoryPage />
            </>
          }
        /> 
        */}

        {/* --- ADDED: The new routes for specific user histories --- */}
        <Route
          path="/my-hosted-rides"
          element={
            <>
              <Navbar />
              <MyHostedRidesPage />
            </>
          }
        />
        <Route
          path="/my-booked-rides"
          element={
            <>
              <Navbar />
              <MyBookedRidesPage />
            </>
          }
        />

        {/* Change: Add the new admin dashboard route */}
        <Route
          path="/admin/dashboard"
          element={
            <>
              <Navbar />
              <AdminDashboard />
            </>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;