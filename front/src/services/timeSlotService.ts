import type { TimeSlot } from "../types/TimeSlot";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export type TimeSlotPayload = Omit<TimeSlot, "timeSlotId">;

/**
 * Creates a new time slot on the server.
 * @param {TimeSlotPayload} payload - The time slot data to create (all fields except timeSlotId)
 * @returns {Promise<TimeSlot>} A promise that resolves to the created time slot with its ID
 * @throws {Error} If the creation fails, throws an error with the HTTP status and error message
 */
export async function createTimeslot(
  payload: TimeSlotPayload
): Promise<TimeSlot> {
  const response = await fetch(`${baseURL}/api/timeslots`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la création du timeslot (${response.status}) : ${errorText}`
    );
  }

  return response.json();
}

/**
 * Retrieves all time slots for a specific course within a date range.
 * @param {string} startDate - The start date in ISO format (YYYY-MM-DD)
 * @param {string} endDate - The end date in ISO format (YYYY-MM-DD)
 * @param {number} courseId - The ID of the course
 * @returns {Promise<TimeSlot[]>} A promise that resolves to an array of time slots within the date range
 * @throws {Error} If the retrieval fails, throws an error with the HTTP status and error message
 */
export async function getTimeSlotsBetweenDates(
  startDate: string,
  endDate: string, 
  courseId: number
): Promise<TimeSlot[]> {
  const response = await fetch(
    `${baseURL}/api/courses/${courseId}/timeslots?startDate=${startDate}&endDate=${endDate}`
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération des créneaux (${response.status}) : ${errorText}`
    );
  }

  const res = await response.json();
  return res;
}

/**
 * Updates an existing time slot on the server.
 * @param {string} id - The ID of the time slot to update
 * @param {TimeSlotPayload} payload - The updated time slot data
 * @returns {Promise<TimeSlot>} A promise that resolves to the updated time slot
 * @throws {Error} If the update fails, throws an error with the HTTP status and error message
 */
export async function updateTimeSlot(
  id: string,
  payload: TimeSlotPayload
): Promise<TimeSlot> {
  const response = await fetch(`${baseURL}/api/timeslots/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la mise à jour du créneau (${response.status}) : ${errorText}`
    );
  }

  const res = await response.json();
  return res;
}

/**
 * Deletes a time slot from the server.
 * @param {string} id - The ID of the time slot to delete
 * @returns {Promise<void>} A promise that resolves when the deletion is complete
 * @throws {Error} If the deletion fails, throws an error with the HTTP status and error message
 */
export async function deleteTimeSlot(id: string): Promise<void> {
  const response = await fetch(`${baseURL}/api/timeslots/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la suppression du créneau (${response.status}) : ${errorText}`
    );
  }
}

/**
 * Retrieves a specific time slot by its ID.
 * @param {string} id - The ID of the time slot to retrieve
 * @returns {Promise<TimeSlot>} A promise that resolves to the time slot
 * @throws {Error} If the retrieval fails, throws an error with the HTTP status and error message
 */
export async function getTimeSlotById(id: string): Promise<TimeSlot> {
  const response = await fetch(`${baseURL}/api/timeslots/${id}`);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la récupération du créneau (${response.status}) : ${errorText}`
    );
  }

  return response.json();
}

/**
 * Imports ICS events from an automatically constructed URL using course ID.
 * The backend automatically determines the ICS URL based on the course ID.
 * @param {number} courseId - The ID of the course to import events for
 * @returns {Promise<string>} A promise that resolves to a success message from the server
 * @throws {Error} If the import fails, throws an error with the HTTP status and error message
 */
export async function importIcsByCourseId(
  courseId: number
): Promise<string> {
  const response = await fetch(
    `${baseURL}/api/timeslots/${courseId}/import-ics-auto`,
    {
      method: "POST",
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de l'importation ICS (${response.status}) : ${errorText}`
    );
  }

  return response.text();
}

/**
 * Import ICS events from a complete URL provided by the user.
 * @param {number} courseId - The ID of the course to import events for
 * @param {string} icsUrl - The complete URL to the ICS file
 * @returns {Promise<string>} A promise that resolves to a success message from the server
 * @throws {Error} If the import fails, throws an error with the HTTP status and error message
 */
export async function importIcsFromUrl(
  courseId: number,
  icsUrl: string
): Promise<string> {
  const response = await fetch(
    `${baseURL}/api/timeslots/${courseId}/import-ics-url?icsUrl=${encodeURIComponent(icsUrl)}`,
    {
      method: "POST",
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de l'importation ICS (${response.status}) : ${errorText}`
    );
  }

  return response.text();
}

/**
 * Imports ICS events from an uploaded file.
 * @param {number} courseId - The ID of the course to import events for
 * @param {File} file - The ICS file to upload and import
 * @returns {Promise<string>} A promise that resolves to a success message from the server
 * @throws {Error} If the import fails, throws an error with the HTTP status and error message
 */
export async function importIcsFromFileUpload(
  courseId: number,
  file: File
): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(
    `${baseURL}/api/timeslots/${courseId}/import-ics-upload`,
    {
      method: "POST",
      body: formData,
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de l'importation ICS (${response.status}) : ${errorText}`
    );
  }

  return response.text();
}

/**
 * Imports time slots from an uploaded Excel file.
 * @param {number} courseId - The ID of the course to import time slots for
 * @param {File} file - The Excel file containing time slot data to upload and import
 * @returns {Promise<string>} A promise that resolves to a success message from the server
 * @throws {Error} If the import fails, throws an error with the HTTP status and error message
 */
export async function importTimeSlotsFromExcelUpload(
  courseId: number,
  file: File
): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(
    `http://localhost:8080/api/timeslots/${courseId}/import-excel-upload`,
    {
      method: "POST",
      body: formData,
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de l'importation Excel (${response.status}) : ${errorText}`
    );
  }

  return response.text();
}
