package com.reckue.account.transfers;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Class ErrorTransfer allows to convert a response to a given response when it throws an exception.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@ApiModel
public class ErrorTransfer {

    @ApiModelProperty(notes = "Error message")
    private String message;

    @ApiModelProperty(notes = "Error status")
    private HttpStatus httpStatus;

    @ApiModelProperty(notes = "Error status code")
    private Integer status;

    public ErrorTransfer(int status, Map<String, Object> errorAttributes) {
        this.setStatus(status);
        this.setMessage((String) errorAttributes.get("message"));
    }

    public ErrorTransfer(String message, HttpStatus httpStatus, Integer status) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.status = status;
    }
}
