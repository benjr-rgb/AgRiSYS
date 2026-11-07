package exception;

/**
 * Thrown when we try to add a product whose ID or name already exists.
 *
 * It extends ValidationException (inheritance!), so a duplicate is a special
 * kind of validation problem. That means a "catch (ValidationException e)"
 * will also catch this one, but you can still catch it separately if you want
 * to show a special message.
 */
public class DuplicateProductException extends ValidationException {

    // Java asks serializable classes (all exceptions) to have this number. Just ignore it.
    private static final long serialVersionUID = 1L;

    public DuplicateProductException(String message) {
        super(message);
    }
}
