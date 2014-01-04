package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.AbstractAddHeadendCommand
import com.tikinou.schedulesdirect.core.commands.headend.AddDeleteHeadendResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import org.joda.time.DateTime

/**
 * @author Sebastien Astie.
 */
@Commons
class AddHeadendCommandImpl extends AbstractAddHeadendCommand{

    @Override
    void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.headendId){
            throw new ValidationException("headendId parameter is required");
        }
    }

    @Override
    public void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.getInstance()
        try{
            clientUtils.failIfUnauthenticated(client.getCredentials())
            status = CommandStatus.RUNNING
            validateParameters()
            clientUtils.executeRequest(client, this, AddDeleteHeadendResult.class)
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new AddDeleteHeadendResult(message: e.message)
        }
    }

}
