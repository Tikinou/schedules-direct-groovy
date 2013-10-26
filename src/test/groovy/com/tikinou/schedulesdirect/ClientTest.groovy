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

import com.tikinou.schedulesdirect.utils.Country
import groovy.json.JsonSlurper
import org.codehaus.groovy.GroovyException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * @author: Sebastien Astie
 */
class ClientTest {
    private def client;
    @Before
    void setUp() {
        client = new Client(SchedulesDirectApiVersion.VERSION_20130709)
    }

    @After
    void tearDown() {
    }

    @Test(expected = GroovyException.class)
    void testUnknownVersion() {
        new Client(null)
    }

    @Test
    void testConnect() {
        def credentials = createCredentials()
        assert credentials.randhash == null
        client.connect(credentials)
        assert credentials.randhash != null
    }

    @Test
    void testGetCommand() {
        assert client.getCommand(ActionType.ADD, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.DELETE, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.GET, ObjectTypes.RANDHASH) != null
        assert client.getCommand(ActionType.UPDATE, ObjectTypes.RANDHASH) == null
    }

    @Test
    void testStatus(){
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.STATUS)
        client.execute(cmd)
        println "Get Status: " << cmd.results
    }

    @Test
    void testGetheadends(){
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.HEADENDS)
        cmd.parameters.country = Country.UnitedState
        cmd.parameters.postalCode = 10562
        client.execute(cmd)
        println "Get Headends: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    private def createCredentials(){
        def slurper = new JsonSlurper()
        def config = slurper.parseText( ClientTest.class.getResource( '/credentials.json' ).text )
        def credentials = new Credentials(username:config.username, password:config.password)
        // override from system props (can be provided from gradle.properties)
        String userName = System.properties["credentials.username"]
        if(userName != null)
            credentials.username = userName
        String password = System.properties["credentials.password"]
        if(password != null)
            credentials.password = password

        println "credentials used :" + credentials.username + " | " + credentials.password
        assert credentials.username != "CHANGE_USER_NAME"
        assert credentials.password != "CHANGE_PASSWORD"
        return credentials
    }

}
