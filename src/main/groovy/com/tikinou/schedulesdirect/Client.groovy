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

import groovy.util.logging.Commons
import groovyx.net.http.RESTClient

import static com.tikinou.schedulesdirect.SchedulesDirectApiVersion.VERSION_20130709
import static com.tikinou.schedulesdirect.SchedulesDirectApiVersion.VERSION_20131021
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
 * @author Sebastien Astie
 */

@Commons
class Client {
    static final int CREDENTIALS_EXPIRY_HOURS = 12

    Client(SchedulesDirectApiVersion apiVersion) {
        log.debug("Trying to use api version ${apiVersion.value}")
        switch (apiVersion) {
            case VERSION_20130709:
            case VERSION_20131021:
                CommandFactory.concreteFactory = new com.tikinou.schedulesdirect.v20130709.Factory()
                break
            default:
                log.error("Unknown api version provided: ${apiVersion}")
                throw new VersionNotSupportedException("Unknown api version provided: ${apiVersion}")
        }
        log.debug("Using api version ${apiVersion.value}")
    }

    String baseUrl
    String endpoint
    RESTClient restClient
    Credentials credentials

    void connect(Credentials credentials, forceConnect = false) {
        if (credentials == null)
            throw AuthenticationException("credentials object cannot be null")
        initializeConnectivityData()
        if(!forceConnect){
            if (this.credentials != null) {
                //are these the same credentials ?
                if (this.credentials.sameUserNamePassword(credentials)) {
                    // is the randhash older than 12 hours ?
                    if (!this.credentials.isOlderThan(CREDENTIALS_EXPIRY_HOURS)) {
                        log.info("credentials less than ${CREDENTIALS_EXPIRY_HOURS} hours. No need to get a new randhash")
                        return;
                    }
                }
            } else if(!credentials.isOlderThan(CREDENTIALS_EXPIRY_HOURS)){
                this.credentials = credentials
                return
            }
        }
        // if we got here we need to get a new randhash
        this.credentials = credentials
        Command cmd = getCommand(ActionType.GET, ObjectTypes.RANDHASH)
        execute(cmd)
        if (cmd.status != CommandStatus.SUCCESS)
            throw new AuthenticationException("Could not login to schedules direct", cmd.results)
    }

    void execute(Command command) {
        command.execute(this)
    }

    def getCommand(actionType, objectType) {
        return CommandFactory.getCommand(actionType, objectType)
    }

    private void initializeConnectivityData() {
        if (baseUrl == null)
            baseUrl = CommandFactory.getDefaultBaseUrl()
        if (endpoint == null)
            endpoint = CommandFactory.getDefaultEndpoint()
        if (restClient == null)
            restClient = new RESTClient(baseUrl)
    }
}
