import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { notificationApi } from '../api';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const res = await notificationApi.getMyNotifications();
      setNotifications(res.data);
    } catch (err) {
      console.error('Failed to fetch notifications', err);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationApi.markAsRead(id);
      // Update local state
      setNotifications(prev => prev.map(n => 
        n.id === id ? { ...n, read: true, readAt: new Date().toISOString() } : n
      ));
    } catch (err) {
      console.error('Failed to mark as read', err);
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div><p className="mt-4">Loading notifications...</p></div>;

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="mb-8">Notifications</h1>

      <div className="card">
        {notifications.length === 0 ? (
          <p className="text-gray-500">You have no notifications.</p>
        ) : (
          <div className="flex flex-col gap-4">
            {notifications.map(notif => (
              <div 
                key={notif.id} 
                className={`p-4 rounded-md border ${notif.read ? 'bg-gray-50 border-gray-200' : 'bg-blue-50 border-blue-200'}`}
              >
                <div className="flex justify-between items-start mb-2">
                  <div className="flex gap-2 items-center">
                    {!notif.read && <span className="w-2 h-2 rounded-full bg-blue-600 inline-block"></span>}
                    <h4 className={`m-0 ${notif.read ? 'text-gray-700' : 'text-blue-900'}`}>
                      {notif.title}
                    </h4>
                  </div>
                  <div className="text-xs text-gray-500">
                    {dayjs(notif.createdAt).format('MMM D, YYYY HH:mm')}
                  </div>
                </div>
                
                <p className={`text-sm mb-3 ${notif.read ? 'text-gray-600' : 'text-gray-800'}`}>
                  {notif.message}
                </p>

                <div className="flex justify-between items-center mt-4">
                  {notif.requestId ? (
                    <Link to={`/requests/${notif.requestId}`} className="text-sm text-indigo-600 hover:underline font-medium">
                      View Request {notif.requestCode}
                    </Link>
                  ) : (
                    <div></div>
                  )}

                  {!notif.read ? (
                    <button 
                      onClick={() => handleMarkAsRead(notif.id)}
                      className="text-xs text-blue-600 hover:text-blue-800 underline bg-transparent border-none cursor-pointer"
                    >
                      Mark as read
                    </button>
                  ) : (
                    <span className="text-xs text-gray-400">Read on {dayjs(notif.readAt).format('MMM D')}</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
