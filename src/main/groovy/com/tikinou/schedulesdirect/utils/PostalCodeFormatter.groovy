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

package com.tikinou.schedulesdirect.utils

import com.tikinou.schedulesdirect.ValidationException
import org.codehaus.groovy.GroovyException

import static com.tikinou.schedulesdirect.utils.Country.Canada
import static com.tikinou.schedulesdirect.utils.Country.UnitedState

/**
 * @author Sebastien Astie
 */
class PostalCodeFormatter {
    static def format(Country country, String postalcode){
        def code = "PC:"
        switch(country){
            case Canada:
                if(postalcode.length() < 4)
                    throw new ValidationException("postal code for Canada must be at least 4 characters long")
                code << postalcode[0..4] // grab the 4 left most
                return code
            case UnitedState:
                if(postalcode.length() < 5)
                    throw new ValidationException("postal code for United States must be at least 5 characters long")
                code << postalcode[0..5]
                return code
            default:
                code << postalcode
        }
        return code
    }
}