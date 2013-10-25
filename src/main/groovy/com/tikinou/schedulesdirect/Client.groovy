/*
 * Copyright (c) 2013 TIKINOU LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tikinou.schedulesdirect

import groovyx.net.http.HTTPBuilder
import org.codehaus.groovy.GroovyException

import static com.tikinou.schedulesdirect.SchedulesDirectApiVersion.VERSION_20130709

/**
 * Client class that handles the communication with Schedules Direct JSON API Server.
 * The communication is done via http, the information is processed using JSON
 * This is the main entry point to deal with Schedules Direct.
 * The flow should be the following.
 * -> Instantiate the client by passing it the schedules direct JSON API version.
 *      -> If the version is not supported it will throw a VersionNotSupportedException
 * -> Create a Credentials instance and provide it you username / pasword for schedules direct.
 * -> Call the connect method providing the credentials instance.
 *      -> This will throw an AuthenticationException if the client cannot authenticate;
 *          response data will be in the responseData member.
 * -> Call the getCommandMethod() with action type GET and object type status.(this will create a StatusCommand instance)
 * -> Call the execute() method by passing it the status command previously create.
 *      -> upon execution check the command status property for success or failure.
 *      -> the command property result data will contain the parsed json response.
 * @author: Sebastien Astie
 */

class Client {
    Client(SchedulesDirectApiVersion apiVersion){
        switch (apiVersion){
            case VERSION_20130709:
                CommandFactory.concreteFactory = new com.tikinou.schedulesdirect.v20130709.Factory()
                break
            default:
                throw new VersionNotSupportedException("Unknown api version " + apiVersion)
        }
    }

    String baseUrl
    String endpoint
    HTTPBuilder httpBuilder
    Credentials credentials

    def connect(Credentials credentials){
        this.credentials = credentials
        initializeConnectivityData()
        Command cmd = getCommand(ActionType.GET, ObjectTypes.RANDHASH)
        execute(cmd)
        if(cmd.status != CommandStatus.SUCCESS)
            throw new AuthenticationException("Could not login to schedules direct", cmd.results)
    }

    void execute(Command command){
        command.execute(this)
    }

    def getCommand(actionType, objectType){
        return CommandFactory.getCommand(actionType, objectType)
    }

    private void initializeConnectivityData() {
        if(baseUrl == null)
            baseUrl = CommandFactory.getDefaultBaseUrl()
        if(endpoint == null)
            endpoint = CommandFactory.getDefaultEndpoint()
        if(httpBuilder == null)
            httpBuilder = new HTTPBuilder(baseUrl)
    }
}
