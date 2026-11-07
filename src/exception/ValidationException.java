package exception;

/**
 * ValidationException is thrown when the user (or our own code) gives
 * something that breaks a rule, for example:
 *   - an empty name
 *   - a price that is 0 or negative
 *   - restocking a negative amount
 *   - a Clerk trying to do a Manager-only action
 *
 * It "extends Exception", which makes it a CHECKED exception: any method that
 * can throw it must say "throws ValidationException", and whoever calls that
 * method must either catch it (try/catch) or pass it along.
 */
public class ValidationException extends Exception {

    // Java asks serializable classes (all exceptions) to have this number. Just ignore it.
    private static final long serialVersionUID = 1L;

    // The message explains what went wrong. It is what we show to the user.
    public ValidationException(String message) {
        super(message); // pass the message to the parent class (Exception)
    }
}
