import React from 'react';
import dayjs from 'dayjs';

export default function NextBestActionCard({ action, onStatusUpdate }) {
  const isPending = action.status === 'PROPOSED';
  const isAccepted = action.status === 'ACCEPTED';

  return (
    <div className={`p-4 rounded-md border flex flex-col gap-2 ${
      isPending ? 'bg-indigo-50 border-indigo-200' : 'bg-gray-50 border-gray-200'
    }`}>
      <div className="flex justify-between items-start">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-indigo-900">✨ AI Suggestion</span>
          <span className={`badge ${
            isPending ? 'bg-indigo-200 text-indigo-900' : 
            isAccepted ? 'bg-green-200 text-green-900' : 
            'bg-gray-200 text-gray-800'
          }`}>
            {action.status}
          </span>
        </div>
        <div className="text-xs text-gray-500">
          Generated: {dayjs(action.createdAt).format('MMM D, HH:mm')}
        </div>
      </div>

      <div className="mt-2 text-lg font-medium text-gray-900">
        {action.recommendation}
      </div>
      
      {action.reason && (
        <div className="text-sm text-gray-700 bg-white p-2 rounded border border-indigo-100">
          <strong>Reasoning:</strong> {action.reason}
        </div>
      )}

      {isPending && (
        <div className="flex gap-2 mt-2">
          <button onClick={() => onStatusUpdate(action.id, 'ACCEPTED')} className="btn btn-primary text-xs bg-indigo-600 border-indigo-600">Accept Suggestion</button>
          <button onClick={() => onStatusUpdate(action.id, 'REJECTED')} className="btn btn-secondary text-xs">Reject</button>
        </div>
      )}

      {isAccepted && (
        <div className="flex gap-2 mt-2">
          <button onClick={() => onStatusUpdate(action.id, 'DONE')} className="btn btn-primary text-xs bg-green-600 border-green-600 text-white">Mark as Done</button>
        </div>
      )}
    </div>
  );
}
