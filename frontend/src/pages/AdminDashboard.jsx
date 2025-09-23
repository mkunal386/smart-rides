import React, { useState, useEffect } from "react";
import axios from "axios";
import StatCard from "../components/StatCard";

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState("overview");
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);
  const [users, setUsers] = useState([]);
  const [rides, setRides] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [cancellations, setCancellations] = useState([]);
  const [disputes, setDisputes] = useState([]);

  const API_BASE_URL = "http://localhost:8080/api";

  useEffect(() => {
    const fetchDashboardStats = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get(`${API_BASE_URL}/admin/overview/stats`);
        setStats(response.data);
      } catch (err) {
        setError("Failed to fetch data. Please check if the backend is running.");
        console.error("API Call failed:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardStats();
  }, []);

  useEffect(() => {
    const fetchTabData = async () => {
      try {
        if (activeTab === "users") {
          const res = await axios.get(`${API_BASE_URL}/admin/users`);
          setUsers(res.data);
        } else if (activeTab === "rides") {
          const res = await axios.get(`${API_BASE_URL}/admin/rides`);
          setRides(res.data);
        } else if (activeTab === "bookings") {
          const res = await axios.get(`${API_BASE_URL}/admin/bookings`);
          setBookings(res.data);
        } else if (activeTab === "cancellations") {
          const res = await axios.get(`${API_BASE_URL.replace('/api','')}/api/cancellations`);
          setCancellations(res.data);
        } else if (activeTab === "disputes") {
          const res = await axios.get(`${API_BASE_URL.replace('/api','')}/api/disputes`);
          setDisputes(res.data);
        }
      } catch (err) {
        console.error("Failed to fetch tab data:", err);
      }
    };
    fetchTabData();
  }, [activeTab]);

  const handleVerify = async (id) => {
    try {
      await axios.patch(`${API_BASE_URL}/admin/users/${id}/verify`);
      setUsers((prev) => prev.map((u) => (u.id === id ? { ...u, isVerified: true, status: "VERIFIED" } : u)));
    } catch (e) {
      console.error("Verify failed", e);
    }
  };

  const handleBlockToggle = async (id, block) => {
    try {
      await axios.patch(`${API_BASE_URL}/admin/users/${id}/${block ? 'block' : 'unblock'}`);
      setUsers((prev) => prev.map((u) => (u.id === id ? { ...u, isBlocked: block, status: block ? 'BLOCKED' : (u.isVerified ? 'VERIFIED' : 'ACTIVE') } : u)));
    } catch (e) {
      console.error("Block/unblock failed", e);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">
      <div className="w-64 bg-white shadow-md p-4">
        <h2 className="text-xl font-bold text-indigo-600 mb-6">Admin Panel</h2>
        <nav className="flex flex-col space-y-2">
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "overview"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("overview")}
          >
            Overview
          </button>
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "users"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("users")}
          >
            User Management
          </button>
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "rides"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("rides")}
          >
            Rides Tab
          </button>
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "bookings"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("bookings")}
          >
            Bookings Tab
          </button>
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "cancellations"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("cancellations")}
          >
            Cancellations
          </button>
          <button
            className={`text-left px-4 py-2 rounded-md transition-colors ${
              activeTab === "disputes"
                ? "bg-indigo-600 text-white"
                : "text-gray-700 hover:bg-gray-200"
            }`}
            onClick={() => setActiveTab("disputes")}
          >
            Disputes
          </button>
        </nav>
      </div>
      <main className="flex-1 p-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">
          {activeTab === "overview" && "Dashboard Overview"}
          {activeTab === "users" && "User Management"}
          {activeTab === "rides" && "Rides Monitor"}
          {activeTab === "bookings" && "Bookings Monitor"}
          {activeTab === "cancellations" && "Ride Cancellations"}
          {activeTab === "disputes" && "Dispute Management"}
        </h1>
        {error && <div className="p-4 bg-red-100 text-red-700 rounded-lg">{error}</div>}
        {loading && !error ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-indigo-500"></div>
          </div>
        ) : (
          <div className="bg-white p-6 rounded-lg shadow-md">
            {activeTab === "overview" && stats && (
              <div className="flex flex-wrap justify-center gap-6">
                <StatCard
                  title="Total Users"
                  value={stats.totalUsers}
                  icon="👥"
                  color="#4F46E5"
                />
                <StatCard
                  title="Total Drivers"
                  value={stats.totalDrivers}
                  icon="🚗"
                  color="#10B981"
                />
                <StatCard
                  title="Total Passengers"
                  value={stats.totalPassengers}
                  icon="🚶"
                  color="#6366F1"
                />
                <StatCard
                  title="Total Rides"
                  value={stats.totalRides}
                  icon="🗺️"
                  color="#F97316"
                />
                <StatCard
                  title="Active Rides"
                  value={stats.activeRides}
                  icon="🟢"
                  color="#3B82F6"
                />
                <StatCard
                  title="Completed Rides"
                  value={stats.completedRides}
                  icon="✅"
                  color="#22C55E"
                />
                <StatCard
                  title="Cancelled Rides"
                  value={stats.cancelledRides}
                  icon="❌"
                  color="#EF4444"
                />
                <StatCard
                  title="Total Bookings"
                  value={stats.totalBookings}
                  icon="📋"
                  color="#A855F7"
                />
                <StatCard
                  title="Pending Bookings"
                  value={stats.pendingBookings}
                  icon="⏳"
                  color="#FBBF24"
                />
                <StatCard
                  title="Approved Bookings"
                  value={stats.approvedBookings}
                  icon="👍"
                  color="#34D399"
                />
                <StatCard
                  title="Rejected Bookings"
                  value={stats.rejectedBookings}
                  icon="👎"
                  color="#F43F5E"
                />
                <StatCard
                  title="Total Earnings"
                  value={`₹${stats.totalEarnings.toLocaleString()}`}
                  icon="💰"
                  color="#16A34A"
                />
                <StatCard
                  title="Monthly Earnings"
                  value={`₹${stats.monthlyEarnings.toLocaleString()}`}
                  icon="📈"
                  color="#8B5CF6"
                />
                <StatCard
                  title="Daily Earnings"
                  value={`₹${stats.dailyEarnings.toLocaleString()}`}
                  icon="☀️"
                  color="#FBBF24"
                />
              </div>
            )}
            {activeTab === "cancellations" && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Booking</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">User</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Reason</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Fee</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {cancellations.map(c => (
                      <tr key={c.id}>
                        <td className="px-4 py-2">{c.id}</td>
                        <td className="px-4 py-2">{c.booking?.id}</td>
                        <td className="px-4 py-2">{c.user?.name}</td>
                        <td className="px-4 py-2">{c.reason}</td>
                        <td className="px-4 py-2">₹{Number(c.feeApplied).toFixed(2)}</td>
                        <td className="px-4 py-2">{c.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {activeTab === "disputes" && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Booking</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Raised By</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {disputes.map(d => (
                      <tr key={d.id}>
                        <td className="px-4 py-2">{d.id}</td>
                        <td className="px-4 py-2">{d.booking?.id}</td>
                        <td className="px-4 py-2">{d.raisedBy?.name}</td>
                        <td className="px-4 py-2">{d.type}</td>
                        <td className="px-4 py-2">{d.description}</td>
                        <td className="px-4 py-2">{d.status}</td>
                        <td className="px-4 py-2 space-x-2">
                          <button onClick={async () => {
                            const note = prompt('Resolution note:');
                            const refund = Number(prompt('Refund amount (optional):') || '');
                            const penalty = Number(prompt('Penalty amount (optional):') || '');
                            try {
                              await axios.patch(`${API_BASE_URL.replace('/api','')}/api/disputes/${d.id}/resolve`, { resolutionNote: note || '', refundAmount: isNaN(refund) ? null : refund, penaltyAmount: isNaN(penalty) ? null : penalty, rejected: false });
                              setDisputes(prev => prev.map(x => x.id === d.id ? { ...x, status: 'RESOLVED' } : x));
                            } catch (e) { console.error(e); }
                          }} className="px-3 py-1 bg-green-600 text-white rounded">Resolve</button>
                          <button onClick={async () => {
                            const note = prompt('Reject reason:');
                            try {
                              await axios.patch(`${API_BASE_URL.replace('/api','')}/api/disputes/${d.id}/resolve`, { resolutionNote: note || '', rejected: true });
                              setDisputes(prev => prev.map(x => x.id === d.id ? { ...x, status: 'REJECTED' } : x));
                            } catch (e) { console.error(e); }
                          }} className="px-3 py-1 bg-red-600 text-white rounded">Reject</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {activeTab === "users" && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">User ID</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Phone</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {users.map(u => (
                      <tr key={u.id}>
                        <td className="px-4 py-2">{u.id}</td>
                        <td className="px-4 py-2">{u.name}</td>
                        <td className="px-4 py-2">{u.email}</td>
                        <td className="px-4 py-2">{u.phone}</td>
                        <td className="px-4 py-2">{u.role}</td>
                        <td className="px-4 py-2">{u.status || (u.isBlocked ? 'BLOCKED' : (u.isVerified ? 'VERIFIED' : 'ACTIVE'))}</td>
                        <td className="px-4 py-2 space-x-2">
                          <button onClick={() => handleVerify(u.id)} className="px-3 py-1 bg-indigo-600 text-white rounded">Verify</button>
                          {u.isBlocked ? (
                            <button onClick={() => handleBlockToggle(u.id, false)} className="px-3 py-1 bg-green-600 text-white rounded">Unblock</button>
                          ) : (
                            <button onClick={() => handleBlockToggle(u.id, true)} className="px-3 py-1 bg-red-600 text-white rounded">Block</button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {activeTab === "rides" && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Ride ID</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Route</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Date & Time</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Driver & Vehicle</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Seats</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Fare</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {rides.map(r => (
                      <tr key={r.id}>
                        <td className="px-4 py-2">{r.id}</td>
                        <td className="px-4 py-2">{r.source} → {r.destination}</td>
                        <td className="px-4 py-2">{new Date(r.dateTime).toLocaleString()}</td>
                        <td className="px-4 py-2">{r.driver?.name} • {r.carModel} {r.plateNumber}</td>
                        <td className="px-4 py-2">{r.availableSeats}</td>
                        <td className="px-4 py-2">₹{Number(r.fare).toFixed(2)}</td>
                        <td className="px-4 py-2">{r.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {activeTab === "bookings" && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Booking ID</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Passenger</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Route</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Ride Date</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Seats</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Booking Status</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Payment Status</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {bookings.map(b => (
                      <tr key={b.id}>
                        <td className="px-4 py-2">{b.id}</td>
                        <td className="px-4 py-2">{b.passenger?.name}</td>
                        <td className="px-4 py-2">{b.ride?.source} → {b.ride?.destination}</td>
                        <td className="px-4 py-2">{b.ride ? new Date(b.ride.dateTime).toLocaleString() : '-'}</td>
                        <td className="px-4 py-2">{b.seatsBooked}</td>
                        <td className="px-4 py-2">{b.status}</td>
                        <td className="px-4 py-2">{b.paymentStatus}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
};

export default AdminDashboard;