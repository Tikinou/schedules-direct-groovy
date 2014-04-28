package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.AbstractGetHeadendsCommand
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.postalcode.DefaultPostalCodeFormatter
import com.tikinou.schedulesdirect.core.domain.postalcode.PostalCodeFormatter
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException

/**
 * @author Sebastien Astie.
 */
@Commons
class GetHeadendsCommandImpl extends AbstractGetHeadendsCommand{
    private static PostalCodeFormatter POSTAL_CODE_FORMATTER = new DefaultPostalCodeFormatter()

    @Override
    void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.getInstance()
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            clientUtils.executeRequest(client,this, GetHeadendsResult.class)
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetHeadendsResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.country)
            throw new ValidationException("country parameter is required")
        if(!parameters.postalCode)
            throw new ValidationException("postalCode parameter is required")
        parameters.postalCode = POSTAL_CODE_FORMATTER.format(parameters.country, parameters.postalCode)
    }
}
