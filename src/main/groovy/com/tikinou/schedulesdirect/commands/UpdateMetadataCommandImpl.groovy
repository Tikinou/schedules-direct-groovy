package com.tikinou.schedulesdirect.commands

import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.BaseCommandResult
import com.tikinou.schedulesdirect.core.commands.metadata.AbstractUpdateMetadataCommand
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import groovyx.net.http.HttpResponseException

/**
 * @author Sebastien Astie.
 */
class UpdateMetadataCommandImpl extends AbstractUpdateMetadataCommand{
    @Override
    public void execute(SchedulesDirectClient client, int numRetries) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            while(numRetries >= 0) {
                try {
                    clientUtils.executeRequest(client,this, BaseCommandResult.class)
                    break
                } catch (HttpResponseException ex) {
                    numRetries = clientUtils.retryConnection(client, parameters, ex, numRetries)
                }
            }
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new BaseCommandResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if (!parameters.source)
            throw new ValidationException("source parameter is required");
        if (!parameters.comment)
            throw new ValidationException("comment parameter is required");
        if (!parameters.suggested)
            throw new ValidationException("suggested parameter is required");
        if (!parameters.current)
            throw new ValidationException("current series id parameter is required");
        if (!parameters.programId)
            throw new ValidationException("programId parameter is required");
        if (!parameters.field)
            throw new ValidationException("field parameter is required");
    }
}

