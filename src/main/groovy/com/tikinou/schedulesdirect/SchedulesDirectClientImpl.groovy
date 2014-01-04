package com.tikinou.schedulesdirect

import com.tikinou.schedulesdirect.commands.RandHashCommandImpl
import com.tikinou.schedulesdirect.core.AbstractSchedulesDirectClient
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
        }
        return null
    }
}
