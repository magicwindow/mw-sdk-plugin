package cn.magicwindow.sdk.plugin.exception;

/**
 * Created by tony on 16/8/1.
 */
public class MWPluginException extends RuntimeException {

    private static final long serialVersionUID = 1597941340635998976L;

    public MWPluginException() {
        super();
    }

    public MWPluginException(String message) {
        super(message);
    }
}
