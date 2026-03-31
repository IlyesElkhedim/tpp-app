import type { Supervisor } from "@/types/Supervisor";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const getSupervisors = async (): Promise<Supervisor[]> => {
  const response = await fetch(`${baseURL}/api/supervisors`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération des responsables (${response.status}) : ${errorText}`,
    );
  }

  const res = await response.json();
  return res;
};

export const getSupervisorById = async (id: number): Promise<Supervisor> => {
  const response = await fetch(`${baseURL}/api/supervisors/${id}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération du responsable (${response.status}) : ${errorText}`,
    );
  }

  const res = await response.json();
  return res;
};