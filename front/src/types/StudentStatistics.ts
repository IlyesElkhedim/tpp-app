import type {Student} from "../types/Student.ts";

export interface StudentStatistics extends Omit<Student, 'currentCourse' | 'courseIds'> {
  attendanceRate: number;
  justifiedAbsences: number;
  unjustifiedAbsences: number;
  validReports: number;
  invalidReports: number;
  notSubmittedReports: number;
}