package back.projet.tpp.service;

import back.projet.tpp.dto.request.TimeSlotDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static back.projet.tpp.utils.ExcelTimeSlotParser.*;

@Service
public class ExcelTimeSlotImportService {
    /**
     * Extracts time slots from an uploaded Excel file.
     * Looks for columns "Dates" and "Horaires" to extract date and time information.
     * Automatically extracts the room from "Type salle" column.
     * Calculates default submission times: 15 minutes before end time to 5 minutes after.
     * @param file The uploaded Excel file
     * @param courseId The ID of the course to associate extracted time slots with
     * @return List of TimeSlotDto objects extracted from the Excel file
     * @throws IOException if file reading fails
     * @throws IllegalArgumentException if date or time format is invalid in the Excel file
     * @throws RuntimeException if required columns (Dates/Horaires) are not found
     */
    public List<TimeSlotDto> extractTimeSlots(MultipartFile file, Integer courseId) throws IOException, IllegalArgumentException, RuntimeException {
        List<TimeSlotDto> timeSlotDtos = new ArrayList<>();

        try (InputStream in = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(in);
            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            String room = extractRoom(sheet, formatter);

            int headerRowIndex = -1;
            int dateColumnIndex = -1;
            int hourColumnIndex = -1;

            for (Row row : sheet) {
                for (Cell cell : row) {

                    String value = formatter.formatCellValue(cell).trim();

                    if ("Dates".equalsIgnoreCase(value)) {
                        headerRowIndex = row.getRowNum();
                        dateColumnIndex = cell.getColumnIndex();
                    }

                    if ("Horaires".equalsIgnoreCase(value)) {
                        headerRowIndex = row.getRowNum();
                        hourColumnIndex = cell.getColumnIndex();
                    }
                }

                if (headerRowIndex != -1
                        && dateColumnIndex != -1
                        && hourColumnIndex != -1) {
                    break;
                }
            }

            if (headerRowIndex == -1) {
                throw new RuntimeException("Columns Dates/Horaires not found");
            }

            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String dateStr = formatter.formatCellValue(row.getCell(dateColumnIndex)).trim();
                String hourStr = formatter.formatCellValue(row.getCell(hourColumnIndex)).trim();

                if (dateStr.isBlank() || hourStr.isBlank()) continue;

                LocalDate date = parseDate(dateStr);
                LocalTime[] times = parseTimeRange(hourStr);

                LocalTime startTime = times[0];
                LocalTime endTime = times[1];

                LocalTime defaultSubmissionStartTime = endTime.minusMinutes(15);
                LocalTime defaultSubmissionEndTime = endTime.plusMinutes(5);

                TimeSlotDto timeSlotDto = new TimeSlotDto(
                        null,
                        courseId,
                        date,
                        startTime,
                        endTime,
                        defaultSubmissionStartTime,
                        defaultSubmissionEndTime,
                        room
                );

                timeSlotDtos.add(timeSlotDto);
            }
        }

        return timeSlotDtos;
    }
}
