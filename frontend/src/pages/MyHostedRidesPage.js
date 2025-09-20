import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './MyHostedRidesPage.css';

const MyHostedRidesPage = () => {
    const [hostedRides, setHostedRides] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // --- New State for Pending Bookings ---
    const [pendingBookings, setPendingBookings] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false);

    const user = JSON.parse(localStorage.getItem('user'));

    // --- New function to fetch both hosted rides and pending bookings ---
    const fetchAllData = async () => {
        if (!user || !user.id) {
            alert("You must be logged in to view your hosted rides.");
            navigate('/login');
            return;
        }

        try {
            // Fetch hosted rides
            const ridesResponse = await fetch(`http://localhost:8080/api/rides/driver/${user.id}`);
            if (!ridesResponse.ok) throw new Error('Failed to fetch your hosted rides.');
            const ridesData = await ridesResponse.json();

            // --- New API call to fetch pending bookings for the driver ---
            const bookingsResponse = await fetch(`http://localhost:8080/api/v1/bookings/driver-requests/${user.id}`);
            if (!bookingsResponse.ok) throw new Error('Failed to fetch pending bookings.');
            const bookingsData = await bookingsResponse.json();

            setHostedRides(ridesData);
            setPendingBookings(bookingsData);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchAllData();
    }, [navigate]);
    
    // --- New function to handle driver's decision (accept/reject) ---
    const handleDriverResponse = async (bookingId, decision) => {
        setIsProcessing(true);
        try {
            const payload = {
                bookingId: bookingId,
                driverId: user.id,
                decision: decision
            };
            
            const response = await fetch('http://localhost:8080/api/v1/bookings/driver-response', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to process request.');
            }
            
            // Refetch all data to update the UI
            fetchAllData();

            alert(`Ride ${decision}ed successfully!`);

        } catch (err) {
            setError(err.message);
        } finally {
            setIsProcessing(false);
        }
    };


    if (isLoading) return <div className="loading-message">Loading your rides...</div>;
    if (error) return <div className="error-message">{error}</div>;

    return (
        <div className="history-page-container">
            <h2>My Hosted Rides</h2>
            {hostedRides.length > 0 ? (
                <ul className="ride-history-list">
                    {hostedRides.map(ride => {
                        const rideBookings = pendingBookings.filter(booking => booking.ride.id === ride.id);
                        return (
                            <li key={ride.id} className="ride-history-item">
                                <div className="ride-info">
                                    <p><strong>Route:</strong> {ride.source} to {ride.destination}</p>
                                    <p><strong>Date:</strong> {new Date(ride.dateTime).toLocaleString()}</p>
                                    <p><strong>Car:</strong> {ride.carModel} ({ride.plateNumber})</p>
                                </div>
                                <div className="ride-stats">
                                    <p><strong>Fare:</strong> ₹{ride.fare.toFixed(2)}</p>
                                    <p><strong>Seats:</strong> {ride.availableSeats} available</p>
                                </div>
                                
                                {/* --- New section for pending requests --- */}
                                {rideBookings.length > 0 && (
                                    <div className="pending-requests">
                                        <h4>Pending Requests ({rideBookings.length})</h4>
                                        {rideBookings.map(booking => (
                                            <div key={booking.id} className="request-item">
                                                <p>Passenger: {booking.passenger.name}</p>
                                                <p>Seats: {booking.seatsBooked}</p>
                                                <div className="request-actions">
                                                    <button 
                                                        onClick={() => handleDriverResponse(booking.id, 'accept')}
                                                        disabled={isProcessing}
                                                        className="accept-button"
                                                    >
                                                        Accept
                                                    </button>
                                                    <button 
                                                        onClick={() => handleDriverResponse(booking.id, 'reject')}
                                                        disabled={isProcessing}
                                                        className="reject-button"
                                                    >
                                                        Reject
                                                    </button>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </li>
                        );
                    })}
                </ul>
            ) : (
                <div className="no-rides-message">
                    <p>You haven't hosted any rides yet.</p>
                    <Link to="/host-ride" className="action-link">Host Your First Ride</Link>
                </div>
            )}
        </div>
    );
};

export default MyHostedRidesPage;