import { useEffect, useState, type FC } from "react";
import CalendarEvent from "./CalendarEvent";
import TimeSlotModal from "./modal/TimeSlotModal";
import { timestampToMinutes } from "../utils/timestampsUtils";
import type { TimeSlot } from "../types/TimeSlot";
import {
  createTimeslot,
  updateTimeSlot,
  getTimeSlotsBetweenDates,
  deleteTimeSlot,
  type TimeSlotPayload,
} from "../services/timeSlotService";
import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuSeparator,
  ContextMenuTrigger,
} from "./ui/context-menu";

import { useSelector } from "react-redux";
import { type RootState } from "@/store/store";

interface ScheduleCalendarProps {
  currentWeek: Date[];
  currentDay: Date;
  readOnly?: boolean;
}

const ScheduleCalendar: FC<ScheduleCalendarProps> = ({
  currentWeek,
  currentDay,
  readOnly = false,
}) => {
  const currentCourse = useSelector((state: RootState) => state.course.currentCourse);
  const currentCourseId = currentCourse?.id;
  const [events, setEvents] = useState<TimeSlot[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalEvent, setModalEvent] = useState<TimeSlot | null>(null);

  useEffect(() => {
    const fetchEvents = async () => {
      if (currentWeek.length === 0 || !currentCourseId) return;

      const startDate = currentWeek[0].toISOString().split("T")[0];
      const endDate = currentWeek[currentWeek.length - 1]
        .toISOString()
        .split("T")[0];

      try {
        const timeSlots = await getTimeSlotsBetweenDates(
          startDate,
          endDate,
          currentCourseId,
        );
        setEvents(timeSlots);
        console.log("Fetched TimeSlots:", timeSlots);
      } catch (error) {
        console.error("Erreur lors du chargement des créneaux:", error);
      }
    };

    fetchEvents();
  }, [currentWeek, currentCourseId]);

  const hours = Array.from({ length: 15 }, (_, i) => i + 7); // 7h to 21h

  const isSameDay = (eventDate: string, day: Date) => {
    const dayStr = day.toISOString().split("T")[0];
    return eventDate === dayStr;
  };

  const getEventPosition = (date: string, startTime: string) => {
    const timestamp = new Date(`${date}T${startTime}`).getTime();
    const startMinutes = timestampToMinutes(timestamp);
    const dayStartMinutes = 7 * 60; // 7h in minutes (start time of the calendar)
    return ((startMinutes - dayStartMinutes) / 60) * 100; // position as px (100px per hour )
  };

  const getEventHeight = (date: string, startTime: string, endTime: string) => {
    const startTimestamp = new Date(`${date}T${startTime}`).getTime();
    const endTimestamp = new Date(`${date}T${endTime}`).getTime();
    const duration =
      timestampToMinutes(endTimestamp) - timestampToMinutes(startTimestamp);
    return (duration / 60) * 100; // height as px (100px per hour)
  };

  const getEventsForDay = (day: Date) => {
    return events.filter((e) => isSameDay(e.date, day));
  };

  const handleUpdateEvent = async (event: TimeSlot, newEvent: TimeSlot) => {
    if (!currentCourseId) return;
    try {
      const newTimeSlotData: TimeSlotPayload = {
        courseId: currentCourseId,
        date: newEvent.date,
        startTime: newEvent.startTime,
        endTime: newEvent.endTime,
        submissionStartTime: newEvent.submissionStartTime,
        submissionEndTime: newEvent.submissionEndTime,
        room: newEvent.room,
      };

      console.log("Updating TimeSlot with data:", newTimeSlotData);

      const updatedTimeSlot = await updateTimeSlot(
        event.timeSlotId,
        newTimeSlotData,
      );

      console.log("Updated TimeSlot:", updatedTimeSlot);

      setEvents((prev) =>
        prev.map((e) =>
          e.timeSlotId === event.timeSlotId ? updatedTimeSlot : e,
        ),
      );
      console.log("Event updated successfully");
    } catch (error) {
      console.error("Erreur lors de la mise à jour du créneau:", error);
    }
  };

  const handleDeleteEvent = (eventId: string) => {
    setEvents((prev) => prev.filter((e) => e.timeSlotId !== eventId));
    try {
      deleteTimeSlot(eventId);
    } catch (error) {
      console.error("Erreur lors de la suppression du créneau:", error);
    }
  };

  const handleCellClick = (day: Date, hour: number) => {
    if (!currentCourseId || readOnly) return;
    // Prépare un nouvel événement mais n'envoie rien à l'API
    const startTime = new Date(day);
    startTime.setHours(hour, 0, 0, 0);

    // Default duration: 1.5 hours
    const endTime = new Date(startTime);
    endTime.setHours(startTime.getHours() + 1, 30, 0, 0);

    const submissionStart = new Date(endTime);
    submissionStart.setMinutes(submissionStart.getMinutes() - 15);

    const submissionEnd = new Date(endTime);
    submissionEnd.setMinutes(submissionEnd.getMinutes() + 5);

    const newEvent: TimeSlot = {
      timeSlotId: "",
      courseId: currentCourseId,
      date: day.toISOString().split("T")[0],
      startTime: startTime.toTimeString().slice(0, 8),
      endTime: endTime.toTimeString().slice(0, 8),
      submissionStartTime: submissionStart.toTimeString().slice(0, 8),
      submissionEndTime: submissionEnd.toTimeString().slice(0, 8),
      room: "",
    };
    setModalEvent(newEvent);
    setModalOpen(true);
  };

  useEffect(() => {
    getEventHeight;
    getEventPosition;
  }, [events]);

  const handleCreateEvent = async (newEvent: TimeSlot) => {
    try {
      const createdTimeSlot = await createTimeslot(newEvent);
      setEvents((prev) => [...prev, createdTimeSlot]);
      setModalOpen(false);
      setModalEvent(null);
      console.log("Créneau créé avec succès");
    } catch (error) {
      console.error("Erreur lors de la création du créneau:", error);
    }
  };

  return (
    <div className="bg-white rounded-lg flex flex-col h-full overflow-hidden">
      {/* Header with dates */}
      <div className="grid grid-cols-6 sticky top-0">
        <div />
        {currentWeek.map((day) => (
          <div
            key={day.toISOString()}
            className="flex gap-2 p-4 justify-center items-center border-b border-gray-300"
          >
            <span className="text-gray-600 font-medium">
              {day.toLocaleDateString("fr-FR", { weekday: "short" })}
            </span>
            <span
              className={`font-bold ${
                day.getDate() === currentDay.getDate() &&
                day.getMonth() === currentDay.getMonth() &&
                day.getFullYear() === currentDay.getFullYear()
                  ? "text-white bg-red-500 w-fit p-1 rounded-full"
                  : ""
              }`}
            >
              {day.getDate()}
            </span>
          </div>
        ))}
      </div>

      {/* Modal de création de créneau */}
      {!readOnly && modalEvent && (
        <TimeSlotModal
          currentTimeSlot={modalEvent}
          onUpdate={handleCreateEvent}
          open={modalOpen}
          setOpen={setModalOpen}
        />
      )}

      {/* Dates content */}
      <div className="flex-1 overflow-y-auto pb-8">
        {hours.map((hour) => (
          <div key={`${hour}`} className="grid grid-cols-6 ">
            <div className="flex h-25 px-4 justify-end items-start">
              <span className="text-gray-500 font-medium">{hour}:00</span>
            </div>
            {currentWeek.map((day, dayIndex) => (
              <div
                key={`${dayIndex}-${hour}`}
                className="border-r border-b border-gray-300 last:border-r-0 relative h-25 hover:bg-gray-50 cursor-pointer transition-colors"
                onClick={() => handleCellClick(day, hour)}
              >
                {hour === 7 &&
                  getEventsForDay(day).map((event) => (
                    <ContextMenu key={event.timeSlotId}>
                      <ContextMenuTrigger>
                        <CalendarEvent
                          event={event}
                          top={getEventPosition(event.date, event.startTime)}
                          height={getEventHeight(
                            event.date,
                            event.startTime,
                            event.endTime,
                          )}
                          onUpdate={(newEvent) =>
                            handleUpdateEvent(event, newEvent)
                          }
                          onDelete={() => handleDeleteEvent(event.timeSlotId)}
                          currentWeek={currentWeek}
                          dayIndex={dayIndex}
                          readOnly={readOnly}
                        />
                      </ContextMenuTrigger>

                      <ContextMenuContent className="w-52">
                        <ContextMenuItem
                          inset
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDeleteEvent(event.timeSlotId);
                          }}
                        >
                          Supprimer
                        </ContextMenuItem>
                        <ContextMenuSeparator />
                        <ContextMenuItem inset disabled>
                          Copier
                        </ContextMenuItem>
                        <ContextMenuItem inset disabled>
                          Couper
                        </ContextMenuItem>
                        <ContextMenuItem inset disabled>
                          Coller
                        </ContextMenuItem>
                      </ContextMenuContent>
                    </ContextMenu>
                  ))}
              </div>
            ))}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ScheduleCalendar;
