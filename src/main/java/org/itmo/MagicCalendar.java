package org.itmo;

import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MagicCalendar {

    // словарь с пользователями со встречами пользователь -  объект типа класс
    //
    private final Map<String, List<Meeting>> userMeetings = new HashMap<>();



    // Перечисление типов встреч
    public enum MeetingType {
        WORK, PERSONAL
    }


    // Класс Встреча
    public static class Meeting {
        LocalTime time;
        MeetingType type;

        public Meeting(LocalTime time, MeetingType type) {
            this.time = time;
            this.type = type;
        }

        // начальное время + 1 час
        public LocalTime getEndTime() {
            return this.time.plusHours(1);
        }
    }


    /**
     * Запланировать встречу для пользователя.
     *
     * @param user имя пользователя
     * @param time временной слот (например, "10:00")
     * @param type тип встречи (WORK или PERSONAL)
     * @return true, если встреча успешно запланирована, false если:
     *         - в этот временной слот уже есть встреча, и правило замены не выполняется,
     *         - лимит в 5 встреч в день уже достигнут.
     *
     *
     */
    public boolean scheduleMeeting(String user, String time, MeetingType type) {

        LocalTime newTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime newEndTime = newTime.plusHours(1);

        userMeetings.putIfAbsent(user, new ArrayList<>());
        List<Meeting> meetings = userMeetings.get(user);

        // Проверка лимита встреч
        if (meetings.size() >= 5) {
            return false;
        }

        // Проверка пересечения встреч
        for (Meeting meeting : meetings) {
            LocalTime existingStart = meeting.time;
            LocalTime existingEnd = meeting.getEndTime();

            // Проверяем пересечение по времени
            if (!(newEndTime.isBefore(existingStart) || newTime.isAfter(existingEnd))) {

                if (meeting.type == MeetingType.WORK && type == MeetingType.PERSONAL) {
                    meetings.remove(meeting);
                    meetings.add(new Meeting(newTime, type));
                    return true;
                }
                return false;
            }
        }

        // Добавляем встречу
        meetings.add(new Meeting(newTime, type));
        return true;

    }

    /**
     * Получить список всех встреч пользователя.
     *
     * @param user имя пользователя
     * @return список временных слотов, на которые запланированы встречи.
     */
    public List<String> getMeetings ( String user) {

        if (!userMeetings.containsKey(user)) {
            return Collections.emptyList();
        }
        List<String> meetingTimes = new ArrayList<>();
        for (Meeting meeting : userMeetings.get(user)) {
            meetingTimes.add(String.valueOf(meeting.time));
        }
        return meetingTimes;
    }
    /**
     * Отменить встречу для пользователя по заданному времени.
     *
     * @param user имя пользователя
     * @param time временной слот, который нужно отменить.
     * @return true, если встреча была успешно отменена; false, если:
     *         - встреча в указанное время отсутствует,
     *         - встреча имеет тип PERSONAL (отменять можно только WORK встречу).
     */
    public boolean cancelMeeting(String user, String time) {
        if (!userMeetings.containsKey(user)) {
            return false;
        }
        List<Meeting> meetings = userMeetings.get(user);
        LocalTime targetTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime targetEndTime = targetTime.plusHours(1);

        for (int i = 0; i < meetings.size(); i++) {
            Meeting meeting = meetings.get(i);
            if (meeting.time.equals(targetTime)) {
                if (meeting.type == MeetingType.WORK) {
                    meetings.remove(i);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

}
