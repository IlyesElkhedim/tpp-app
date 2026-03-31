import { useEffect, useState, type FC } from "react";
import { Dialog, DialogClose, DialogContent } from "../ui/dialog";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import type { TimeSlot } from "../../types/TimeSlot";
import { DialogTitle, DialogTrigger } from "@radix-ui/react-dialog";
import { Settings } from "lucide-react";
import { computeSubmissionTimes } from "@/utils/timestampsUtils";

interface TimeSlotModalProps {
  currentTimeSlot: TimeSlot;
  onUpdate: (updatedTimeSlot: TimeSlot) => void;
  open?: boolean;
  setOpen?: (open: boolean) => void;
}

const TimeSlotModal: FC<TimeSlotModalProps> = ({
  currentTimeSlot,
  onUpdate,
  open: controlledOpen,
  setOpen: controlledSetOpen,
}) => {
  const [currentPage, setCurrentPage] = useState<
    "duration" | "rendering" | "room"
  >("duration");
  console.log("Current TimeSlot:", currentTimeSlot.startTime);
  const [startTime, setStartTime] = useState(currentTimeSlot.startTime);
  const [endTime, setEndTime] = useState(currentTimeSlot.endTime);
  const [submissionStartTime, setSubmissionStartTime] = useState(
    currentTimeSlot.submissionStartTime,
  );
  const [submissionEndTime, setSubmissionEndTime] = useState(
    currentTimeSlot.submissionEndTime,
  );
  const [room, setRoom] = useState(currentTimeSlot.room || "");

  const [uncontrolledOpen, setUncontrolledOpen] = useState(false);
  const open = controlledOpen !== undefined ? controlledOpen : uncontrolledOpen;
  const setOpen =
    controlledSetOpen !== undefined ? controlledSetOpen : setUncontrolledOpen;
  const originalEndTime = currentTimeSlot.endTime;

  useEffect(() => {
    setStartTime(currentTimeSlot.startTime);
    setEndTime(currentTimeSlot.endTime);
    setSubmissionStartTime(currentTimeSlot.submissionStartTime);
    setSubmissionEndTime(currentTimeSlot.submissionEndTime);
    setRoom(currentTimeSlot.room || "");
  }, [open, currentTimeSlot]);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      {controlledOpen === undefined && (
        <DialogTrigger asChild>
          <button className="bg-transparent text-blue-950 hover:cursor-pointer shrink-0 pointer-events-auto">
            <Settings size={16} />
          </button>
        </DialogTrigger>
      )}

      <DialogContent className="sm:max-w-md p-0 gap-0" showCloseButton={false}>
        <DialogTitle className="flex w-full justify-between text-xl font-semibold mb-4 bg-gray-300 rounded-t-lg">
          <button
            className={`flex flex-1 justify-center p-3 text-gray-600 ${
              currentPage === "duration"
                ? "border-b-2 border-blue-950 bg-blue-950 text-white rounded-t-lg"
                : "hover:border-b-2 hover:border-blue-950 hover:text-blue-950"
            }`}
            onClick={() => {
              setCurrentPage("duration");
            }}
          >
            Durée
          </button>
          <button
            className={`flex flex-1 justify-center p-3 text-gray-600 ${
              currentPage === "rendering"
                ? "border-b-2 border-blue-950 bg-blue-950 text-white rounded-t-lg"
                : "hover:border-b-2 hover:border-blue-950 hover:text-blue-950"
            }`}
            onClick={() => {
              setCurrentPage("rendering");
            }}
          >
            Rendu
          </button>
          <button
            className={`flex flex-1 justify-center p-3 text-gray-600 ${
              currentPage === "room"
                ? "border-b-2 border-blue-950 bg-blue-950 text-white rounded-t-lg"
                : "hover:border-b-2 hover:border-blue-950 hover:text-blue-950"
            }`}
            onClick={() => {
              setCurrentPage("room");
            }}
          >
            Salles
          </button>
        </DialogTitle>
        <div className="px-6 pb-4">
          {currentPage === "duration" && (
            <form
              key="duration"
              className="space-y-4 "
              onSubmit={(e) => e.preventDefault()}
            >
              <h2 className="text-2xl font-bold mb-2">Durée du créneau :</h2>
              <div>
                <label
                  className="block text-sm font-medium mb-1"
                  htmlFor="session-duration"
                >
                  Heure de début :
                </label>
                <Input
                  type="time"
                  id="time-picker-start"
                  step="1"
                  value={startTime}
                  onChange={(e) => setStartTime(e.target.value)}
                  className="bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
                />
              </div>
              <div>
                <label
                  className="block text-sm font-medium mb-1"
                  htmlFor="break-duration"
                >
                  Heure de fin
                </label>
                <Input
                  type="time"
                  id="time-picker-end"
                  step="1"
                  value={endTime}
                  onChange={(e) => setEndTime(e.target.value)}
                  className="bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
                />
              </div>
            </form>
          )}
          {currentPage === "rendering" && (
            <form
              key="rendering"
              className="space-y-4 "
              onSubmit={(e) => e.preventDefault()}
            >
              <h2 className="text-2xl font-bold mb-2">
                Créneau de rendu des CR :
              </h2>
              <div>
                <label
                  className="block text-sm font-medium mb-1"
                  htmlFor="session-duration"
                >
                  Heure de début :
                </label>
                <Input
                  type="time"
                  id="submission-start"
                  step="1"
                  value={submissionStartTime}
                  onChange={(e) => setSubmissionStartTime(e.target.value)}
                  className="bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
                />
              </div>
              <div>
                <label
                  className="block text-sm font-medium mb-1"
                  htmlFor="break-duration"
                >
                  Heure de fin
                </label>
                <Input
                  type="time"
                  id="submission-end"
                  step="1"
                  value={submissionEndTime}
                  onChange={(e) => setSubmissionEndTime(e.target.value)}
                  className="bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
                />
              </div>
            </form>
          )}
          {currentPage === "room" && (
            <form
              key="room"
              className="space-y-4 "
              onSubmit={(e) => e.preventDefault()}
            >
              <h2 className="text-2xl font-bold mb-2">Salle :</h2>
              <div>
                <label
                  className="block text-sm font-medium mb-1"
                  htmlFor="room-input"
                >
                  Nom de la salle
                </label>
                <Input
                  type="text"
                  id="room-input"
                  value={room}
                  onChange={(e) => setRoom(e.target.value)}
                  className="bg-background"
                />
              </div>
            </form>
          )}
          <div className="mt-6 flex justify-center space-x-4">
            <DialogClose asChild>
              <Button variant="outline">Annuler</Button>
            </DialogClose>
            <Button
              onClick={() => {
                const { newSubmissionStartTime, newSubmissionEndTime } =
                  computeSubmissionTimes({
                    endTime,
                    originalEndTime,
                    submissionStartTime,
                    submissionEndTime,
                    currentTimeSlot,
                  });
                onUpdate({
                  ...currentTimeSlot,
                  startTime,
                  endTime,
                  submissionStartTime: newSubmissionStartTime,
                  submissionEndTime: newSubmissionEndTime,
                  room: room || "",
                });
                setOpen(false);
              }}
              variant="default"
            >
              Valider
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default TimeSlotModal;
