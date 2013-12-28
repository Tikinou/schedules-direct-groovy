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

import com.tikinou.schedulesdirect.core.domain.ActionType
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.ResponseCode
import com.tikinou.schedulesdirect.core.domain.SchedulesDirectApiVersion
import com.tikinou.schedulesdirect.core.exceptions.AuthenticationException
import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
/**
 * @author Sebastien Astie
 */
abstract class Command {
    ActionType action
    SchedulesDirectApiVersion apiVersion

    def parameters = [:]
    def status = CommandStatus.NONE
    def results

    void execute(client) {
        status = CommandStatus.RUNNING
        def jsonRequest = prepareJsonRequestData(client.credentials)
        def postBody = "request=" + URLEncoder.encode(jsonRequest, "UTF-8")
        // this is a post request setup
        def response = client.restClient.post(path: client.endpoint,
                requestContentType: ContentType.URLENC,
                body: postBody)
        handleResponse(response)
    }

    protected abstract def prepareJsonRequestData(credentials)


    protected void processResult(resultData, success) {
        if (resultData.code == ResponseCode.OK.code) {
            status = CommandStatus.SUCCESS
            results = resultData
        } else {
            status = CommandStatus.FAILURE
            results = resultData
        }
    }

    protected void validateParameters() {}

    protected void failIfUnathenticated(credentials) {
        if (credentials.randhash == null)
            throw new AuthenticationException("Not authenticated")
    }

    protected void handleResponse(response) {
        if (response.status == 200) {
            def slurp = new JsonSlurper()
            if (response.data != null) {
                def res = null
                if (response.data instanceof InputStream) {
                    res = slurp.parse(new InputStreamReader(response.data))
                } else {
                    res = slurp.parseText(response.data.text())
                }
                processResult(res, true)
            }
        } else {
            status = CommandStatus.FAILURE
            processResult(resp.text(), false)
        }
    }
}
