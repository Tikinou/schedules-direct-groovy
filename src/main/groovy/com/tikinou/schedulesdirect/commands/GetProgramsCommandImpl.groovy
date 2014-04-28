package com.tikinou.schedulesdirect.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.ClientUtils
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.program.AbstractGetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommandResult
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.program.ProgramSD
import com.tikinou.schedulesdirect.core.exceptions.ValidationException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovy.util.logging.Commons

/**
 * @author Sebastien Astie.
 */
@Commons
class GetProgramsCommandImpl extends AbstractGetProgramsCommand{
    @Override
    public void execute(SchedulesDirectClient client) {
        ClientUtils clientUtils = ClientUtils.instance
        try{
            clientUtils.failIfUnauthenticated(client.credentials)
            status = CommandStatus.RUNNING
            validateParameters()
            def rawResponseData = clientUtils.executeRequest(client,this, GetProgramsCommandResult.class, true)
            ObjectMapper objectMapper = ModuleRegistration.instance.configuredObjectMapper;
            if (rawResponseData instanceof InputStream){
                def programs = []
                ((InputStream)rawResponseData).withReader { reader ->
                    programs.add(objectMapper.readValue(reader.readLine(), ProgramSD.class))
                }
                results = new GetProgramsCommandResult(programs: programs);
            } else
                results = objectMapper.readValue(rawResponseData, GetProgramsCommandResult.class)
        } catch (Exception e){
            log.error("Error while executing command.", e)
            status = CommandStatus.FAILURE
            results = new GetProgramsCommandResult(message: e.message)
        }
    }

    @Override
    public void validateParameters() throws ValidationException {
        assert parameters
        if (!parameters.programIds)
            throw new ValidationException("programIds parameter is required");
    }


}
