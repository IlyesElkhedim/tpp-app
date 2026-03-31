import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { Course } from "@/types/Courses";

interface CourseState {
  currentCourse: Course | null;
}

const initialState: CourseState = {
  currentCourse: null,
};

const courseSlice = createSlice({
  name: "course",
  initialState,
  reducers: {
    setCurrentCourse(state, action: PayloadAction<Course | null>) {
      state.currentCourse = action.payload;
    },
  },
});

export const { setCurrentCourse } = courseSlice.actions;
export default courseSlice.reducer;
