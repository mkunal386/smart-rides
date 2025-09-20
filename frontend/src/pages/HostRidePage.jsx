import React, { useState, useEffect } from 'react';
// CHANGED: Imported Link
import { useNavigate, Link } from 'react-router-dom';
import './HostRidePage.css';

const HostRidePage = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        source: '',
        destination: '',
        dateTime: '',
        availableSeats: '',
        carModel: '',
        plateNumber: '',
        phoneNumber: '',
        carColor: '',
    });
    
    const [fare, setFare] = useState('');
    const [sourceCoords, setSourceCoords] = useState(null);
    const [destinationCoords, setDestinationCoords] = useState(null);
    const [sourceSuggestions, setSourceSuggestions] = useState([]);
    const [destinationSuggestions, setDestinationSuggestions] = useState([]);
    
    const [isSourceSelected, setIsSourceSelected] = useState(false);
    const [isDestinationSelected, setIsDestinationSelected] = useState(false);

    const [isLoadingFare, setIsLoadingFare] = useState(false);
    const [error, setError] = useState('');

    // ... (All your existing functions like useEffect, fetchSuggestions, etc. remain unchanged)
    
    useEffect(() => {
        const handler = setTimeout(() => {
            if (formData.source.length > 2 && !isSourceSelected) {
                fetchSuggestions(formData.source, setSourceSuggestions);
            }
            if (formData.destination.length > 2 && !isDestinationSelected) {
                fetchSuggestions(formData.destination, setDestinationSuggestions);
            }
        }, 500);
        return () => clearTimeout(handler);
    }, [formData.source, formData.destination, isSourceSelected, isDestinationSelected]);

    const fetchSuggestions = async (query, setSuggestions) => {
        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}`);
            const data = await response.json();
            setSuggestions(data);
        } catch (err) {
            console.error("Failed to fetch address suggestions:", err);
        }
    };
    
    const handleSelectSuggestion = (suggestion, fieldName, setSuggestions, setCoords, setIsSelectedFlag) => {
        setFormData({ ...formData, [fieldName]: suggestion.display_name });
        setCoords({ lat: parseFloat(suggestion.lat), lon: parseFloat(suggestion.lon) });
        setSuggestions([]);
        setIsSelectedFlag(true);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        if (name === 'source') setIsSourceSelected(false);
        if (name === 'destination') setIsDestinationSelected(false);
    };

    const handleFareCalculation = async () => {
        if (!sourceCoords || !destinationCoords) {
            setError('Please select a valid source and destination from the suggestions.');
            return;
        }
        setError('');
        setIsLoadingFare(true);
        try {
            const response = await fetch('http://localhost:8080/api/v1/fare/calculate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    startLat: sourceCoords.lat,
                    startLon: sourceCoords.lon,
                    endLat: destinationCoords.lat,
                    endLon: destinationCoords.lon,
                }),
            });
            if (!response.ok) throw new Error('Fare calculation failed');
            const data = await response.json();
            setFare(data.fare.toFixed(2));
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoadingFare(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.id) {
            alert("You must be logged in to host a ride.");
            navigate('/login');
            return;
        }
        if (!fare) {
            alert("Please calculate or enter the fare before posting.");
            return;
        }

        const rideData = { ...formData, fare: parseFloat(fare), driverId: user.id };
        
        try {
            const response = await fetch('http://localhost:8080/api/rides/host', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(rideData),
            });
            if (response.ok) {
                alert('Ride hosted successfully!');
                navigate('/dashboard');
            } else {
                const errorText = await response.text();
                alert(`Failed to host ride: ${errorText}`);
            }
        } catch (err) {
            console.error('Error hosting ride:', err);
            alert('An error occurred while hosting the ride.');
        }
    };


    return (
        <div className="host-ride-page">
            <div className="host-ride-container">
                <h2>Host a New Ride</h2>
                
                {/* --- NEW BUTTON ADDED HERE --- */}
                <div className="page-actions">
                    <Link to="/my-hosted-rides" className="view-history-btn">View My Hosted Rides</Link>
                </div>
                {/* --- END OF NEW BUTTON --- */}

                <p className="subtitle">Fill out the details below to offer a ride to passengers.</p>
                <form onSubmit={handleSubmit} className="host-ride-form">
                    {/* The rest of your form JSX remains unchanged */}
                    <fieldset>
                        <legend>Route Details</legend>
                        <div className="input-group">
                            <label>From</label>
                            <input name="source" value={formData.source} onChange={handleInputChange} placeholder="Start location" required />
                             {sourceSuggestions.length > 0 && (
                                <ul className="suggestions-list">
                                    {sourceSuggestions.map(s => <li key={s.place_id} onClick={() => handleSelectSuggestion(s, 'source', setSourceSuggestions, setSourceCoords, setIsSourceSelected)}>{s.display_name}</li>)}
                                </ul>
                            )}
                        </div>
                        <div className="input-group">
                            <label>To</label>
                            <input name="destination" value={formData.destination} onChange={handleInputChange} placeholder="Destination" required />
                            {destinationSuggestions.length > 0 && (
                                <ul className="suggestions-list">
                                    {destinationSuggestions.map(s => <li key={s.place_id} onClick={() => handleSelectSuggestion(s, 'destination', setDestinationSuggestions, setDestinationCoords, setIsDestinationSelected)}>{s.display_name}</li>)}
                                </ul>
                            )}
                        </div>
                        <div className="input-group">
                            <label>Date and Time</label>
                            <input name="dateTime" type="datetime-local" value={formData.dateTime} onChange={handleInputChange} required />
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>Ride Details</legend>
                         <div className="input-group">
                            <label>Available Seats</label>
                            <input name="availableSeats" type="number" min="1" value={formData.availableSeats} onChange={handleInputChange} placeholder="e.g., 3" required />
                        </div>
                        <div className="fare-section">
                            <label>Fare (per seat)</label>
                            <div className="fare-input-wrapper">
                                <input name="fare" type="number" step="0.01" value={fare} onChange={(e) => setFare(e.target.value)} placeholder="₹" required />
                                <button type="button" onClick={handleFareCalculation} disabled={isLoadingFare}>
                                    {isLoadingFare ? '...' : 'Estimate Fare'}
                                </button>
                            </div>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>Car Details</legend>
                        <div className="input-group">
                            <label>Car Model</label>
                            <input name="carModel" value={formData.carModel} onChange={handleInputChange} placeholder="e.g., Toyota Camry" required />
                        </div>
                        <div className="input-group">
                            <label>Plate Number</label>
                            <input name="plateNumber" value={formData.plateNumber} onChange={handleInputChange} placeholder="e.g., TS09AB1234" required />
                        </div>
                         <div className="input-group">
                            <label>Your Phone Number</label>
                            <input name="phoneNumber" type="tel" value={formData.phoneNumber} onChange={handleInputChange} required />
                        </div>
                        <div className="input-group">
                            <label>Car Color (Optional)</label>
                            <input name="carColor" value={formData.carColor} onChange={handleInputChange} />
                        </div>
                    </fieldset>
                    
                    {error && <p className="error-message">{error}</p>}
                    
                    <button type="submit" className="post-ride-btn">Post Your Ride</button>
                </form>
            </div>
        </div>
    );
};

export default HostRidePage;