import React, { useState, useEffect } from "react";
import "./JoinRidePage.css";

const JoinRidePage = () => {
  const [pickup, setPickup] = useState("");
  const [drop, setDrop] = useState("");
  const [time, setTime] = useState("");
  const [rides, setRides] = useState([]); // Holds fetched rides

  // Convert userId from localStorage to number
  const currentUserId = parseInt(localStorage.getItem("userId"));

  const handleSearch = () => {
    if (pickup && drop && time) {
      alert(`Searching rides from ${pickup} to ${drop} at ${time}`);
    } else if (pickup && drop) {
      alert(`Searching rides NOW from ${pickup} to ${drop}`);
    } else {
      alert("Please enter Pickup and Drop locations");
    }

    // TODO: Fetch rides from backend here
    // Example: setRides(fetchedRides);
  };

  return (
    <div className="page-container">
      <h2 className="page-title">Join a Ride</h2>
      <div className="form-container">
        <input
          type="text"
          placeholder="Pickup Location"
          value={pickup}
          onChange={(e) => setPickup(e.target.value)}
          className="form-input"
        />
        <input
          type="text"
          placeholder="Drop Location"
          value={drop}
          onChange={(e) => setDrop(e.target.value)}
          className="form-input"
        />
        <input
          type="time"
          value={time}
          onChange={(e) => setTime(e.target.value)}
          className="form-input"
        />
        <p className="hint-text">
          ⏰ Leave time empty to search rides available <b>right now</b>.
        </p>
        <button onClick={handleSearch} className="form-button">
          Search Rides
        </button>
      </div>

      {/* Display rides */}
      <div className="rides-container">
        {rides.map((ride) => {
          // 🖨️ Debugging logs
          console.log("Full ride object:", ride);
          console.log("Driver ID:", ride.driver.id, "Type:", typeof ride.driver.id);
          console.log("Current User ID:", currentUserId, "Type:", typeof currentUserId);

          return (
            <div key={ride.id} className="ride-card">
              <p><b>From:</b> {ride.source}</p>
              <p><b>To:</b> {ride.destination}</p>
              <p><b>Time:</b> {ride.dateTime}</p>
              <p><b>Fare:</b> ${ride.fare}</p>
              <p><b>Seats Available:</b> {ride.availableSeats}</p>

              {/* Conditional rendering for Book button */}
              {Number(ride.driver.id) === Number(currentUserId) ? (
                <p className="your-ride-label">Your Ride</p>
              ) : (
                <button className="book-button">Book</button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default JoinRidePage;
