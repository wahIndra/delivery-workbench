import React from 'react';

const SlaBadge = ({ status, text }) => {
  if (!status) return null;

  let colorClass = 'badge-gray';
  
  if (status === 'NORMAL') colorClass = 'bg-green-100 text-green-800 border-green-200';
  if (status === 'WARNING') colorClass = 'bg-yellow-100 text-yellow-800 border-yellow-200';
  if (status === 'BREACHED') colorClass = 'bg-red-100 text-red-800 border-red-200';

  return (
    <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full border ${colorClass}`}>
      {text || status}
    </span>
  );
};

export default SlaBadge;
