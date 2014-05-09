package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractAddLineupCommand
import com.tikinou.schedulesdirect.core.commands.lineup.LineupCommandResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException

/**
 * @author Sebastien Astie.
 */
@Commons
class AddLineupCommandImpl extends AbstractAddLineupCommand {

    @Override
    void validateParameters() throws ValidationException {
        assert parameters
        if(!parameters.lineupId){
            throw new ValidationException("lineupId parameter is required");
        }
    }

    @Override
    public void execute(SchedulesDirectClient client, int numRetries) {
        ClientUtils clientUtils = ClientUtils.getInstance()
        try{
            clientUtils.failIfUnauthenticated(client.getCredentials())
            status = CommandStatus.RUNNING
            validateParameters()
            while(numRetries >= 0) {
                try {
                    clientUtils.executeRequest(client, this, LineupCommandResult.class)
                    break
                } catch (HttpResponseException ex) {
                    numRetries = clientUtils.retryConnection(client, parameters, ex, numRetries);
                }
            }
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new LineupCommandResult(message: e.message)
        }
    }

}
