package exception;

/**
 * Thrown when we try to add a product whose ID or name already exists. Basically catching
 * duplicate names and id
 */
public class DuplicateProductException extends ValidationException {

    private static final long serialVersionUID = 1;

    public DuplicateProductException(String message) {
        super(message);
    }
}
