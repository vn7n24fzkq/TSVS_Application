package vn7.tsvsapplication.base;

public class CalendarItem {
    private String date;
    private String schedule;
    private String department;

    public CalendarItem(String date, String schedule, String department) {
        this.date = date;
        this.schedule = schedule;
        this.department = department;
    }

    public String getDate() {
        return date;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getDepartment() {
        return department;
    }
}
