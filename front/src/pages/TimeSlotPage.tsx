
import { CourseSearch } from "@/components/CourseSearch";
import { TppList } from "@/components/TppList";
import { DatePicker } from "@/components/ui/datePicker";
import { getTimeSlotsBetweenDates } from "@/services/timeSlotService";
import type { RootState } from "@/store/store";
import type { TimeSlot } from "@/types/TimeSlot";
import { useEffect, useState } from "react";
import type { DateRange } from "react-day-picker";
import { useSelector } from "react-redux";

export const TppPage = () => {
  const [tppList, setTppList] = useState<TimeSlot[]>([]);
  const [range, setRange] = useState<DateRange | undefined>();
  const currentCourse = useSelector(
    (state: RootState) => state.course.currentCourse,
  );

  useEffect(() => {
    const fetchTimeSlots = async () => {
      if (!range?.from || !currentCourse || !range?.to) {
        return;
      }
      const fromStr = range.from.toLocaleDateString("fr-CA");
      const toStr = range.to.toLocaleDateString("fr-CA");
      const tpps = await getTimeSlotsBetweenDates(
        fromStr,
        toStr,
        currentCourse.id,
      );
      setTppList(tpps);
    };
    fetchTimeSlots();
  }, [currentCourse, range, currentCourse]);

  return (
    <div className="w-full h-full flex flex-col p-8 gap-3">
      <h1 className="text-3xl font-bold mb-4">Calendrier TPP</h1>

      <div className="flex justify-between">
        <CourseSearch />

        {currentCourse && <DatePicker range={range} setRange={setRange} />}
      </div>

      {currentCourse && <TppList tppList={tppList} />}
    </div>
  );
};
