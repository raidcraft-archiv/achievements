package de.raidcraft.achievements.api;

/**
 * @author mdoering
 */
public class AchievementException extends Exception {

    public AchievementException(String message) {

        super(message);
    }

    public AchievementException(String message, Throwable cause) {

        super(message, cause);
    }

    public AchievementException(Throwable cause) {

        super(cause);
    }

    public AchievementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
