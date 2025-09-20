import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './HistoryPage.css'; 

const HistoryPage = () => {
  const [pastBookings, setPastBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchHistory = async () => {
      const user = JSON.parse(localStorage.getItem('user'));
      if (!user || !user.id) {
        alert('Please log in to view your history.');
        navigate('/login');
        return;
      }

      try {
        // This is the crucial line: it now calls the correct backend endpoint
        const response = await fetch(`http://localhost:8080/api/v1/bookings/my-history?passengerId=${user.id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch booking history.');
        }
        const data = await response.json();
        setPastBookings(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [navigate]);

  if (loading) {
    return <div className="history-container"><p>Loading history...</p></div>;
  }

  if (error) {
    return <div className="history-container"><p>Error: {error}</p></div>;
  }

  return (
    <div className="history-container">
      <h2>My Ride History</h2>
      {pastBookings.length === 0 ? (
        <p>You have no past bookings.</p>
      ) : (
        <ul className="booking-list">
          {pastBookings.map((booking) => (
            <li key={booking.id} className="booking-item">
              <div className="booking-details">
                <p><strong>From:</strong> {booking.ride.source}</p>
                <p><strong>To:</strong> {booking.ride.destination}</p>
                <p>
                  <strong>Date:</strong>{' '}
                  {new Date(booking.ride.dateTime).toLocaleDateString()}
                </p>
                <p><strong>Driver:</strong> {booking.ride.driver.name}</p>
              </div>
              <div className="booking-fare">
                <p><strong>Fare Paid:</strong> ₹{booking.fare.toFixed(2)}</p>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default HistoryPage;

