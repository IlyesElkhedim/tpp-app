package back.projet.tpp.service;

import back.projet.tpp.dto.request.TimeSlotDto;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static back.projet.tpp.utils.IcsTimeSlotParser.*;

@Service
public class IcsTimeSlotImportService {
    /**
     * Extracts time slots from ICS calendar content.
     * Parses VEVENT entries and converts them to TimeSlotDto objects.
     * @param icsContent The raw ICS file content as a string
     * @param courseId The ID of the course to associate extracted time slots with
     * @return List of TimeSlotDto objects extracted from the ICS content
     * @throws IOException if reading the ICS content fails
     * @throws ResponseStatusException (BAD_REQUEST) if the ICS file is empty or null
     */
    public List<TimeSlotDto> extractTimeSlots(String icsContent, Integer courseId) throws IOException, ResponseStatusException {
        List<TimeSlotDto> timeSlotDtos = new ArrayList<>();

        if (icsContent == null || icsContent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ICS file is empty or could not be downloaded");
        }

        BufferedReader reader = new BufferedReader(new StringReader(icsContent));

        String line = reader.readLine();

        while (!line.startsWith("END:VCALENDAR")) {
            if (line.startsWith("BEGIN:VEVENT")) {
                IcsEvent event = parseEvent(reader);
                if (event != null) {
                    TimeSlotDto timeSlotDto = getTimeSlotDto(courseId, event);

                    timeSlotDtos.add(timeSlotDto);
                }
            }
            line = reader.readLine();
        }

        reader.close();

        return timeSlotDtos;
    }

    /**
     * Converts an IcsEvent to a TimeSlotDto with calculated default submission times.
     * Submission window defaults: 15 minutes before end time to 5 minutes after end time.
     * @param courseId The ID of the course
     * @param event The parsed ICS event
     * @return TimeSlotDto created from the ICS event data
     */
    private static @NonNull TimeSlotDto getTimeSlotDto(Integer courseId, IcsEvent event) {
        LocalDate date = event.startDateTime.toLocalDate();
        LocalTime startTime = event.startDateTime.toLocalTime();
        LocalTime endTime = event.endDateTime.toLocalTime();
        LocalTime defaultSubmissionStartTime = endTime.minusMinutes(15);
        LocalTime defaultSubmissionEndTime = endTime.plusMinutes(5);

        String room = event.location;

        return new TimeSlotDto(
                null,
                courseId,
                date,
                startTime,
                endTime,
                defaultSubmissionStartTime,
                defaultSubmissionEndTime,
                room
        );
    }
}
