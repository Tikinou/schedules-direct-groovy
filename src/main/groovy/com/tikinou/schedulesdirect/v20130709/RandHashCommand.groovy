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

package com.tikinou.schedulesdirect.v20130709

import com.tikinou.schedulesdirect.Command
import com.tikinou.schedulesdirect.CommandStatus
import com.tikinou.schedulesdirect.ObjectTypes
import com.tikinou.schedulesdirect.ResponseCode
import groovy.json.JsonBuilder
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime

import static ResponseCode.OK

/**
 * @author Sebastien Astie
 */
class RandHashCommand extends Command{
    //store a reference.
    private def credentials

    @Override
    protected def prepareJsonRequestData(credentials) {
        this.credentials = credentials
        def shaVal = DigestUtils.shaHex(credentials.password)
        def jsonRequest = new JsonBuilder()
        jsonRequest {
            request {
                username credentials.username
                password shaVal
            }
            action action.name().toLowerCase()
            api apiVersion.value
            object ObjectTypes.RANDHASH.name().toLowerCase()
        }
        jsonRequest.toString()
    }

    protected void processResult(resultData, success){
        if(resultData.code == OK.code){
            status = CommandStatus.SUCCESS
            credentials.randhash = resultData.randhash
            credentials.randhashDateTime = DateTime.now()
        } else {
            credentials.randhash = null
            credentials.randhashDateTime = null
            status = CommandStatus.FAILURE
            results = resultData
        }
    }
}
