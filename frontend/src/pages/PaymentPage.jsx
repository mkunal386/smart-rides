import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './PaymentPage.css'; // Make sure this CSS file is also present and correct

const PaymentPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { ride, fare } = location.state || {}; // Get ride and fare from navigation state

  // State for the fake card details
  const [cardDetails, setCardDetails] = useState({
    name: '',
    cardNumber: '',
    expiry: '',
    cvv: '',
  });
  const [isProcessing, setIsProcessing] = useState(false);

  if (!ride || fare === undefined) {
    return (
      <div className="payment-container">
        <div className="payment-box">
          <h2>Error</h2>
          <p>No ride details found. Please go back and select a ride first.</p>
          <button onClick={() => navigate('/book-ride')} className="payment-button">Go Back</button>
        </div>
      </div>
    );
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCardDetails(prev => ({ ...prev, [name]: value }));
  };

  const handlePayNow = async () => {
    // Basic validation
    if (!cardDetails.name || !cardDetails.cardNumber || !cardDetails.expiry || !cardDetails.cvv) {
        alert('Please fill in all card details.');
        return;
    }

    setIsProcessing(true);

    const user = JSON.parse(localStorage.getItem('user'));
    if (!user || !user.id) {
        alert('You must be logged in to book a ride.');
        navigate('/login');
        return;
    }

    const bookingPayload = {
        passengerId: user.id,
        rideId: ride.id,
        fare: fare,
    };

    try {
        const response = await fetch('http://localhost:8080/api/v1/bookings/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookingPayload),
        });

        if (response.ok) {
            alert('Booking successful! Your ride is confirmed.');
            navigate('/history'); // Redirect to history page on success
        } else {
            const errorText = await response.text();
            alert(`Booking failed: ${errorText}`);
        }
    } catch (error) {
        console.error('Failed to create booking:', error);
        alert('An error occurred while trying to book the ride.');
    } finally {
        setIsProcessing(false);
    }
  };

  return (
    <div className="payment-container">
      <div className="payment-box">
        <h2>Confirm Your Booking</h2>
        <div className="ride-summary">
          <p><strong>From:</strong> {ride.source}</p>
          <p><strong>To:</strong> {ride.destination}</p>
          <p><strong>Driver:</strong> {ride.driver.name}</p>
          <p className="fare-display">Total Fare: ₹{fare.toFixed(2)}</p>
        </div>

        <div className="payment-form">
          <h3>Enter Payment Details</h3>
          <input
            type="text"
            name="name"
            placeholder="Cardholder Name"
            className="payment-input"
            value={cardDetails.name}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="cardNumber"
            placeholder="Card Number"
            className="payment-input"
            value={cardDetails.cardNumber}
            onChange={handleInputChange}
          />
          <div className="card-details-row">
            <input
              type="text"
              name="expiry"
              placeholder="MM/YY"
              className="payment-input"
              value={cardDetails.expiry}
              onChange={handleInputChange}
            />
            <input
              type="text"
              name="cvv"
              placeholder="CVV"
              className="payment-input"
              value={cardDetails.cvv}
              onChange={handleInputChange}
            />
          </div>
        </div>

        <button onClick={handlePayNow} disabled={isProcessing} className="payment-button">
          {isProcessing ? 'Processing...' : `Pay ₹${fare.toFixed(2)}`}
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;

