import { configureStore } from "@reduxjs/toolkit";
import courseReducer from "./slice/courseSlice";
import userReducer from "./slice/userSlice";

export const store = configureStore({
  reducer: {
    course: courseReducer,
    user: userReducer,
  },
});

store.subscribe(() => {
  const { currentUser } = store.getState().user;
  try {
    if (currentUser === null) {
      localStorage.removeItem("currentUser");
    } else {
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
    }
  } catch {
    // ignore write errors (e.g. private browsing quota)
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
