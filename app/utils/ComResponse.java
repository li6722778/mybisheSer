package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author woderchen
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComResponse<T> {

    public final static int STATUS_OK = 0;

    public final static int STATUS_FAIL = 1;

    public final static int STATUS_TIMEOUT = 2;

    private int responseStatus;

    private String errorMessage;

    private T responseEntity;

    private String extendResponseContext;

    /**
     * @return the responseStatus
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * @param responseStatus the responseStatus to set
     */
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the responseEntity
     */
    public T getResponseEntity() {
        return responseEntity;
    }

    /**
     * @param responseEntity the responseEntity to set
     */
    public void setResponseEntity(T responseEntity) {
        this.responseEntity = responseEntity;
    }

    /**
     * @return the extendResponseContext
     */
    public String getExtendResponseContext() {
        return extendResponseContext;
    }

    /**
     * @param extendResponseContext the extendResponseContext to set
     */
    public void setExtendResponseContext(String extendResponseContext) {
        this.extendResponseContext = extendResponseContext;
    }

}
