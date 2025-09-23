import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './MyBookedRidesPage.css';

const MyBookedRidesPage = () => {
    const [bookedRides, setBookedRides] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // --- New states for payment process ---
    const [isPaying, setIsPaying] = useState(false);
    const [paymentError, setPaymentError] = useState('');
    const [isCancelling, setIsCancelling] = useState(false);
    const [cancelError, setCancelError] = useState('');
    const [isRaisingDispute, setIsRaisingDispute] = useState(false);
    const [disputeError, setDisputeError] = useState('');

    const fetchBookedRides = async () => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.id) {
            alert("Please log in to see your booked rides.");
            navigate('/login');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/v1/bookings/passenger/${user.id}`);
            if (!response.ok) {
                throw new Error('Failed to fetch your booked rides.');
            }
            const data = await response.json();
            setBookedRides(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchBookedRides();
    }, [navigate]);

    // --- New function to handle payment confirmation ---
    const handlePaymentConfirmation = async (booking) => {
        setIsPaying(true);
        setPaymentError('');

        try {
            // This is a dummy transaction ID for our simulated payment flow
            const dummyTransactionId = `txn_${Date.now()}`;
            
            const payload = {
                bookingId: booking.id,
                transactionId: dummyTransactionId
            };

            const response = await fetch('http://localhost:8080/api/v1/bookings/confirm-payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Payment confirmation failed.');
            }

            // On success, refetch the bookings to update the UI
            alert("Payment successful! Your ride is now confirmed.");
            fetchBookedRides();

        } catch (err) {
            setPaymentError(err.message);
        } finally {
            setIsPaying(false);
        }
    };

    const handleCancel = async (booking) => {
        const reason = prompt('Enter cancellation reason (Emergency, Weather, Vehicle Issue, Personal, etc.)');
        if (!reason) return;
        setIsCancelling(true);
        setCancelError('');
        try {
            const user = JSON.parse(localStorage.getItem('user'));
            const payload = { bookingId: booking.id, userId: user.id, reason };
            const res = await fetch('http://localhost:8080/api/cancellations/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                throw new Error(err.message || 'Cancellation failed');
            }
            const data = await res.json();
            alert(`Cancellation submitted. Fee applied: ₹${Number(data.feeApplied).toFixed(2)}`);
            fetchBookedRides();
        } catch (e) {
            setCancelError(e.message);
        } finally {
            setIsCancelling(false);
        }
    };

    const handleRaiseDispute = async (booking) => {
        const type = prompt('Dispute type (Payment Issue, Driver No-Show, Route Change, Service Quality)');
        if (!type) return;
        const description = prompt('Describe the issue:');
        if (!description) return;
        setIsRaisingDispute(true);
        setDisputeError('');
        try {
            const user = JSON.parse(localStorage.getItem('user'));
            const payload = { bookingId: booking.id, userId: user.id, type, description };
            const res = await fetch('http://localhost:8080/api/disputes/open', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                throw new Error(err.message || 'Failed to raise dispute');
            }
            alert('Dispute raised successfully');
        } catch (e) {
            setDisputeError(e.message);
        } finally {
            setIsRaisingDispute(false);
        }
    };

    if (isLoading) return <div className="loading-message">Loading your bookings...</div>;
    if (error) return <div className="error-message">{error}</div>;

    return (
        <div className="history-page-container">
            <h2>My Booked Rides</h2>
            {cancelError && <div className="error-message">{cancelError}</div>}
            {disputeError && <div className="error-message">{disputeError}</div>}
            {bookedRides.length > 0 ? (
                <ul className="ride-history-list">
                    {bookedRides.map(booking => (
                        <li key={booking.id} className="ride-history-item">
                            <div className="ride-info">
                                <p><strong>Route:</strong> {booking.ride.source} to {booking.ride.destination}</p>
                                <p><strong>Date:</strong> {new Date(booking.ride.dateTime).toLocaleString()}</p>
                                <p><strong>Driver:</strong> {booking.ride.driver.name}</p>
                            </div>
                            <div className="ride-stats">
                                {/* --- Conditional rendering based on booking status --- */}
                                {booking.status === 'PENDING' && (
                                    <p><strong>Status:</strong> <span className="status-pending">Awaiting driver's approval...</span></p>
                                )}
                                {booking.status === 'DRIVER_ACCEPTED' && (
                                    <>
                                        <p><strong>Status:</strong> <span className="status-accepted">Accepted!</span></p>
                                        <p><strong>Fare:</strong> ₹{booking.fare.toFixed(2)}</p>
                                        <button
                                            onClick={() => handlePaymentConfirmation(booking)}
                                            disabled={isPaying}
                                            className="pay-now-button"
                                        >
                                            {isPaying ? 'Processing...' : 'Pay Now'}
                                        </button>
                                        <button
                                            onClick={() => handleCancel(booking)}
                                            disabled={isCancelling}
                                            className="cancel-button"
                                        >
                                            {isCancelling ? 'Cancelling...' : 'Cancel Booking'}
                                        </button>
                                    </>
                                )}
                                {booking.status === 'CONFIRMED' && (
                                    <>
                                        <p><strong>Fare Paid:</strong> ₹{booking.fare.toFixed(2)}</p>
                                        <p><strong>Status:</strong> <span className="status-confirmed">Confirmed</span></p>
                                        <button
                                            onClick={() => handleCancel(booking)}
                                            disabled={isCancelling}
                                            className="cancel-button"
                                        >
                                            {isCancelling ? 'Cancelling...' : 'Cancel Booking'}
                                        </button>
                                        <button
                                            onClick={() => handleRaiseDispute(booking)}
                                            disabled={isRaisingDispute}
                                            className="dispute-button"
                                        >
                                            {isRaisingDispute ? 'Submitting...' : 'Raise Dispute'}
                                        </button>
                                    </>
                                )}
                                {booking.status === 'DRIVER_REJECTED' && (
                                    <p><strong>Status:</strong> <span className="status-rejected">Rejected</span></p>
                                )}
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <div className="no-rides-message">
                    <p>You have no upcoming or past bookings.</p>
                    <Link to="/book-ride" className="action-link">Book a New Ride</Link>
                </div>
            )}
        </div>
    );
};

export default MyBookedRidesPage;