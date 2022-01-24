package io.buyan.dubbo.viewer;

/**
 * API 扫描异常
 *
 * @author Gan Pengyu
 * CreateDate 2022/1/24
 */
public class ApiScanException extends RuntimeException {

    public ApiScanException() {
    }

    public ApiScanException(String message) {
        super(message);
    }

    public ApiScanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiScanException(Throwable cause) {
        super(cause);
    }

    public ApiScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
