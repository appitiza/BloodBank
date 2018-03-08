package net.appitiza.android.lifedrop.app;

import android.util.Log;

import net.appitiza.android.lifedrop.config.Config;


/**
 * Utility class for log.
 * <p>
 * For utility class for logging.
 *
 * @author Zco
 */
public class AppLog {
    // Application Tag
    public static final String APP_TAG = "BLOOD";

    /**
     * Logs application debug messages.
     *
     * @param message The message to write to console.
     */
    public static int logString(String message) {
        return logString(APP_TAG, message);
    }

    /**
     * Logs application debug messages.
     *
     * @param message The error message to write to console.
     */
    public static int logErrorString(String message) {
        return logErrorString(APP_TAG, message);
    }

    /**
     * Logs application debug messages.
     *
     * @param tag     The log tag.
     * @param message The message to write to console.
     */
    public static int logString(String tag, String message) {
        int result = 0;

        // If log is enabled, the print the log
        if (!Config.isProduction) {
        result = Log.i(tag, " " + message);
        }

        return result;
    }

    /**
     * Logs application debug messages.
     *
     * @param tag     The log tag.
     * @param message The error message to write to console.
     */
    public static int logErrorString(String tag, String message) {
        int result = 0;

        // If log is enabled, the print the log
        if (!Config.isProduction) {
        result = Log.e(tag, " " + message);
        }

        return result;
    }
}
