/*
 * Copyright 2013 Tikinou LLC
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

package com.tikinou.schedulesdirect.v20131021

import com.tikinou.schedulesdirect.v20130709.*

import static com.tikinou.schedulesdirect.core.domain.ActionType.*
import static com.tikinou.schedulesdirect.core.domain.ObjectTypes.*
import static com.tikinou.schedulesdirect.core.domain.SchedulesDirectApiVersion.VERSION_20131021
/**
 * Factory for Commands running against version 20130709
 * @author Sebastien Astie
 */
class Factory {
    private final static def GET_VALID = [HEADENDS, LINEUPS, PROGRAMS, RANDHASH, SCHEDULES, STATUS]
    private final static def ADD_VALID = [HEADENDS]
    private final static def DELETE_VALID = [HEADENDS, MESSAGE]
    private final static def UPDATE_VALID = [METADATA]

    private final static def DEFAUlT_BASE_URL = "https://data2.schedulesdirect.org/"
    private final static def DEFAULT_ENDPOINT = "handleRequest.php"

    def static getDefaultBaseUrl() { DEFAUlT_BASE_URL }

    def static getDefaultEndpoint() { DEFAULT_ENDPOINT }

    static def getCommand(actionType, objectType) {
        switch (actionType) {
            case ADD:
                if (!ADD_VALID.contains(objectType))
                    return null
                break
            case DELETE:
                if (!DELETE_VALID.contains(objectType))
                    return null
                break
            case GET:
                if (!GET_VALID.contains(objectType))
                    return null
                break;
            case UPDATE:
                if (!UPDATE_VALID.contains(objectType))
                    return null
                break;
            default:
                // we don't recognize this actionType
                return null
        }
        // if we get here the verification is complete we can return the command
        return instantiateCommand(actionType, objectType)
    }

    private static def instantiateCommand(actionType, objectType) {
        switch (objectType) {
            case HEADENDS:
                return new HeadendsCommand(action: actionType, apiVersion: VERSION_20131021)
            case LINEUPS:
                return new LineupsCommand(action: actionType, apiVersion: VERSION_20131021)
            case MESSAGE:
                return new MessageCommand(action: actionType, apiVersion: VERSION_20131021)
            case METADATA:
                return new MetadataCommand(action: actionType, apiVersion: VERSION_20131021)
            case PROGRAMS:
                return new ProgramsCommand(action: actionType, apiVersion: VERSION_20131021)
            case RANDHASH:
                return new RandHashCommand(action: actionType, apiVersion: VERSION_20131021)
            case SCHEDULES:
                return new SchedulesCommand(action: actionType, apiVersion: VERSION_20131021)
            case STATUS:
                return new StatusCommand(action: actionType, apiVersion: VERSION_20131021)
            default:
                return null
        }
    }
}
