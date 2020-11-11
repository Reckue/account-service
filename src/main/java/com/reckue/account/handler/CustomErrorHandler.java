package com.reckue.account.handler;

import com.reckue.account.transfer.ErrorTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Class CustomErrorHandler allows to render errors.
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequiredArgsConstructor
public class CustomErrorHandler implements ErrorController {

    private final ErrorAttributes errorAttributes;

    /**
     * This method is used to send an error response to the client
     * using the specified HTTP status code and error attributes.
     *
     * @param request  generic web request interceptors
     * @param response the servlet container
     * @return an object of ErrorTransfer class
     */
    @RequestMapping("/error")
    @ResponseBody
    public ErrorTransfer error(WebRequest request, HttpServletResponse response) {
        return new ErrorTransfer(response.getStatus(), getErrorAttributes(request));
    }

    /**
     * This method is used to return the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * This method is used to provide access to error attributes
     * which can be logged or presented to the account.
     *
     * @param request generic web request interceptors
     * @return map of error attributes
     */
    private Map<String, Object> getErrorAttributes(WebRequest request) {
        return new HashMap<>(this.errorAttributes.getErrorAttributes(request, false));
    }
}
