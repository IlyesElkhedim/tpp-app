import type { Student } from "../types/Student";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export async function getAllCoursesStudents(
  courseId: number,
): Promise<Student[]> {
  const response = await fetch(
    `${baseURL}/api/courses/${courseId}/students`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération des étudiants (${response.status}) : ${errorText}`,
    );
  }

  const data = await response.json();
  return data;
}

export async function getStudentById(id: number): Promise<Student> {
  const response = await fetch(`${baseURL}/api/students/${id}`);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération de l'étudiant (${response.status}) : ${errorText}`,
    );
  }
  return response.json();
}

interface StudentPayload extends Omit<Student, "currentCourse" | "courseIds"> {
  courseIds: number[];
}

export async function createMultipleStudents(
  courseId: number,
  Students: StudentPayload[],
): Promise<void> {
  const response = await fetch(`${baseURL}/api/courses/${courseId}/students`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(Students),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la création des étudiants (${response.status}) : ${errorText}`,
    );
  }
}

export async function getAllStudents(): Promise<Student[]> {
  const response = await fetch(`${baseURL}/api/students`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération des étudiants (${response.status}) : ${errorText}`,
    );
  }

  const data = await response.json();
  return data;
}
