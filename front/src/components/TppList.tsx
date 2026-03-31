import type { TimeSlot } from "@/types/TimeSlot";
import { type FC, useEffect, useState } from "react";
import CollapsibleCard from "./CollapsibleCard";
import { type Student } from "@/types/Student";
import { useSelector } from "react-redux";
import type { RootState } from "@/store/store";
import { getAllCoursesStudents } from "@/services/studentService";

interface TppListProps {
  tppList: TimeSlot[];
}

export const TppList: FC<TppListProps> = ({ tppList }) => {
  const [openTppId, setOpenTppId] = useState<string | null>(null);
  const currentCourse = useSelector(
    (state: RootState) => state.course.currentCourse,
  );
  const [students, setStudents] = useState<Student[]>([]);
  const [now] = useState(() => Date.now());

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const response = await getAllCoursesStudents(currentCourse!.id);
        setStudents(response);
      } catch (error) {
        console.error("Erreur lors du chargement des étudiants:", error);
      }
    };

    fetchStudents();
  }, [currentCourse]);

  return (
    <div className="w-full h-full flex flex-col p-8 gap-3">
      {tppList.length > 0 ? (
        <ul className="flex w-full flex-col gap-4">
          {tppList
            .sort((a, b) => {
              const dateA = new Date(`${a.date}T${a.startTime}`);
              const dateB = new Date(`${b.date}T${b.startTime}`);
              return dateA.getTime() - dateB.getTime();
            })
            .map((tpp) => {
              const [year, month, day] = tpp.date.split("-").map(Number);
              const [hours, minutes] = tpp.endTime.split(":").map(Number);
              const tppDate = new Date(
                year,
                month - 1,
                day,
                hours,
                minutes,
              ).getTime();
              const isPast = tppDate < now;

              if (!isPast) {
                return (
                  <CollapsibleCard
                    key={tpp.timeSlotId}
                    tpp={tpp}
                    students={students}
                    isOpen={openTppId === tpp.timeSlotId}
                    isPast={isPast}
                    onOpenChange={(isOpen) =>
                      setOpenTppId(isOpen ? tpp.timeSlotId : null)
                    }
                  />
                );
              } else {
                return (
                  <CollapsibleCard
                    key={tpp.timeSlotId}
                    tpp={tpp}
                    students={students}
                    isOpen={openTppId === tpp.timeSlotId}
                    isPast={isPast}
                    onOpenChange={(isOpen) =>
                      setOpenTppId(isOpen ? tpp.timeSlotId : null)
                    }
                  />
                );
              }
            })}
        </ul>
      ) : (
        <p className="text-gray-500 text-lg">
          Aucun TPP disponible pour la période sélectionnée.
        </p>
      )}
    </div>
  );
};
