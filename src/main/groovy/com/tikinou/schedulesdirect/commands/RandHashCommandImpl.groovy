package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.randhash.AbstractRandhashCommand
import com.tikinou.schedulesdirect.core.commands.randhash.RandHashParameters
import com.tikinou.schedulesdirect.core.commands.randhash.RandHashResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.Credentials
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import groovyx.net.http.RESTClient
import org.joda.time.DateTime

import static com.tikinou.schedulesdirect.core.domain.CommandStatus.FAILURE
import static com.tikinou.schedulesdirect.core.domain.CommandStatus.RUNNING
import static com.tikinou.schedulesdirect.core.domain.CommandStatus.SUCCESS

/**
 * @author Sebastien Astie.
 */
@Commons
class RandHashCommandImpl extends AbstractRandhashCommand{
    @Override
    void execute(SchedulesDirectClient client) {
        try{
            status = RUNNING
            validateParameters()
            ClientUtils.executeRequest(client, this, RandHashResult.class)
            if(status == SUCCESS){
                parameters.credentials.randhash = result.randhash
                parameters.credentials.randhashDateTime = DateTime.now()
            }
        } catch (Exception e){
            log.error("Error while executing command.", e);
            status = FAILURE;
            results = new RandHashResult(message: e.message)
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
