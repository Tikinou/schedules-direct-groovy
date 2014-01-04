package com.tikinou.schedulesdirect

import com.tikinou.schedulesdirect.commands.AddHeadendCommandImpl
import com.tikinou.schedulesdirect.commands.DeleteHeadendCommandImpl
import com.tikinou.schedulesdirect.commands.DeleteMessageCommandImpl
import com.tikinou.schedulesdirect.commands.GetHeadendsCommandImpl
import com.tikinou.schedulesdirect.commands.GetLineupsCommandImpl
import com.tikinou.schedulesdirect.commands.GetProgramsCommandImpl
import com.tikinou.schedulesdirect.commands.GetSchedulesCommandImpl
import com.tikinou.schedulesdirect.commands.GetStatusCommandImpl
import com.tikinou.schedulesdirect.commands.RandHashCommandImpl
import com.tikinou.schedulesdirect.commands.UpdateMetadataCommandImpl
import com.tikinou.schedulesdirect.core.AbstractSchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.AddHeadendCommand
import com.tikinou.schedulesdirect.core.commands.headend.DeleteHeadendCommand
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetLineupsCommand
import com.tikinou.schedulesdirect.core.commands.message.DeleteMessageCommand
import com.tikinou.schedulesdirect.core.commands.metadata.UpdateMetadataCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.randhash.RandHashCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommand
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommand

/**
 * @author Sebastien Astie.
 */
class SchedulesDirectClientImpl extends AbstractSchedulesDirectClient{
    @Override
    def <T extends com.tikinou.schedulesdirect.core.Command<?, ?>> T createCommand(Class<T> commandClass) {
        switch (commandClass){
            case AddHeadendCommand.class:
                return (T)new AddHeadendCommandImpl()
            case DeleteHeadendCommand.class:
                return (T)new DeleteHeadendCommandImpl()
            case DeleteMessageCommand:
                return (T)new DeleteMessageCommandImpl()
            case GetHeadendsCommand.class:
                return (T)new GetHeadendsCommandImpl()
            case GetLineupsCommand.class:
                return (T)new GetLineupsCommandImpl()
            case GetProgramsCommand.class:
                return (T)new GetProgramsCommandImpl()
            case GetSchedulesCommand.class:
                return (T)new GetSchedulesCommandImpl()
            case GetStatusCommand.class:
                return (T)new GetStatusCommandImpl()
            case RandHashCommand.class:
                return (T)new RandHashCommandImpl()
            case UpdateMetadataCommand.class:
                return (T)new UpdateMetadataCommandImpl()
        }
        return null
    }
}
