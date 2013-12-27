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

/**
 * @author Sebastien Astie
 */
public enum SchedulesDirectApiVersion {
    VERSION_20130709("20130709"),
    VERSION_20131021("20131021")

    SchedulesDirectApiVersion(String v) { value = v }
    final String value

    static SchedulesDirectApiVersion fromValue(value){
        if(value){
            switch (value){
                case "20130709":
                    return VERSION_20130709
                case "20131021":
                    return VERSION_20131021
            }
        }
        return null
    }
}