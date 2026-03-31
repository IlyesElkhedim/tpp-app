import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { Supervisor } from "@/types/Supervisor";
import type { Student } from "@/types/Student";

interface UserState {
  currentUser: Student | Supervisor | null;
}

const loadUserFromStorage = (): Student | Supervisor | null => {
  try {
    const serialized = localStorage.getItem("currentUser");
    return serialized ? (JSON.parse(serialized) as Student | Supervisor) : null;
  } catch {
    return null;
  }
};

const initialState: UserState = {
  currentUser: loadUserFromStorage(),
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setCurrentUser(state, action: PayloadAction<Student | Supervisor | null>) {
      state.currentUser = action.payload;
    },
    clearCurrentUser(state) {
      state.currentUser = null;
    },
  },
});

export const { setCurrentUser, clearCurrentUser } = userSlice.actions;
export default userSlice.reducer;
