package util;

import models.Employee;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final FileManager fileManager = new FileManager(Config.LOG_FILE);

    public static void log(Employee user, String action) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String userInfo;
        
        if (user != null) {
            userInfo = String.format("[%s|ID:%d|%s]", 
                user.getUserName(), 
                user.getId(), 
                user.getRole().toString());
        } else {
            userInfo = "[SYSTEM]";
        }
        
        String logEntry = String.format("%s %s %s", timestamp, userInfo, action);
        
        try {
            fileManager.writeLine(logEntry, true);
        } catch (Exception e) {
            System.err.println("Failed to write to activity log: " + e.getMessage());
        }
    }

    public static void log(String action) {
        Employee currentUser = null;
        try {
            currentUser = controllers.SystemManager.getInstance().getCurrentUser();
        } catch (Exception e) {
        }
        log(currentUser, action);
    }
}
