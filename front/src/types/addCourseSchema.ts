import { CourseLevels, CourseNames } from "@/types/Courses";
import { z } from "zod";

export const addCourseSchema = z.object({
  id: z.number().min(1, "Identifiant requis"),
  name: z.enum(Object.values(CourseNames)),
  years: z.string().min(1, "Année requise"),
  level: z.enum(Object.values(CourseLevels)),
  supervisorId: z.number().min(1, "Identifiant du responsable requis"),
});

export type AddCourseFormValues = z.infer<typeof addCourseSchema>;
