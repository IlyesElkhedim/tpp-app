import type {
  GetWorkSubmissionPayload,
  WorkSubmissionType,
} from "@/types/WorkSubmission";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const addWorkSubmission = async (
  studentId: string,
  timeslotId: string,
  workSubmission: WorkSubmissionType,
) => {
  const response = await fetch(
    `${baseURL}/api/timeslots/${studentId}/${timeslotId}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(workSubmission.works),
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de l'ajout du CR (${response.status}) : ${errorText}`,
    );
  }
};

export const getWorkSubmission = async (
  studentId: string,
  timeslotId: string,
): Promise<GetWorkSubmissionPayload> => {
  const response = await fetch(
    `${baseURL}/api/timeslots/${studentId}/${timeslotId}`,
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
      `Erreur lors de la récupération du CR (${response.status}) : ${errorText}`,
    );
  }

  const data = await response.json();
  return data;
};

export const setWorkSubmissionGrade = async (
  studentId: string,
  timeslotId: string,
  note: number,
) => {
  let body;
  switch (note) {
    case 0:
      body = { grade: "ZERO" };
      break;
    case 0.5:
      body = { grade: "HALF" };
      break;
    case 1:
      body = { grade: "ONE" };
      break;
    default:
      throw new Error(`Note invalide : ${note}`);
  }
  const response = await fetch(
    `${baseURL}/api/timeslots/${studentId}/${timeslotId}/grade`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    },
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la mise à jour de la note du CR (${response.status}) : ${errorText}`,
    );
  }
};
