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
 *
 * @author: Sebastien Astie
 */

class Client {
    Client(SchedulesDirectApiVersion apiVersion){
        switch (apiVersion){
            case VERSION_20130709:
                CommandFactory.concreteFactory = new com.tikinou.schedulesdirect.v20130709.Factory()
                break
            default:
                throw new GroovyException("Unknown api version " + apiVersion)
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
            throw new GroovyException("Could not login to schedules direct. response was:" + cmd.results)
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
