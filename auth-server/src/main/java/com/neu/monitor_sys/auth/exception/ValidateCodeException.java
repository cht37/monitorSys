package com.neu.monitor_sys.auth.exception;

import javax.naming.AuthenticationException;
import java.io.Serial;

public class ValidateCodeException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 2672899097153524723L;

    public ValidateCodeException(String explanation) {
        super(explanation);
    }
}
