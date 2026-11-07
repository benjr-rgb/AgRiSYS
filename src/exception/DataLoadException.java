package exception;

/**
 * Thrown when a data file cannot be read (or written), for example when the
 * file is locked, the folder is missing, or the disk is full.
 * We use the same exception for saving, to keep the number of classes small.
 */
public class DataLoadException extends Exception {

    // Java asks serializable classes (all exceptions) to have this number. Just ignore it.
    private static final long serialVersionUID = 1L;

    public DataLoadException(String message) {
        super(message);
    }
}
