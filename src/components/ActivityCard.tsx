import React from "react";

interface ActivityCardProps {
  title: string;
  date: string;
  onClick?: () => void;
}

const ActivityCard: React.FC<ActivityCardProps> = ({ title, date, onClick }) => {
  return (
    <button
      onClick={onClick}
      className="w-full text-left bg-activity text-white rounded-lg p-4 shadow-md hover:brightness-110 transition"
    >
      <p className="font-bold">{title}</p>
      <span className="text-sm">{date}</span>
    </button>
  );
};

export default ActivityCard;
