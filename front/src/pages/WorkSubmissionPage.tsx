import { useEffect, useState } from "react";
import { useForm, useFieldArray } from "react-hook-form";
import type { TimeSlot } from "@/types/TimeSlot";
import { ArrowLeft, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { WorkSubmissionField } from "@/components/WorkSubmissionField";
import { useNavigate, useParams } from "react-router";
import { getTimeSlotById } from "@/services/timeSlotService";
import { Separator } from "@/components/ui/separator";
import {
  WorkType,
  type GetWorkSubmissionPayload,
  type WorkSubmissionType,
} from "@/types/WorkSubmission";
import {
  addWorkSubmission,
  getWorkSubmission,
} from "@/services/workSubmissionService";
import { store } from "@/store/store";
import { formatHourMinute } from "@/utils/timestampsUtils";

const WorkSubmissionPage = () => {
  const navigate = useNavigate();
  const [tpp, setTpp] = useState<TimeSlot>();
  const [data, setData] = useState<GetWorkSubmissionPayload | null>(null);
  const { tppId } = useParams();
  const currentUser = store.getState().user.currentUser;
  const currentStudentId =
    currentUser && "studentNumber" in currentUser
      ? currentUser.studentNumber
      : undefined;

  useEffect(() => {
    const fetchTppDetails = async () => {
      if (tppId) {
        const res = await getTimeSlotById(tppId!);
        setTpp(res);
      }
    };

    const fetchData = async () => {
      if (!currentStudentId || !tppId) return;
      const fetched = await getWorkSubmission(currentStudentId, tppId);
      if (fetched && fetched.works && fetched.works.length > 0) {
        setData(fetched);
      } else {
        setData(null);
      }
    };

    fetchTppDetails();
    fetchData();
  }, [tppId]);

  const {
    control,
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<WorkSubmissionType>({
    defaultValues: {
      works: [],
    },
  });
  const { fields, append, remove } = useFieldArray({
    control,
    name: "works",
  });

  const onSubmit = (data: WorkSubmissionType) => {
    const sendWorkSubmissions = async () => {
      if (!tppId || !currentStudentId) return;
      await addWorkSubmission(currentStudentId, tppId, data);
      navigate("/student/calendar");
    };

    sendWorkSubmissions();
  };

  if (!tpp) {
    return <div>Chargement...</div>;
  }

  if (data) {
    return (
      <div className="flex flex-col h-screen p-6 gap-6">
        <Button variant="outline" onClick={() => navigate("student/calendar")}>
          {" "}
          <ArrowLeft className="h-4 w-4" /> Retour{" "}
        </Button>
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
          const worksOfType = data.works.filter(
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
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen p-6 gap-6">
      <div className="flex flex-row items-center justify-between">
        <div className="flex flex-col gap-1">
          <h2 className="text-2xl font-bold">
            <b>CR </b>
            <span className="text-lg font-normal">du : {` ${tpp.date} `}</span>
          </h2>
          <span className="text-md font-normal block text-gray-400">
            {formatHourMinute(tpp.startTime)} - {formatHourMinute(tpp.endTime)}
          </span>
        </div>
        <Button variant="outline" onClick={() => navigate("student/calendar")}>
          {" "}
          <ArrowLeft className="h-4 w-4" /> Retour{" "}
        </Button>
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* ...existing code... */}
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
        ].map((section, sectionIdx) => (
          <div key={section.type} className="mb-4">
            <div className="flex flex-row justify-between items-center mb-2">
              {sectionIdx + 1} - {section.label}
              <Button
                type="button"
                variant="ghost"
                onClick={() =>
                  append({
                    workType: section.type,
                    subject: "",
                    description: "",
                    timeSpent: 60,
                  })
                }
              >
                <Plus />
              </Button>
            </div>
            <div className="flex flex-col gap-6">
              {fields
                .filter((field) => field.workType === section.type)
                .map((field) => (
                  <WorkSubmissionField
                    key={field.id}
                    register={register}
                    idx={fields.findIndex((f) => f.id === field.id)}
                    // errors={
                    //   errors.works?.[fields.findIndex((f) => f.id === field.id)]
                    // }
                    defaultValues={field}
                    remove={remove}
                  />
                ))}
            </div>
            <Separator className="my-6 bg-black" />
          </div>
        ))}
        {errors.works?.message && (
          <div className="text-red-500 text-xs mt-1 self-center">
            {errors.works.message}
          </div>
        )}
        <div className="mt-6 flex justify-center gap-4">
          <Button type="submit" className="px-8">
            Valider
          </Button>
        </div>
      </form>
    </div>
  );
};

export default WorkSubmissionPage;
