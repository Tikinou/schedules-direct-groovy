/*
 * Copyright (c) 2013 Tikinou LLC
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

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.Method

/**
 * @author: Sebastien Astie
 */
abstract class Command {
    ActionType action
    SchedulesDirectApiVersion apiVersion

    def parameters
    def status = CommandStatus.NONE
    def results

    void execute(client) {
        status = CommandStatus.RUNNING
        def jsonRequest = prepareJsonRequestData(client.credentials)
        def postBody = "request=" + URLEncoder.encode(jsonRequest.toString(), "UTF-8")
        // this is a post request setup
        client.httpBuilder.request(Method.POST) {
            uri.path = client.endpoint
            requestContentType = ContentType.URLENC
            body = postBody
            response.success = { resp, json ->
                def slurp = new JsonSlurper()
                results = slurp.parseText(json.text())
                if(results.response == "OK")
                    status = CommandStatus.SUCCESS
                else
                    status = CommandStatus.FAILURE
            }
            response.failure = { resp ->
                status = CommandStatus.FAILURE
                results = resp.text()
            }

        }

    }

    protected abstract def prepareJsonRequestData(credentials)
}
