package se.salt.game.http.exception;

public class DeadlinePassedException extends RuntimeException {
    public DeadlinePassedException(String sessionId) {
        super("Deadline has passed for game for session " + sessionId);
    }
}
