import type { GetWorkSubmissionPayload } from "@/types/WorkSubmission";
import type { FC } from "react";
import { Badge } from "./ui/badge";
import WorkSubmissionConsultation from "./modal/WorkSubmissionConsultation";
import type { TimeSlot } from "@/types/TimeSlot";

interface WorkSubmissionBadgeProps {
  workSubmissions: GetWorkSubmissionPayload[];
  tpp: TimeSlot;
  studentNumber: string;
}

export const WorkSubmissionBadge: FC<WorkSubmissionBadgeProps> = ({
  workSubmissions,
  tpp,
  studentNumber,
}) => {
  const hasSubmitted = workSubmissions.some(
    (ws) => ws.idStudent === studentNumber,
  );
  const workSubmission = workSubmissions.find(
    (ws) => ws.idStudent === studentNumber,
  );
  if (hasSubmitted && workSubmission) {
    const status = workSubmission.workSubmissionStatus;
    const variant = status === "VALID" ? "valid" : "destructive";
    return (
      <div className="flex w-full gap-2 items-center">
        <Badge
          variant={variant}
          className="flex items-center justify-center gap-2"
        >
          <span
            className={`rounded-full w-2 h-2 ${status === "VALID" ? "bg-green-500" : "bg-red-400"}`}
          >
            {""}
          </span>
          <span
            className={status === "VALID" ? "text-green-600" : "text-red-600"}
          >
            {status === "VALID" ? "Rendu valide" : "Rendu non valide"}
          </span>
        </Badge>

        <WorkSubmissionConsultation tpp={tpp} workSubmission={workSubmission} />
      </div>
    );
  }

  return (
    <Badge
      variant="secondary"
      className="flex w-full items-center justify-center gap-2 bg-gray-200"
    >
      <span className="rounded-full bg-gray-400 w-2 h-2">{""}</span>
      <span className="text-gray-600">Non rendu</span>
    </Badge>
  );
};
