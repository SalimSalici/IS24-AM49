package it.polimi.ingsw.am49.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Simple Logger wrapper class.
 */
public class Log {

    /**
     * The Logger instance used for logging messages.
     */
    private static Logger logger;

    /**
     * The directory where log files will be stored.
     */
    private static String directory = "logs/";

    /**
     * Initializes the logger with the specified filename and console output option.
     *
     * @param filename the name of the log file
     * @param console  true if logging should also be output to the console, false otherwise
     * @return true if the logger was successfully initialized, false if the logger was already initialized
     */
    public static boolean initializeLogger(String filename, boolean console) {
        if (Log.logger != null)
            return false;

        logger = Logger.getLogger("log");
        if (!console)
            logger.setUseParentHandlers(false);

        try {
            FileHandler fh = new FileHandler(directory + filename, false);
            fh.setFormatter(new CustomFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Error setting up file handler for logger.");
        }

        return true;
    }

    /**
     * Gets the Logger instance, initializing it with a default log file if necessary.
     *
     * @return the Logger instance
     */
    public static Logger getLogger() {
        if (Log.logger == null) {
            Log.initializeLogger("default.log", false);
        }
        return Log.logger;
    }

    /**
     * Sets the logging level for the Logger.
     *
     * @param level the logging level to be set
     */
    public static void setLevel(Level level) {
        if (Log.logger != null) {
            Log.logger.setLevel(level);
        }
    }

    /**
     * Disables logging by setting the logging level to OFF.
     */
    public static void disable() {
        Log.setLevel(Level.OFF);
    }

    /**
     * Custom log formatter class.
     */
    static class CustomFormatter extends Formatter {
        private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
        private static final String PACKAGE_PREFIX = "it.polimi.ingsw.am49.";

        @Override
        public String format(LogRecord record) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN);
            String className = record.getSourceClassName();

            // Remove the package prefix if present
            if (className.startsWith(PACKAGE_PREFIX)) {
                className = className.substring(PACKAGE_PREFIX.length());
            }

            return dateFormat.format(new Date(record.getMillis())) +
                    " " +
                    record.getLevel().getLocalizedName() +
                    "\t" +
                    className +
                    "\t- " +
                    formatMessage(record) +
                    System.lineSeparator();
        }
    }

    /**
     * Custom handler for system output.
     */
    static class SystemOutHandler extends ConsoleHandler {
        public SystemOutHandler() {
            super();
            setOutputStream(System.out);
        }
    }
}