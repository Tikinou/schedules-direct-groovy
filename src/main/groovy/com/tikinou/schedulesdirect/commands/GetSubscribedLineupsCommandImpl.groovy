package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractGetSubscribedLineupsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetSubscribedLineupsResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException

/**
 * @author Sebastien Astie.
 */
@Commons
class GetSubscribedLineupsCommandImpl extends AbstractGetSubscribedLineupsCommand{
    @Override
    public void execute(SchedulesDirectClient client, int numRetries) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            while(numRetries >= 0) {
                try {
                    clientUtils.executeRequest(client,this, GetSubscribedLineupsResult.class)
                    break
                } catch (HttpResponseException ex) {
                    numRetries = clientUtils.retryConnection(client, parameters, ex, numRetries)
                }
            }
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetSubscribedLineupsResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
    }
}
