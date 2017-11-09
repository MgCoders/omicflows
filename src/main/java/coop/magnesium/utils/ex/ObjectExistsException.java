package coop.magnesium.utils.ex;

/**
 * Created by rsperoni on 09/11/17.
 */
public class ObjectExistsException extends Exception {
    public ObjectExistsException() {
        super();
    }

    public ObjectExistsException(String message) {
        super(message);
    }

    public ObjectExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectExistsException(Throwable cause) {
        super(cause);
    }

    protected ObjectExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
