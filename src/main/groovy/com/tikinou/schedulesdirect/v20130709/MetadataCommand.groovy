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
import com.tikinou.schedulesdirect.ObjectTypes
import com.tikinou.schedulesdirect.ValidationException
import groovy.json.JsonBuilder

/**
 * @author Sebastien Astie
 */
class MetadataCommand extends Command {
    @Override
    protected def prepareJsonRequestData(credentials) {
        failIfUnathenticated(credentials)
        validateParameters()
        def jsonRequest = new JsonBuilder()
        jsonRequest {
            request {
                source parameters.source
                comment parameters.comment
                suggested parameters.suggested
                current parameters.current_series_id
                prog_id parameters.prog_id
                field parameters.field
            }
            randhash credentials.randhash
            action action.name().toLowerCase()
            api apiVersion.value
            object ObjectTypes.METADATA.name().toLowerCase()
        }
        jsonRequest.toString()
    }

    @Override
    protected void validateParameters() {
        if (parameters.source == null)
            throw new ValidationException("source parameter is required")
        if (parameters.comment == null)
            throw new ValidationException("comment parameter is required")
        if (parameters.suggested == null)
            throw new ValidationException("suggested parameter is required")
        if (parameters.current_series_id == null)
            throw new ValidationException("current_series_id parameter is required")
        if (parameters.prog_id == null)
            throw new ValidationException("prog_id parameter is required")
        if (parameters.field == null)
            throw new ValidationException("field parameter is required")
    }
}
