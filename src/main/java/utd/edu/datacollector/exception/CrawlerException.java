package utd.edu.datacollector.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CrawlerException extends RuntimeException{
    private final String errorCode;
    private final Long configId;
    private final Severity severity;
    private final boolean retryable;

    public enum Severity {
         WARNING, ERROR, CRITICAL
    }
}
