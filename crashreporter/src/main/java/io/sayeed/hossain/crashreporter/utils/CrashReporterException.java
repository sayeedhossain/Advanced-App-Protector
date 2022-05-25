

package io.sayeed.hossain.crashreporter.utils;


public class CrashReporterException extends RuntimeException {
    static final long serialVersionUID = 1;


    public CrashReporterException() {
        super();
    }


    public CrashReporterException(String message) {
        super(message);
    }


    public CrashReporterException(String format, Object... args) {
        this(String.format(format, args));
    }


    public CrashReporterException(String message, Throwable throwable) {
        super(message, throwable);
    }


    public CrashReporterException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString() {

        return getMessage();
    }
}
