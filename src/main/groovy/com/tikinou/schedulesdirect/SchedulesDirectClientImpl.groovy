package com.tikinou.schedulesdirect

import com.tikinou.schedulesdirect.commands.*
import com.tikinou.schedulesdirect.core.AbstractSchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractAddLineupCommand
import com.tikinou.schedulesdirect.core.commands.lineup.AbstractDeleteLineupCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetLineupDetailsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetSubscribedLineupsCommand
import com.tikinou.schedulesdirect.core.commands.message.DeleteMessageCommand
import com.tikinou.schedulesdirect.core.commands.metadata.UpdateMetadataCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommand
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommand
import com.tikinou.schedulesdirect.core.commands.token.TokenCommand

/**
 * @author Sebastien Astie.
 */
class SchedulesDirectClientImpl extends AbstractSchedulesDirectClient{
    @Override
    def <T extends com.tikinou.schedulesdirect.core.ParameterizedCommand<?, ?>> T createCommand(Class<T> commandClass) {
        switch (commandClass){
            case AbstractAddLineupCommand.class:
                return (T)new AddLineupCommandImpl()
            case AbstractDeleteLineupCommand.class:
                return (T)new DeleteLineupCommandImpl()
            case DeleteMessageCommand:
                return (T)new DeleteMessageCommandImpl()
            case GetHeadendsCommand.class:
                return (T)new GetHeadendsCommandImpl()
            case GetLineupDetailsCommand.class:
                return (T)new GetLineupDetailsCommandImpl()
            case GetProgramsCommand.class:
                return (T)new GetProgramsCommandImpl()
            case GetSchedulesCommand.class:
                return (T)new GetSchedulesCommandImpl()
            case GetSubscribedLineupsCommand.class:
                return (T)new GetSubscribedLineupsCommandImpl()
            case GetStatusCommand.class:
                return (T)new GetStatusCommandImpl()
            case TokenCommand.class:
                return (T)new TokenCommandImpl()
            case UpdateMetadataCommand.class:
                return (T)new UpdateMetadataCommandImpl()
        }
        return null
    }
}
