import { ChevronLeft, ChevronRight } from "lucide-react";
import type { FC } from "react";

interface CalendarNavigationProps {
  referenceDate: Date;
  setReferenceDate: (date: Date) => void;
}

export const CalendarNavigation: FC<CalendarNavigationProps> = ({
  referenceDate,
  setReferenceDate,
}) => {
  const goToPreviousWeek = () => {
    const newDate = new Date(referenceDate);
    newDate.setDate(newDate.getDate() - 7);
    setReferenceDate(newDate);
  };

  const goToNextWeek = () => {
    const newDate = new Date(referenceDate);
    newDate.setDate(newDate.getDate() + 7);
    setReferenceDate(newDate);
  };

  const goToCurrentWeek = () => {
    const today = new Date();
    setReferenceDate(today);
  };

  return (
    <div className="flex items-center gap-2">
      <button
        onClick={goToPreviousWeek}
        className="bg-gray-100 p-2 hover:bg-gray-200 rounded-lg transition-colors"
        aria-label="Semaine précédente"
      >
        <ChevronLeft className="w-5 h-5" />
      </button>
      <button
        onClick={goToCurrentWeek}
        className="bg-gray-100 px-4 py-2 hover:bg-gray-200 rounded-lg transition-colors font-medium text-sm"
      >
        Aujourd'hui
      </button>
      <button
        onClick={goToNextWeek}
        className="bg-gray-100 p-2 hover:bg-gray-200 rounded-lg transition-colors"
        aria-label="Semaine suivante"
      >
        <ChevronRight className="w-5 h-5" />
      </button>
    </div>
  );
};
