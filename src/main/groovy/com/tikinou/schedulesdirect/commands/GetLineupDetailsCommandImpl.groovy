package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractGetLineupDetailsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetLineupDetailsResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons

/**
 * @author Sebastien Astie.
 */
@Commons
class GetLineupDetailsCommandImpl extends AbstractGetLineupDetailsCommand{
    @Override
    public void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            clientUtils.executeRequest(client,this, GetLineupDetailsResult.class)
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetLineupDetailsResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.lineupId){
            throw new ValidationException("lineupId parameter is required");
        }
    }
}
