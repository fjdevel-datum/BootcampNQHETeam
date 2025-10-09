import React from "react";

interface BillCardProps {
  title: string;
  date: string;
  amount: number;
  onClick?: () => void;
}

const ExpenseCard: React.FC<BillCardProps> = ({ title, date, amount, onClick }) => {
  return (
  
     <button
      onClick={onClick}
      className="w-full text-left bg-activity text-white rounded-lg p-4 shadow-md hover:brightness-110 transition"
    >
      <p className="font-bold">{title}</p>
      <span className="text-sm">{date}</span>
    <p className="text-button font-bold">${amount.toFixed(2)}</p>

    </button>
  );
};

export default ExpenseCard;
