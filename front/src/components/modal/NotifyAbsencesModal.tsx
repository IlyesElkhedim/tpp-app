import { useEffect, useState, type FC } from "react";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "../ui/dialog";
import { Button } from "../ui/button";
import type { Student } from "@/types/Student";
import type { TimeSlot } from "@/types/TimeSlot";
import { DialogTitle } from "@radix-ui/react-dialog";
import type { Supervisor } from "@/types/Supervisor";
import { getCourseById } from "@/services/courseSearchService";
import { getSupervisorById } from "@/services/supervisorService";

interface NotifyAbsencesModalProps {
  absents: Student[];
  timeslot: TimeSlot;
}

const NotifyAbsencesModal: FC<NotifyAbsencesModalProps> = ({
  absents,
  timeslot,
}) => {
  const [supervisor, setSupervisor] = useState<Supervisor>();

  useEffect(() => {
    const getSupervisor = async () => {
      try {
        const course = await getCourseById(timeslot.courseId);
        const supervisor = await getSupervisorById(course.supervisorId);
        setSupervisor(supervisor);
      } catch (error) {
        console.error(
          "Erreur lors de la récupération du responsable de formation:",
          error,
        );
      }
    };

    getSupervisor();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="mt-4" variant="destructive">
          Notifier les absents
        </Button>
      </DialogTrigger>

      <DialogContent aria-describedby="Notify absences">
        <DialogHeader>
          <h2 className="text-2xl font-bold">Notification des absents</h2>
        </DialogHeader>

        <DialogTitle className="flex mt-4 gap-2 flex-col">
          <u>Liste des étudiants à notifier :</u>
          <p>
            {absents.length > 0
              ? absents.map((student) => (
                  <span key={student.email}>{student.email}; </span>
                ))
              : "Aucun étudiant absent à notifier."}
          </p>
        </DialogTitle>

        <div className="flex mt-4 gap-2 flex-col">
          <u>Message :</u>
          <p>
            Bonjour, <br />
            <br />
            Vous avez été absent(e) lors du TPP prévu le{" "}
            <b>{new Date(timeslot.date).toLocaleDateString()}</b> de{" "}
            <b>{timeslot.startTime}</b> à <b>{timeslot.endTime}</b>. Vous devez
            au plus vite justifier cette absence auprès de la Formation
            Continue.
            <br />
            <br />
            Cordialement,
            <br />
            <br />
            {supervisor ? supervisor.firstName + " " + supervisor.lastName : ""}
          </p>
        </div>

        <div className="mt-6 flex justify-center gap-4">
          <DialogClose asChild>
            <Button variant="outline" className="px-8">
              Fermer
            </Button>
          </DialogClose>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default NotifyAbsencesModal;
