import { useState, useRef, useEffect, type FC } from "react";
import { computeSubmissionTimes } from "../utils/timestampsUtils";
import { Clock, MapPin, Trash2 } from "lucide-react";
import TimeSlotModal from "./modal/TimeSlotModal";
import { formatTime } from "../utils/timestampsUtils";
import type { TimeSlot } from "../types/TimeSlot";
import { Button } from "./ui/button";
import { useNavigate } from "react-router";

// Convert TimeSlot string format to timestamp
const timeSlotToTimestamp = (date: string, time: string): number => {
  return new Date(`${date}T${time}`).getTime();
};

interface CalendarEventProps {
  event: TimeSlot;
  top: number;
  height: number;
  onUpdate: (updatedEvent: TimeSlot) => void;
  onDelete: () => void;
  currentWeek: Date[];
  dayIndex: number;
  readOnly?: boolean;
}

const CalendarEvent: FC<CalendarEventProps> = ({
  event,
  top: initialTop,
  height: initialHeight,
  onUpdate,
  onDelete,
  currentWeek,
  dayIndex,
  readOnly = false,
}) => {
  const navigate = useNavigate();
  const [isDragging, setIsDragging] = useState(false);
  const [isResizing, setIsResizing] = useState<"top" | "bottom" | null>(null);
  const [position, setPosition] = useState({
    top: initialTop,
    height: initialHeight,
  });
  const horizontalOffsetRef = useRef(0);
  const startPosRef = useRef({ x: 0, y: 0, top: 0, height: 0 });

  // Update position when props change (after modal update)
  useEffect(() => {
    setPosition({
      top: initialTop,
      height: initialHeight,
    });
  }, [initialTop, initialHeight]);

  const handleMouseDown = (
    e: React.MouseEvent,
    action: "drag" | "resize-top" | "resize-bottom",
  ) => {
    if (readOnly) return;
    e.preventDefault();
    e.stopPropagation();

    startPosRef.current = {
      x: e.clientX,
      y: e.clientY,
      top: position.top,
      height: position.height,
    };

    if (action === "drag") {
      setIsDragging(true);
    } else if (action === "resize-top") {
      setIsResizing("top");
    } else {
      setIsResizing("bottom");
    }
  };

  useEffect(() => {
    if (readOnly || (!isDragging && !isResizing)) return;

    const snapToQuarterHour = (pixels: number) => {
      const quarterHourInPixels = 25; // 15 minutes = 25px (100px per hour)
      return Math.round(pixels / quarterHourInPixels) * quarterHourInPixels;
    };

    const handleMouseMove = (e: MouseEvent) => {
      const deltaY = e.clientY - startPosRef.current.y;
      const deltaX = e.clientX - startPosRef.current.x;

      if (isDragging) {
        const newTop = startPosRef.current.top + deltaY;
        const snappedTop = snapToQuarterHour(Math.max(0, newTop));
        setPosition((prev) => ({ ...prev, top: snappedTop }));
        horizontalOffsetRef.current = deltaX;
      } else if (isResizing === "top") {
        const newTop = startPosRef.current.top + deltaY;
        const newHeight = startPosRef.current.height - deltaY;
        if (newHeight >= 25) {
          // Minimum 15 minutes (25px)
          const snappedTop = snapToQuarterHour(newTop);
          const snappedHeight = snapToQuarterHour(
            startPosRef.current.top + startPosRef.current.height - snappedTop,
          );
          if (snappedHeight >= 25) {
            setPosition({ top: snappedTop, height: snappedHeight });
          }
        }
      } else if (isResizing === "bottom") {
        const newHeight = startPosRef.current.height + deltaY;
        if (newHeight >= 25) {
          // Minimum 15 minutes
          const snappedHeight = snapToQuarterHour(newHeight);
          if (snappedHeight >= 25) {
            setPosition((prev) => ({ ...prev, height: snappedHeight }));
          }
        }
      }
    };

    const handleMouseUp = () => {
      if (isDragging || isResizing) {
        // Calculate new timestamps based on position
        const pixelsPerHour = 100;
        const dayStartMinutes = 7 * 60; // 7h in minutes

        const newStartMinutes =
          (position.top / pixelsPerHour) * 60 + dayStartMinutes;
        const durationMinutes = (position.height / pixelsPerHour) * 60;
        const newEndMinutes = newStartMinutes + durationMinutes;

        // Create new time strings in HH:mm:ss format
        const startHours = Math.floor(newStartMinutes / 60);
        const startMinutes = Math.floor(newStartMinutes % 60);
        const newStartTime = `${String(startHours).padStart(2, "0")}:${String(
          startMinutes,
        ).padStart(2, "0")}:00`;

        const endHours = Math.floor(newEndMinutes / 60);
        const endMinutes = Math.floor(newEndMinutes % 60);
        const newEndTime = `${String(endHours).padStart(2, "0")}:${String(
          endMinutes,
        ).padStart(2, "0")}:00`;

        // Calculate new day based on horizontal offset
        // Approximate column width (adjust based on your actual layout)
        const columnWidth = window.innerWidth / 6; // 6 columns (1 for hours + 5 days)
        const dayShift = Math.round(horizontalOffsetRef.current / columnWidth);
        const newDayIndex = Math.max(
          0,
          Math.min(currentWeek.length - 1, dayIndex + dayShift),
        );

        const newDate = currentWeek[newDayIndex].toISOString().split("T")[0];
        const { newSubmissionStartTime, newSubmissionEndTime } =
          computeSubmissionTimes({
            endTime: newEndTime,
            originalEndTime: event.endTime,
            submissionStartTime: event.submissionStartTime,
            submissionEndTime: event.submissionEndTime,
            currentTimeSlot: event,
          });
        const newEvent: TimeSlot = {
          ...event,
          date: newDate,
          startTime: newStartTime,
          endTime: newEndTime,
          submissionStartTime: newSubmissionStartTime,
          submissionEndTime: newSubmissionEndTime,
        };
        onUpdate(newEvent);
      }

      setIsDragging(false);
      setIsResizing(null);
      horizontalOffsetRef.current = 0;
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging, isResizing, position.top, position.height]);

  const handleReadOnlyClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (readOnly) {
      navigate(`/student/work-submission/${event.timeSlotId}`);
    }
  };

  return (
    <>
      <div
        className={`absolute left-1 right-1 bg-cyan-100 border-l-4 border-cyan-500 rounded p-3 flex flex-col justify-between hover:bg-cyan-200 transition-colors ${
          readOnly
            ? "cursor-pointer"
            : isDragging || isResizing
              ? "cursor-grabbing shadow-lg z-50"
              : "cursor-grab"
        }`}
        style={{
          top: `${position.top}px`,
          height: `${position.height}px`,
          transform: isDragging
            ? `translateX(${horizontalOffsetRef.current}px)`
            : undefined,
          zIndex: isDragging || isResizing ? 50 : 10,
        }}
        onMouseDown={readOnly ? undefined : (e) => handleMouseDown(e, "drag")}
        onClick={handleReadOnlyClick}
      >
        {/* Resize top handler */}
        {!readOnly && (
          <div
            className="absolute top-0 left-0 right-0 h-2 cursor-ns-resize hover:bg-cyan-300 rounded-t"
            onMouseDown={(e) => handleMouseDown(e, "resize-top")}
            onClick={(e) => e.stopPropagation()}
          />
        )}

        {/* Event content */}
        <div className="flex h-full flex-col justify-between">
          <div className="flex flex-row items-start justify-between">
            <div className="flex-1 min-w-0">
              <div className="font-bold text-cyan-900 text-sm">TPP</div>
              <div className="flex items-center gap-1 text-cyan-700 text-xs mt-1">
                <Clock size={12} />
                <span>
                  {formatTime(timeSlotToTimestamp(event.date, event.startTime))}{" "}
                  - {formatTime(timeSlotToTimestamp(event.date, event.endTime))}
                </span>
              </div>
              <div>
                <MapPin size={12} className="inline-block mr-1 text-cyan-700" />
                <span className="text-cyan-700 text-xs">
                  {event.room || ""}
                </span>
              </div>
            </div>
            {!readOnly && (
              <div
                onMouseDown={(e) => e.stopPropagation()}
                onClick={(e) => e.stopPropagation()}
              >
                <TimeSlotModal currentTimeSlot={event} onUpdate={onUpdate} />
              </div>
            )}
          </div>

          {!readOnly && (
            <Button
              variant="ghost"
              size="icon"
              className="p-0 m-0"
              onClick={(e) => {
                e.stopPropagation();
                onDelete();
              }}
            >
              <Trash2
                size={16}
                className="text-red-500 hover:cursor-pointer shrink-0"
              />
            </Button>
          )}
        </div>

        {/* Resize bottom handler */}
        {!readOnly && (
          <div
            className="absolute bottom-0 left-0 right-0 h-2 cursor-ns-resize hover:bg-cyan-300 rounded-b"
            onMouseDown={(e) => handleMouseDown(e, "resize-bottom")}
            onClick={(e) => e.stopPropagation()}
          />
        )}
      </div>
    </>
  );
};

export default CalendarEvent;
