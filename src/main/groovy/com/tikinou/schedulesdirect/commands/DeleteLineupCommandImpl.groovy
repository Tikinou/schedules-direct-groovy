package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractDeleteLineupCommand
import com.tikinou.schedulesdirect.core.commands.lineup.LineupCommandResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons

/**
 * @author Sebastien Astie.
 */
@Commons
class DeleteLineupCommandImpl extends AbstractDeleteLineupCommand{
    @Override
    void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.lineupId){
            throw new ValidationException("lineupId parameter is required");
        }
    }

    @Override
    public void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.getInstance()
        try{
            clientUtils.failIfUnauthenticated(client.getCredentials())
            status = CommandStatus.RUNNING
            validateParameters()
            clientUtils.executeRequest(client, this, LineupCommandResult.class)
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new LineupCommandResult(message: e.message)
        }
    }
}
