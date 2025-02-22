package xyz.aweirdwhale.utils.log;

import java.io.IOException;
import java.util.logging.*;

public class logger {
    /*
    * This class is used to generate a logfile
    */

    private static final Logger LOGGER = Logger.getLogger(logger.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.ALL);

            // Create a file handler that writes log record to a file called application.log
            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            // Add the file handler to the logger
            LOGGER.addHandler(fileHandler);

            // Add a console handler to also log to the console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(consoleHandler);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize logger handler.", e);
        }
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logWarning(String message) {
        LOGGER.warning(message);
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}