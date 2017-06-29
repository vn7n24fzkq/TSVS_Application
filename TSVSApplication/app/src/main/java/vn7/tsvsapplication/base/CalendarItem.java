package vn7.tsvsapplication.base;

public class CalendarItem {
    private String date;
    private String schedule;
    private String department;

    public CalendarItem(String date, String schedule, String department) {
        this.date = date;
        this.schedule = schedule;
        //parser有時候會有意料之外的格式，先註解掉，待parser程式部分完善
        //  this.department = department;
        this.department = "";
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
