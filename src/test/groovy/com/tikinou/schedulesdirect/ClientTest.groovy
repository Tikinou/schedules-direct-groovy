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

import com.tikinou.schedulesdirect.core.domain.ActionType
import com.tikinou.schedulesdirect.core.domain.Credentials
import com.tikinou.schedulesdirect.core.domain.ObjectTypes
import com.tikinou.schedulesdirect.core.domain.ResponseCode
import com.tikinou.schedulesdirect.core.domain.SchedulesDirectApiVersion
import com.tikinou.schedulesdirect.core.domain.Country
import com.tikinou.schedulesdirect.core.exceptions.VersionNotSupportedException
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
/**
 * @author: Sebastien Astie
 */
class ClientTest {
    private def client
    private def postalCode = 10562

    @Before
    void setUp() {

        client = new Client(SchedulesDirectApiVersion.VERSION_20131021)
        // use beta site
        client.baseUrl="http://23.21.174.111/"
    }

    @After
    void tearDown() {
    }

    @Test(expected = VersionNotSupportedException.class)
    void testUnknownVersion() {
        new Client(null)
    }

    @Test
    void testConnect() {
        def credentials = createCredentials()
        println "credentials used " << credentials
        assert credentials.randhash == null
        client.connect(credentials)
        assert credentials.randhash != null
        println "TestConnect success: credentials now " << credentials
    }

    @Test
    void testMultipleConnect() {
        def credentials = createCredentials()
        println "credentials used " << credentials
        client.connect(credentials)
        client.connect(credentials)
        println "TestMultipleConnect success: credentials now " << credentials
    }

    @Test
    void testGetCommand() {
        assert client.getCommand(ActionType.ADD, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.DELETE, ObjectTypes.RANDHASH) == null
        assert client.getCommand(ActionType.GET, ObjectTypes.RANDHASH) != null
        assert client.getCommand(ActionType.UPDATE, ObjectTypes.RANDHASH) == null
    }

    @Test
    void testStatus() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.STATUS)
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get Status: " << cmd.results
    }

    @Test
    void testGetheadends() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.HEADENDS)
        cmd.parameters.country = Country.UnitedState
        cmd.parameters.postalCode = postalCode
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get Headends: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    @Test
    void testGetLineups() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.LINEUPS)
        cmd.parameters.headendIds = ["NY67791", "NY62482", "PC:10562", "DISH501"]
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get Lineups: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    @Test
    void testGetSubscribedHeadends() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.HEADENDS)
        cmd.parameters.subscribed = true
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get Subscribed headends: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    @Test
    void testGetPrograms() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.PROGRAMS)
        cmd.parameters.programIds = ["EP017398160007", "SH013762600000", "MV003954050000"]
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get programs: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }


    @Test
    void testGetSchedules() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.SCHEDULES)
        cmd.parameters.stationIds = ["16689", "20360", "20453", "21868"]
        println "credentials used " << credentials
        client.execute(cmd)
        println "Get Schedules: " << cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    void testAddAndDeleteHeadends() {
        def credentials = createCredentials()
        client.connect(credentials)
        def cmd = client.getCommand(ActionType.GET, ObjectTypes.HEADENDS)
        cmd.parameters.country = Country.UnitedState
        cmd.parameters.postalCode = postalCode
        println "credentials used " << credentials
        client.execute(cmd)
        println "Got Headends, try to find the first comcast one and add it"
        assert cmd.results.code == ResponseCode.OK.code
        // now lets find the first headend...
        def headendId = cmd.results.data[0].headend
        // adding the headend
        cmd = client.getCommand(ActionType.ADD, ObjectTypes.HEADENDS)
        cmd.parameters.headendId = headendId
        println "Adding headend ${headendId}"
        client.execute(cmd)
        println cmd.results
        assert cmd.results.code == ResponseCode.OK.code
        cmd = client.getCommand(ActionType.DELETE, ObjectTypes.HEADENDS)
        cmd.parameters.headendId = headendId
        println "Deleting headend ${headendId}"
        client.execute(cmd)
        println cmd.results
        assert cmd.results.code == ResponseCode.OK.code
    }

    void testAddAndHeadend() {
        def credentials = createCredentials()
        client.connect(credentials)
        for(headendId in ["DISH501", "NY62482", "NY67791", "PC:10562"]){
            // adding the headend
            def cmd = client.getCommand(ActionType.ADD, ObjectTypes.HEADENDS)
            cmd.parameters.headendId = headendId
            println "Adding headend ${headendId}"
            client.execute(cmd)
            println cmd.results
            assert cmd.results.code == ResponseCode.OK.code
        }
    }

    private def createCredentials() {
        def slurper = new JsonSlurper()
        def config = slurper.parseText(ClientTest.class.getResource('/credentials.json').text)
        def credentials = new Credentials(username: config.username, password: config.password)
        // override from system props (can be provided from gradle.properties)
        String userName = System.properties["credentials.username"]
        if (userName != null)
            credentials.username = userName
        String password = System.properties["credentials.password"]
        if (password != null)
            credentials.password = password

        assert credentials.username != "CHANGE_USER_NAME"
        assert credentials.password != "CHANGE_PASSWORD"
        return credentials
    }

}
