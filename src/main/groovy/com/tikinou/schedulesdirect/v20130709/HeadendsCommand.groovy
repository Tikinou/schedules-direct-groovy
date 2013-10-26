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

import com.tikinou.schedulesdirect.ActionType
import com.tikinou.schedulesdirect.Command
import com.tikinou.schedulesdirect.ObjectTypes
import com.tikinou.schedulesdirect.ValidationException
import com.tikinou.schedulesdirect.utils.PostalCodeFormatter
import groovy.json.JsonBuilder

/**
 * @author Sebastien Astie
 */
class HeadendsCommand extends Command{
    @Override
    protected def prepareJsonRequestData(credentials) {
        failIfUnathenticated(credentials)
        validateParameters()
        switch(action){
            case ActionType.GET:
                return prepareJsonForGetAction(credentials)
            case ActionType.ADD:
            case ActionType.DELETE:
                return prepareJsonForAddDeleteAction(credentials)
        }
        return null
    }

    @Override
    protected void validateParameters() {
        switch(action){
            case ActionType.GET:
                if(parameters.country == null)
                    throw new ValidationException("country parameter is required")
                if(parameters.postalCode == null)
                    throw new ValidationException("postalCode parameter is required")
            case ActionType.ADD:
            case ActionType.DELETE:
                if(parameters.headendId == null)
                    throw new ValidationException("headendId parameter is required")
            default:
                break;
        }
    }

    private prepareJsonForGetAction(credentials){
        def jsonRequest = new JsonBuilder()
        jsonRequest {
            request{
                country parameters.country.code
                postalcode PostalCodeFormatter.format(parameters.postalCode)
            }
            randhash credentials.randhash
            action action.name().toLowerCase()
            api apiVersion.value
            object ObjectTypes.HEADENDS.name().toLowerCase()
        }
        jsonRequest.toString()
    }

    private prepareJsonForAddDeleteAction(credentials){
        def jsonRequest = new JsonBuilder()
        jsonRequest {
            request parameters.headendId
            randhash credentials.randhash
            action action.name().toLowerCase()
            api apiVersion.value
            object ObjectTypes.HEADENDS.name().toLowerCase()
        }
        jsonRequest.toString()
    }
}
