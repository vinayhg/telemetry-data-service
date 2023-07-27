/**
* (C) Koninklijke Philips Electronics N.V. 2021
*
* All rights are reserved. Reproduction or transmission in whole or in part,
* in  any form or by any means, electronic, mechanical or otherwise, is
* prohibited without the prior written permission of the copyright owner.
* 
* @author Saumya Mahajan
* 
*/
package com.philips.ps.prs.telemetrydata.exceptionhandler;

import com.philips.ps.prs.common.core.dto.outcome.CodeableConcept;
import com.philips.ps.prs.common.core.dto.outcome.Issue;
import com.philips.ps.prs.common.core.dto.outcome.OperationOutcome;
import com.philips.ps.prs.common.core.exception.BaseApplicationException;
import com.philips.ps.prs.common.core.messages.BaseErrorMessages;

import org.apache.log4j.Logger;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory for creating OperationOutcome objects.
 */
public class OperationOutcomeFactory {
    private static final Logger LOGGER = Logger.getLogger(OperationOutcomeFactory.class);

    /**
     * Instantiates a new operation outcome factory.
     */
    private OperationOutcomeFactory() {
        throw new IllegalAccessError("Illegal Access");
    }

    /**
     * Gets the validation error outcome.
     *
     * @param issues the issues
     * @return the validation error outcome
     */
    public static OperationOutcome getValidationErrorOutcome(List<Issue> issues) {
        OperationOutcome outcome = new OperationOutcome();
        outcome.setIssues(issues);
        return outcome;
    }

    /**
     * Gets the base application exception outcome.
     *
     * @param ex the ex
     * @return the base application exception outcome
     */
    public static OperationOutcome getBaseApplicationExceptionOutcome(BaseApplicationException ex) {
        Issue issue = Issue.requestError(ex.getLocalizedMessage(), ex.getCode());
        OperationOutcome outcome = new OperationOutcome();
        ArrayList<Issue> issues = new ArrayList<>();
        issues.add(issue);
        outcome.setIssues(issues);
        return outcome;
    }

    /**
     * Gets the unexpected error outcome.
     *
     * @param ex   the ex
     * @param code the code
     * @return the unexpected error outcome
     */
    public static OperationOutcome getUnexpectedErrorOutcome(Exception ex, String code) {
        Issue issue = new Issue();
        issue.setCode(Issue.CodeEnum.INVALID);
        issue.setSeverity(Issue.SeverityEnum.ERROR);
        issue.setDetails(new CodeableConcept(ex.getLocalizedMessage(), code));
        OperationOutcome outcome = new OperationOutcome();
        ArrayList<Issue> issues = new ArrayList<>();
        issues.add(issue);
        outcome.setIssues(issues);
        return outcome;
    }

    /**
     * Gets the exception outcome.
     *
     * @param ex the ex
     * @return the exception outcome
     */
    public static OperationOutcome getExceptionOutcome(Exception ex) {
        Issue issue = new Issue();
        issue.setCode(Issue.CodeEnum.INVALID);
        issue.setSeverity(Issue.SeverityEnum.ERROR);
        switch (ex.getClass().getSimpleName()) {
        case "HttpMediaTypeNotSupportedException":
            issue.setDetails(new CodeableConcept(BaseErrorMessages.MSG_INVALID_MEDIATYPE,
                                            BaseErrorMessages.COD_INVALID_MEDIATYPE));
            break;
        case "HttpRequestMethodNotSupportedException":
            issue.setDetails(new CodeableConcept(BaseErrorMessages.MSG_INVALID_OPERATION,
                                            BaseErrorMessages.COD_INVALID_OPERATION));
            break;
        case "NoHandlerFoundException":
            issue.setDetails(new CodeableConcept(BaseErrorMessages.MSG_UNROUTEABLE_REQUEST,
                                            BaseErrorMessages.COD_UNROUTEABLE_REQUEST));
            break;
        case "HttpMessageNotReadableException":
            issue.setDetails(new CodeableConcept(BaseErrorMessages.MSG_INVALID_REQUESTFORMAT,
                                            BaseErrorMessages.COD_INVALID_REQUESTFORMAT));
            break;
        case "MethodArgumentNotValidException":
            final MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
                if (error.getDefaultMessage() != null) {
                    issue.setDetails(new CodeableConcept(error.getDefaultMessage(),
                                                    BaseErrorMessages.COD_INVALID_REQUESTFORMAT));
                }
            });
            break;
        default:
            LOGGER.error(ex.getLocalizedMessage());
            issue.setDetails(new CodeableConcept(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
                                            BaseErrorMessages.COD_ERROR_INTERNALSERVER));
            break;
        }
        OperationOutcome outcome = new OperationOutcome();
        ArrayList<Issue> issues = new ArrayList<>();
        issues.add(issue);
        outcome.setIssues(issues);
        return outcome;
    }
}