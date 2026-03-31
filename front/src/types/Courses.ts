export enum CourseNames {
  TIW = "TIW",
  SRS = "SRS",
}

export enum CourseLevels {
  M1 = "M1",
  M2 = "M2",
}

export interface Course {
  id: number;
  name: CourseNames;
  years: string;
  level: CourseLevels;
  supervisorId: number;
}
