package it.polimi.ingsw.am49.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Simple Logger wrapper class
 */
public class Log {
    private static Logger logger;
    private static String directory = "logs/";

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

//            if (console) {
//                SystemOutHandler consoleHandler = new SystemOutHandler();
//                consoleHandler.setFormatter(new CustomFormatter());
//                logger.addHandler(consoleHandler);
//            }
        } catch (IOException e) {
            System.err.println("Error setting up file handler for logger.");
        }

        return true;
    }

    public static Logger getLogger() {
        if (Log.logger == null) {
            Log.initializeLogger("default.log", false);
        }
        return Log.logger;
    }

    public static void setLevel(Level level) {
        if (Log.logger != null) {
            Log.logger.setLevel(level);
        }
    }

    public static void disable() {
        Log.setLevel(Level.OFF);
    }

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

    static class SystemOutHandler extends ConsoleHandler {
        public SystemOutHandler() {
            super();
            setOutputStream(System.out);
        }
    }
}