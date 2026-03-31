"use client";

import * as React from "react";
import { cn } from "../../lib/utils";
import { Check, Funnel, X } from "lucide-react";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "./command";
import { Popover, PopoverContent, PopoverTrigger, } from "./popover";
import { Button } from "./button";

interface MultiSelectProps {
  options: { value: string; label: string }[];
  selected: string[];
  onChange: (values: string[]) => void;
  placeholder?: string;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function MultiSelect({
  options,
  selected,
  onChange,
  open: externalOpen,
  onOpenChange: externalOnOpenChange,
}: MultiSelectProps) {
  const [internalOpen, setInternalOpen] = React.useState(false);

  const open = externalOpen !== undefined ? externalOpen : internalOpen;
  const setOpen = externalOnOpenChange || setInternalOpen;

  const toggleOption = (value: string) => {
    if (selected.includes(value)) {
      onChange(selected.filter((v) => v !== value));
    } else {
      onChange([...selected, value]);
    }
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button variant="ghost" className="hover:bg-transparent cursor-pointer p-0">
          <Funnel className={cn("w-1 h-1", selected.length > 0 ? "text-green-500" : "text-gray-500")} />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[200px] p-0">
        <Command>
          <div className="flex items-center p-1 ">
            <CommandInput placeholder="Rechercher..." className="flex-1 border-0 p-0 shadow-none focus:ring-0" />
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setOpen(false)}
              className="h-6 w-6 p-0 ml-2"
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
          <CommandEmpty>No options found.</CommandEmpty>
          <CommandGroup>
            {options.map((option) => (
              <CommandItem
                key={option.value}
                onSelect={() => toggleOption(option.value)}
              >
                <Check
                  className={cn(
                    "mr-2 h-4 w-4",
                    selected.includes(option.value) ? "opacity-100" : "opacity-0"
                  )}
                />
                {option.label}
              </CommandItem>
            ))}
          </CommandGroup>
          {selected.length > 0 && (
            <CommandGroup>
              <CommandItem
                onSelect={() => onChange([])}
              >
                Tout désélectionner
              </CommandItem>
            </CommandGroup>
          )}
        </Command>
      </PopoverContent>
    </Popover>
  );
}