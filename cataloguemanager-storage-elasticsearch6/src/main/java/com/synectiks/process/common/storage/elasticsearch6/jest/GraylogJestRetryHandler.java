/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.jest;

import com.google.common.base.Preconditions;
import io.searchbox.client.JestRetryHandler;
import org.apache.http.ConnectionClosedException;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

public class GraylogJestRetryHandler implements JestRetryHandler<HttpUriRequest> {
    private static final Logger log = LoggerFactory.getLogger(GraylogJestRetryHandler.class);

    private final int retryCount;
    private final Collection<Class<? extends Exception>> exceptionClasses = Arrays.asList(
        UnknownHostException.class,
        SocketException.class,
        ConnectionClosedException.class,
        SSLException.class);

    public GraylogJestRetryHandler(int retryCount) {
        Preconditions.checkArgument(retryCount >= 0, "retryCount must be positive");
        this.retryCount = retryCount;
    }

    @Override
    public boolean retryRequest(Exception exception, int executionCount, HttpUriRequest request) {
        if (executionCount >= retryCount) {
            log.debug("Maximum number of retries ({}) for request {} reached (executed {} times) (Reason: {})",
                retryCount, request, executionCount, exception.getMessage());
            return false;
        } else {
            for (Class<? extends Exception> exceptionClass : exceptionClasses) {
                if (exceptionClass.isInstance(exception)) {
                    log.debug("Retrying request {} (Reason: {})", request, exception.getMessage());
                    return true;
                }
            }

            log.debug("Not retrying request {} due to unsupported exception (Reason: {})", request, exception.getMessage());
            return false;
        }
    }
}
