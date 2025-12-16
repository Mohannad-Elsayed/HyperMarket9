package util;

public abstract class Config {
    public static final String DATA_DIRECTORY = "data/";
    public static final String USERS_FILE =         DATA_DIRECTORY + "Users.txt";
    public static final String PRODUCTS_FILE =      DATA_DIRECTORY + "Products.txt";
    public static final String ORDERS_FILE =        DATA_DIRECTORY + "Orders.txt";
    public static final String ID_TRACKER_FILE =    DATA_DIRECTORY + "id-tracker.txt";

    public static final String CSV_DELIMITER = ",";
    public static final String RANGE_CSV_DELIMITER = "!";

    public static String sanitize(String input) {
        String sanitized = input.replace(",", "");
        sanitized = sanitized.replace("\n", " ");
        sanitized = sanitized.replace("\t", " ");
        return sanitized;
    }
}