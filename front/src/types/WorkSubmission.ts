import { z } from "zod";

export enum WorkType {
  PERSONAL_WORK = "Travail personnel pour la formation",
  COLLECTIVE_WORK = "Travail collectif pour la formation",
  COMPANY = "Travail pour l'entreprise",
  OTHER = "Autre",
}

const workSchema = z.object({
  workType: z.enum(Object.keys(WorkType)),
  subject: z.string().min(1, "Sujet requis"),
  description: z.string().min(1, "Description requise"),
  timeSpent: z.coerce.number().int().min(1, "Durée requise (min)"),
});
export const workSubmissionSchema = z.object({
  works: z.array(workSchema).min(1, "Au moins un travail est requis"),
});
export type WorkSubmissionType = z.infer<typeof workSubmissionSchema>;

export interface GetWorkSubmissionPayload extends WorkSubmissionType {
  idStudent: string;
  idTimeSlot: number;
  workSubmissionStatus: "VALID" | "INVALID";
  workSubmissionGrade: "ZERO" | "HALF" | "ONE" | null;
}