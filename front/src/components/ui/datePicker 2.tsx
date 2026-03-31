"use client";

import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { format, startOfWeek, endOfWeek } from "date-fns";
import { ChevronDownIcon } from "lucide-react";
import type { DateRange } from "react-day-picker";
import type { FC } from "react";

interface DatePickerProps {
  range: DateRange | undefined;
  setRange: (range: DateRange | undefined) => void;
}

const DatePicker: FC<DatePickerProps> = ({ range, setRange }) => {
  const handleDayClick = (day: Date) => {
    const from = startOfWeek(day, { weekStartsOn: 1 });
    const to = endOfWeek(day, { weekStartsOn: 1 });

    setRange({ from, to });
  };

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className="w-65 justify-between text-left font-normal"
        >
          {range?.from && range?.to ? (
            <>
              {format(range.from, "dd/MM/yyyy")} – {format(range.to, "dd/MM/yyyy")}
            </>
          ) : (
            <span>Choisir une semaine</span>
          )}
          <ChevronDownIcon />
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="range"
          selected={range}
          weekStartsOn={1}
          onDayClick={handleDayClick}
          onSelect={() => {}}
        />
      </PopoverContent>
    </Popover>
  );
};

export { DatePicker };