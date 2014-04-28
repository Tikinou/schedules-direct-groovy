package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.token.AbstractTokenCommand
import com.tikinou.schedulesdirect.core.commands.token.TokenResult
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException
import org.joda.time.DateTime

import static com.tikinou.schedulesdirect.core.domain.CommandStatus.*

/**
 * @author Sebastien Astie.
 */
@Commons
class TokenCommandImpl extends AbstractTokenCommand{
    @Override
    void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            status = RUNNING
            validateParameters()
            clientUtils.executeRequest(client, this, TokenResult.class)
            if(status == SUCCESS){
                parameters.credentials.token = results.token
                parameters.credentials.tokenDateTime = DateTime.now()
            }
        } catch (Exception e){
            log.error("Error while executing command.", e);
            status = FAILURE;
            results = new TokenResult(message: e.message)
        }
    }

    @Override
    void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.credentials?.username)
            throw new ValidationException("username must be provided.");
        if(!parameters.credentials?.clearPassword)
            throw new ValidationException("password must be provided");
    }
}
