import { useEffect, useState } from "react";
import type { Supervisor } from "@/types/Supervisor";
import type { Student } from "@/types/Student";
import { getAllStudents } from "@/services/studentService";
import LoginList from "@/components/LoginList";
import { getSupervisors } from "@/services/supervisorService";

export const LoginPage = () => {
  const [supervisors, setSupervisors] = useState<Supervisor[]>([]);
  const [students, setStudents] = useState<Student[]>([]);
  const [openList, setOpenList] = useState<"supervisors" | "students" | null>(null);

  useEffect(() => {
    const fetchSupervisors = async () => {
      setSupervisors(await getSupervisors());
    };

    const fetchStudents = async () => {
      setStudents(await getAllStudents());
    };

    fetchSupervisors();
    fetchStudents();
  }, []);

  return (
    <div className="flex h-screen flex-col items-center justify-center p-4 bg-gray-100">
      <h1 className="mb-8 text-2xl font-bold text-gray-800">Application de gestion des TPP</h1>

      <div className="flex flex-row bg-white rounded-lg shadow-lg h-3/4 w-3/4 overflow-y-auto">
        <LoginList
          title="Responsable de formation"
          users={supervisors}
          isOpen={openList === "supervisors"}
          onToggle={() => setOpenList(openList === "supervisors" ? null : "supervisors")}
        />
        <LoginList
          title="Étudiants"
          users={students}
          isOpen={openList === "students"}
          onToggle={() => setOpenList(openList === "students" ? null : "students")}
        />
      </div>
    </div>
  );
};
