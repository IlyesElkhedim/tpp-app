import type { TimeSlot } from "@/types/TimeSlot";

export const concatToTimestamp = (date: string, time: string): number => {
  const [year, month, day] = date.split("-").map(Number);
  const [hours, minutes, seconds] = time.split(":").map(Number);
  const dt = new Date(year, month - 1, day, hours, minutes, seconds);
  return dt.getTime();
}

export const timestampToMinutes = (timestamp: number): number => {
  const date = new Date(timestamp);
  return date.getHours() * 60 + date.getMinutes();
};

export const formatTime = (timestamp: number) => {
  const date = new Date(timestamp);
  return `${date.getHours().toString().padStart(2, "0")}:${date
    .getMinutes()
    .toString()
    .padStart(2, "0")}`;
};

export function computeSubmissionTimes({
  endTime,
  originalEndTime,
  submissionStartTime,
  submissionEndTime,
  currentTimeSlot,
}: {
  endTime: string;
  originalEndTime: string;
  submissionStartTime: string;
  submissionEndTime: string;
  currentTimeSlot: TimeSlot;
}) {
  const submissionStartTimeChanged = submissionStartTime !== currentTimeSlot.submissionStartTime;
  const submissionEndTimeChanged = submissionEndTime !== currentTimeSlot.submissionEndTime;
  let newSubmissionStartTime = submissionStartTime;
  let newSubmissionEndTime = submissionEndTime;

  if (
    endTime !== originalEndTime &&
    !submissionStartTimeChanged &&
    !submissionEndTimeChanged
  ) {
    const [h, m, s] = endTime.split(":").map(Number);
    const endDate = new Date(0, 0, 0, h, m, s || 0);
    const startDate = new Date(endDate.getTime() - 5 * 60 * 1000);
    const afterDate = new Date(endDate.getTime() + 15 * 60 * 1000);
    const pad = (n: number) => n.toString().padStart(2, "0");
    newSubmissionStartTime = `${pad(startDate.getHours())}:${pad(startDate.getMinutes())}:${pad(startDate.getSeconds())}`;
    newSubmissionEndTime = `${pad(afterDate.getHours())}:${pad(afterDate.getMinutes())}:${pad(afterDate.getSeconds())}`;
  }
  return { newSubmissionStartTime, newSubmissionEndTime };
}

export function formatHourMinute(time: string) {
    const [hours, minutes] = time.split(":");
    if (typeof hours === "undefined" || typeof minutes === "undefined")
      return time;
    return minutes === "00"
      ? `${parseInt(hours, 10)}h`
      : `${parseInt(hours, 10)}h${minutes}`;
  }