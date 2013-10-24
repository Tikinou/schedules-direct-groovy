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

package com.tikinou.schedulesdirect

import groovy.json.JsonSlurper
import org.codehaus.groovy.GroovyException
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Created by sebastien on 10/24/13.
 */
class ClientTest {
    void setUp() {
        super.setUp()
    }

    void tearDown() {
    }

    @Test(expected = GroovyException.class)
    void testUnknownVersion() {
        new Client(null)
    }

    @Test
    void testConnect() {
        def slurper = new JsonSlurper()
        def config = slurper.parseText( ClientTest.class.getResource( '/credentials.json' ).text )
        Credentials credentials = new Credentials(username:config.username, password:config.password)
        assert credentials.username != "CHANGE_USER_NAME"
        assert credentials.password != "CHANGE_PASSWORD"
        Client client = new Client(SchedulesDirectApiVersion.VERSION_20130709)
        assert credentials.randhash == null
        client.connect(credentials)
        assert credentials.randhash != null
    }

    @Test
    void testGetCommand() {
        Client client = new Client(SchedulesDirectApiVersion.VERSION_20130709)
        assert client.getCommand(ActionType.ADD, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.DELETE, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.GET, ObjectTypes.RANDHASH) != null
        assert client.getCommand(ActionType.UPDATE, ObjectTypes.RANDHASH) == null
    }
}
