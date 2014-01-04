package com.tikinou.schedulesdirect

import com.tikinou.schedulesdirect.commands.AddHeadendCommandImpl
import com.tikinou.schedulesdirect.commands.DeleteHeadendCommandImpl
import com.tikinou.schedulesdirect.commands.DeleteMessageCommandImpl
import com.tikinou.schedulesdirect.commands.RandHashCommandImpl
import com.tikinou.schedulesdirect.core.AbstractSchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.AddHeadendCommand
import com.tikinou.schedulesdirect.core.commands.message.DeleteMessageCommand
import com.tikinou.schedulesdirect.core.commands.randhash.RandHashCommand

/**
 * @author Sebastien Astie.
 */
class SchedulesDirectClientImpl extends AbstractSchedulesDirectClient{
    @Override
    def <T extends com.tikinou.schedulesdirect.core.Command<?, ?>> T createCommand(Class<T> commandClass) {
        switch (commandClass){
            case RandHashCommand.class:
                return (T)new RandHashCommandImpl()
            case AddHeadendCommand.class:
                return (T)new AddHeadendCommandImpl()
            case DeleteMessageCommand.class:
                return (T)new DeleteHeadendCommandImpl()
            case DeleteMessageCommand:
                return (T)new DeleteMessageCommandImpl()
        }
        return null
    }
}
