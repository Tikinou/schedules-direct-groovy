package com.tikinou.schedulesdirect.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.image.AbstractGetImageCommand
import com.tikinou.schedulesdirect.core.commands.image.GetImageResult
import com.tikinou.schedulesdirect.core.commands.program.AbstractGetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommandResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.program.ProgramSD
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus

/**
 * @author Sebastien Astie.
 */
@Commons
class GetImageCommandImpl extends AbstractGetImageCommand{
    @Override
    public void execute(SchedulesDirectClient client, int numRetries) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            while(numRetries >= 0) {
                try {
                    def rawResponseData = clientUtils.executeRequest(client, this, GetImageResult.class, true)
                    if (rawResponseData instanceof InputStream)
                        results = new GetImageResult(image: ((InputStream) rawResponseData).bytes)
                    break
                } catch (HttpResponseException ex) {
                    numRetries = clientUtils.retryConnection(client, parameters, ex, numRetries)
                }
            }
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetImageResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if (!parameters.imageUri)
            throw new ValidationException("imageUri parameter is required");
    }


}
