import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './BookRidePage.css';

const BookRidePage = () => {
    const navigate = useNavigate();

    const [source, setSource] = useState('');
    const [destination, setDestination] = useState('');
    const [sourceSuggestions, setSourceSuggestions] = useState([]);
    const [destinationSuggestions, setDestinationSuggestions] = useState([]);

    const [sourceCoords, setSourceCoords] = useState(null);
    const [destinationCoords, setDestinationCoords] = useState(null);

    const [isSourceSelected, setIsSourceSelected] = useState(false);
    const [isDestinationSelected, setIsDestinationSelected] = useState(false);

    const [foundRides, setFoundRides] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [searched, setSearched] = useState(false);

    // --- New states for booking ---
    const [isBooking, setIsBooking] = useState(false);
    const [bookingError, setBookingError] = useState('');
    const [bookingSuccess, setBookingSuccess] = useState('');

    // --- Current User ID from localStorage ---
    const currentUserId = parseInt(localStorage.getItem("userId"));
    console.log("Current User ID (from localStorage):", currentUserId, "Type:", typeof currentUserId);

    useEffect(() => {
        const handler = setTimeout(() => {
            if (source.length > 2 && !isSourceSelected) {
                fetchSuggestions(source, setSourceSuggestions);
            }
            if (destination.length > 2 && !isDestinationSelected) {
                fetchSuggestions(destination, setDestinationSuggestions);
            }
        }, 500);

        return () => clearTimeout(handler);
    }, [source, destination, isSourceSelected, isDestinationSelected]);

    const fetchSuggestions = async (query, setSuggestions) => {
        if (!query) return;
        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}`);
            const data = await response.json();
            setSuggestions(data);
        } catch (error) {
            console.error("Failed to fetch address suggestions:", error);
        }
    };

    const handleSourceChange = (e) => {
        setSource(e.target.value);
        setIsSourceSelected(false);
    };

    const handleDestinationChange = (e) => {
        setDestination(e.target.value);
        setIsDestinationSelected(false);
    };

    const handleSelectSuggestion = (suggestion, setField, setSuggestions, setCoords, setIsSelected) => {
        setField(suggestion.display_name);
        setCoords({ lat: parseFloat(suggestion.lat), lon: parseFloat(suggestion.lon) });
        setSuggestions([]);
        setIsSelected(true);
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!isSourceSelected || !isDestinationSelected) {
            setError("Please select a source and destination from the suggestions list.");
            return;
        }
        setIsLoading(true);
        setError('');
        setSearched(true);
        setFoundRides([]);
        try {
            const response = await fetch(
                `http://localhost:8080/api/rides/search?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}`
            );
            if (!response.ok) throw new Error('Failed to fetch rides.');
            const data = await response.json();

            console.log("Fetched rides from backend:", data);
            data.forEach((ride) => {
                console.log("Ride ID:", ride.id);
                console.log("Driver ID:", ride.driver.id, "Type:", typeof ride.driver.id);
                console.log("Driver Name:", ride.driver.name);
                console.log("Compare Driver vs Current User:", ride.driver.id === currentUserId);
            });

            setFoundRides(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    // --- New function to handle the booking request ---
    const handleBookRide = async (ride) => {
        setIsBooking(true);
        setBookingError('');
        setBookingSuccess('');

        try {
            const payload = {
                rideId: ride.id,
                passengerId: currentUserId,
                fare: ride.fare,
                seatsBooked: 1
            };

            console.log("Sending booking request to backend:", payload);

            const response = await fetch('http://localhost:8080/api/v1/bookings/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            console.log("Backend response received:", response);

            if (!response.ok) {
                console.error("Booking failed with status:", response.status);
                const errorData = await response.json().catch(() => ({ message: 'No error message available' }));
                setBookingError(errorData.message || 'Failed to book ride. Please try again.');
                return;
            }

            // The response from the backend is a savedBooking object, but we don't need it.
            // We only need to know that the request was successful.

            setBookingSuccess('Ride request sent! Awaiting driver confirmation.');
            console.log("Booking successful! Redirecting to my-booked-rides page.");

            // This is the code that will now run after a successful booking
            navigate('/my-booked-rides');

        } catch (err) {
            console.error("Error booking ride:", err);
            setBookingError('An unexpected error occurred. Please try again later.');
        } finally {
            setIsBooking(false);
        }
    };
    // --- handleSelectRide function is no longer needed ---

    return (
        <div className="book-ride-container">
            <div className="form-box">
                <h2>Find Your Ride</h2>

                {/* --- New Button --- */}
                <div className="page-actions">
                    <Link to="/my-booked-rides" className="view-history-btn">View My Booked Rides</Link>
                </div>

                <p className="subtitle">Enter your source and destination to find available rides.</p>

                <form onSubmit={handleSearch}>
                    <div className="input-group">
                        <label>From</label>
                        <input
                            type="text"
                            value={source}
                            onChange={handleSourceChange}
                            placeholder="Type starting point..."
                            required
                        />
                        {sourceSuggestions.length > 0 && (
                            <ul className="suggestions-list">
                                {sourceSuggestions.map(s => (
                                    <li
                                        key={s.place_id}
                                        onClick={() => handleSelectSuggestion(
                                            s,
                                            setSource,
                                            setSourceSuggestions,
                                            setSourceCoords,
                                            setIsSourceSelected
                                        )}
                                    >
                                        {s.display_name}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>

                    <div className="input-group">
                        <label>To</label>
                        <input
                            type="text"
                            value={destination}
                            onChange={handleDestinationChange}
                            placeholder="Type destination..."
                            required
                        />
                        {destinationSuggestions.length > 0 && (
                            <ul className="suggestions-list">
                                {destinationSuggestions.map(s => (
                                    <li
                                        key={s.place_id}
                                        onClick={() => handleSelectSuggestion(
                                            s,
                                            setDestination,
                                            setDestinationSuggestions,
                                            setDestinationCoords,
                                            setIsDestinationSelected
                                        )}
                                    >
                                        {s.display_name}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>

                    <button type="submit" disabled={isLoading} className="action-button">
                        {isLoading ? 'Searching...' : 'Search Rides'}
                    </button>
                </form>

                {error && <p className="error-message">{error}</p>}
                {bookingError && <p className="error-message">{bookingError}</p>}
                {bookingSuccess && <p className="success-message">{bookingSuccess}</p>}
            </div>

            <div className="search-results">
                <h3>Available Rides</h3>
                {isLoading && <p>Loading...</p>}
                {searched && !isLoading && foundRides.length === 0 && (
                    <p>No rides found for this route. Try another search!</p>
                )}
                {foundRides.length > 0 && (
                    <ul className="ride-list">
                        {foundRides.map(ride => (
                            <li key={ride.id} className="ride-item">
                                <div className="ride-details">
                                    <p><strong>From:</strong> {ride.source}</p>
                                    <p><strong>To:</strong> {ride.destination}</p>
                                    <p><strong>Time:</strong> {new Date(ride.dateTime).toLocaleString()}</p>
                                    <p><strong>Driver:</strong> {ride.driver.name}</p>
                                    <p><strong>Seats Left:</strong> {ride.availableSeats}</p>
                                </div>
                                <div className="ride-action">
                                    <p className="fare">₹{ride.fare.toFixed(2)}</p>
                                    {ride.driver.id === currentUserId ? (
                                        <p className="your-ride-label">✅ This is your ride</p>
                                    ) : (
                                        <button
                                            onClick={() => handleBookRide(ride)} // --- Change here ---
                                            className="book-now-button"
                                            disabled={isBooking}
                                        >
                                            {isBooking ? 'Booking...' : 'Book Now'}
                                        </button>
                                    )}
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default BookRidePage;