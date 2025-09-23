import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './MyHostedRidesPage.css';

const MyHostedRidesPage = () => {
    const [hostedRides, setHostedRides] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const [pendingBookings, setPendingBookings] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false);
    
    // --- Change: New state for managing the 'End Ride' button loading state ---
    const [isEndingRide, setIsEndingRide] = useState(false);

    const user = JSON.parse(localStorage.getItem('user'));

    const fetchAllData = async () => {
        if (!user || !user.id) {
            alert("You must be logged in to view your hosted rides.");
            navigate('/login');
            return;
        }
        
        // Use a common function to fetch all data to avoid code duplication
        const fetchData = async (url) => {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Failed to fetch data.');
            return response.json();
        };

        try {
            const ridesData = await fetchData(`http://localhost:8080/api/rides/driver/${user.id}`);
            const bookingsData = await fetchData(`http://localhost:8080/api/v1/bookings/driver-requests/${user.id}`);

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
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to process request.');
            }
            
            fetchAllData();
            alert(`Ride ${decision}ed successfully!`);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsProcessing(false);
        }
    };
    
    // --- Change: New function to handle ending a ride ---
    const handleEndRide = async (rideId) => {
        setIsEndingRide(true);
        try {
            const response = await fetch(`http://localhost:8080/api/rides/end/${rideId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            });
            
            if (!response.ok) {
                throw new Error('Failed to end the ride.');
            }
            
            alert('Ride has been successfully completed!');
            fetchAllData(); // Refresh the list of rides
        } catch (err) {
            setError(err.message);
        } finally {
            setIsEndingRide(false);
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
                                    <p><strong>Status:</strong> {ride.status}</p> {/* Change: Display ride status */}
                                </div>
                                <div className="ride-stats">
                                    <p><strong>Fare:</strong> ₹{ride.fare.toFixed(2)}</p>
                                    <p><strong>Seats:</strong> {ride.availableSeats} available</p>
                                </div>
                                
                                {/* --- New button to end the ride --- */}
                                {ride.status === 'ACTIVE' && (
                                    <button 
                                        onClick={() => handleEndRide(ride.id)}
                                        disabled={isEndingRide}
                                        className="end-ride-button"
                                    >
                                        {isEndingRide ? 'Ending...' : 'End Ride'}
                                    </button>
                                )}

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