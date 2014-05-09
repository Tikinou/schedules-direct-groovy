package com.tikinou.schedulesdirect.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.schedules.AbstractGetSchedulesCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommandResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.schedule.ScheduleSD
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovy.util.logging.Commons
import groovyx.net.http.HttpResponseException

/**
 * @author Sebastien Astie.
 */
@Commons
class GetSchedulesCommandImpl extends AbstractGetSchedulesCommand{
    @Override
    public void execute(SchedulesDirectClient client, int numRetries) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            while(numRetries >= 0) {
                try {
                    coreExecution(clientUtils, client)
                    break
                } catch (HttpResponseException ex) {
                    numRetries = clientUtils.retryConnection(client, parameters, ex, numRetries)
                }
            }
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetSchedulesCommandResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if (!parameters.stationIds)
            throw new ValidationException("stationIds parameter is required");
    }

    private void coreExecution(ClientUtils clientUtils, SchedulesDirectClient client){
        def rawResponseData = clientUtils.executeRequest(client,this, GetSchedulesCommandResult.class, true)
        ObjectMapper objectMapper = ModuleRegistration.instance.configuredObjectMapper;
        if (rawResponseData instanceof InputStream){
            def schedules = []
            ((InputStream)rawResponseData).withReader { reader ->
                programs.add(objectMapper.readValue(reader.readLine(), ScheduleSD.class))
            }
            results = new GetSchedulesCommandResult(schedules: schedules);
        } else
            results = objectMapper.readValue(rawResponseData, GetSchedulesCommandResult.class)
    }
}
