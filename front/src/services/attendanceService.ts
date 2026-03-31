import type { Attendance } from "@/types/Student";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export enum AttendanceType {
  PRESENT = "PRESENT",
  UNJUSTIFIED = "UNJUSTIFIED",
  JUSTIFIED = "JUSTIFIED"
}

export const getStudentAttendance = async (
  timeSlotId: string,
): Promise<{ studentId: string; attended: AttendanceType }[]> => {
  const response = await fetch(
    `${baseURL}/api/timeslots/${timeSlotId}/attendance`,
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération des présences (${response.status}) : ${errorText}`,
    );
  }

  const data = await response.json();
  return data.map((record: { studentId: string; attendanceStatus: AttendanceType }) => ({
    studentId: record.studentId,
    attended: record.attendanceStatus,
  }));
};

export const updateStudentAttendance = async (
  timeSlotId: string,
  attendance: Attendance[],
): Promise<void> => {
  const attendancePayload = attendance.map((record) => ({
    studentId: record.studentNumber,
    attendanceStatus: record.attended,
  }));
  const response = await fetch(
    `${baseURL}/api/timeslots/${timeSlotId}/attendance`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(attendancePayload),
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la mise à jour de la présence de l'étudiant (${response.status}) : ${errorText}`,
    );
  }
};
