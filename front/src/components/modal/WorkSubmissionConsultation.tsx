import { type FC, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { File } from "lucide-react";
import { formatHourMinute } from "@/utils/timestampsUtils";
import type { TimeSlot } from "@/types/TimeSlot";
import {
  WorkType,
  type GetWorkSubmissionPayload,
} from "@/types/WorkSubmission";
import { Table, TableBody, TableCell, TableRow } from "../ui/table";
import {
  getWorkSubmission,
  setWorkSubmissionGrade,
} from "@/services/workSubmissionService";

interface WorkSubmissionConsultationProps {
  tpp: TimeSlot;
  workSubmission: GetWorkSubmissionPayload;
}

const WorkSubmissionConsultation: FC<WorkSubmissionConsultationProps> = ({
  tpp,
  workSubmission,
}) => {
  const [currentWorkSubmission, setCurrentWorkSubmission] =
    useState<GetWorkSubmissionPayload>(workSubmission);

  const handleGradeClick = async (note: number) => {
    await setWorkSubmissionGrade(
      currentWorkSubmission.idStudent,
      tpp.timeSlotId,
      note,
    );
    const updated = await getWorkSubmission(
      currentWorkSubmission.idStudent,
      tpp.timeSlotId,
    );
    setCurrentWorkSubmission(updated);
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <File className="h-4 w-4 hover:cursor-pointer" />
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            <h2 className="text-2xl font-bold">
              <b>CR </b>
              <span className="text-lg font-normal">
                du : {` ${tpp.date} `}
              </span>
            </h2>
            <span className="text-md font-normal block text-gray-400">
              {formatHourMinute(tpp.startTime)} -{" "}
              {formatHourMinute(tpp.endTime)}
            </span>
          </DialogTitle>
        </DialogHeader>
        {[
          {
            label: WorkType.PERSONAL_WORK,
            type: "PERSONAL_WORK",
          },
          {
            label: WorkType.COLLECTIVE_WORK,
            type: "COLLECTIVE_WORK",
          },
          { label: WorkType.COMPANY, type: "COMPANY" },
          { label: WorkType.OTHER, type: "OTHER" },
        ].map((section, sectionIdx) => {
          const worksOfType = currentWorkSubmission.works.filter(
            (field) => field.workType === section.type,
          );
          if (worksOfType.length === 0) return null;
          return (
            <div key={section.type} className="flex flex-col gap-6">
              <div className="flex flex-col gap-2">
                <div className="flex flex-row justify-between items-center mb-2">
                  {sectionIdx + 1} - {section.label}
                </div>
                {worksOfType.map((field, index) => (
                  <div
                    key={index}
                    className="flex flex-col border p-4 rounded-md"
                  >
                    <p className="flex justify-between">
                      <b>Sujet : </b>
                      {field.subject}
                    </p>
                    <p className="flex justify-between">
                      <b>Description : </b>
                      {field.description}
                    </p>
                    <p className="flex justify-between">
                      <b>Temps passé : </b>
                      {field.timeSpent} minutes
                    </p>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
        <Table className="w-full border border-black hover:bg-transparent">
          <TableBody>
            <TableRow className="flex">
              <TableCell
                className={`flex flex-1 justify-center border-r border-black cursor-pointer ${currentWorkSubmission.workSubmissionGrade === "ZERO" ? "bg-red-500 text-white" : ""}`}
                onClick={() => handleGradeClick(0)}
              >
                0
              </TableCell>
              <TableCell
                className={`flex flex-1 justify-center border-r border-black cursor-pointer ${currentWorkSubmission.workSubmissionGrade === "HALF" ? "bg-yellow-500 text-white" : ""}`}
                onClick={() => handleGradeClick(0.5)}
              >
                {"0,5"}
              </TableCell>
              <TableCell
                className={`flex flex-1 justify-center cursor-pointer ${currentWorkSubmission.workSubmissionGrade === "ONE" ? "bg-green-500 text-white" : ""}`}
                onClick={() => handleGradeClick(1)}
              >
                1
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </DialogContent>
    </Dialog>
  );
};

export default WorkSubmissionConsultation;
