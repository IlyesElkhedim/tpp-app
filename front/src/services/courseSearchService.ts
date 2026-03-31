import type { Course } from "../types/Courses";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export async function getCourses(): Promise<Course[]> {
  const response = await fetch(`${baseURL}/api/courses`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Erreur lors de la récupération des cours (${response.status})`,
    );
  }

  const data = response.json();
  return data;
}

export async function createCourse(course: Course): Promise<void> {
  const response = await fetch(`${baseURL}/api/courses`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(course),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la création du cours (${response.status}) : ${errorText}`,
    );
  }
}

export async function deleteCourse(courseId: number): Promise<void> {
  const response = await fetch(
    `${baseURL}/api/courses/${courseId}`,
    {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la suppression du cours (${response.status}) : ${errorText}`,
    );
  }
}

export async function updateCourse(
  courseId: number,
  course: Course,
): Promise<void> {
  const response = await fetch(
    `${baseURL}/api/courses/${courseId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(course),
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la mise à jour du cours (${response.status}) : ${errorText}`,
    );
  }
}

export async function getCourseById(courseId: number): Promise<Course> {
  const response = await fetch(
    `${baseURL}/api/courses/${courseId}`,
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
      `Erreur lors de la récupération du cours (${response.status}) : ${errorText}`,
    );
  }

  const data = await response.json();
  return data;
}
