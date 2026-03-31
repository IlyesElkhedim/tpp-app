import type { ContractType } from "./Contracts";
import type {AttendanceType} from "@/services/attendanceService.ts";

export interface Student {
  studentNumber: string;
  firstName: string;
  lastName: string;
  email: string;
  courseIds: number[];
  currentCourse: number;
  contractType?: ContractType;
  contractStartDate?: string;
}

export interface Attendance extends Student {
  attended: AttendanceType;
}
