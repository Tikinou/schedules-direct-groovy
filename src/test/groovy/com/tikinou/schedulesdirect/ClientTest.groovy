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

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.core.Command
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.headend.AddDeleteHeadendParameters
import com.tikinou.schedulesdirect.core.commands.headend.AddHeadendCommand
import com.tikinou.schedulesdirect.core.commands.headend.DeleteHeadendCommand
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsCommand
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsParameters
import com.tikinou.schedulesdirect.core.commands.lineup.GetLineupsCommand
import com.tikinou.schedulesdirect.core.commands.lineup.GetLineupsCommandParameters
import com.tikinou.schedulesdirect.core.commands.message.DeleteMessageCommand
import com.tikinou.schedulesdirect.core.commands.metadata.UpdateMetadataCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommandParameters
import com.tikinou.schedulesdirect.core.commands.randhash.RandHashCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommandParameters
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommand
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommandParameters
import com.tikinou.schedulesdirect.core.domain.ActionType
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.Credentials
import com.tikinou.schedulesdirect.core.domain.Headend
import com.tikinou.schedulesdirect.core.domain.ObjectTypes
import com.tikinou.schedulesdirect.core.domain.ResponseCode
import com.tikinou.schedulesdirect.core.domain.SchedulesDirectApiVersion
import com.tikinou.schedulesdirect.core.domain.Country
import com.tikinou.schedulesdirect.core.exceptions.VersionNotSupportedException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
/**
 * @author: Sebastien Astie
 */
class ClientTest {
    private SchedulesDirectClient client
    private def postalCode = "94105"

    @Before
    void setUp() {
        client = new SchedulesDirectClientImpl()
        client.setup(SchedulesDirectApiVersion.VERSION_20131021, true)
    }

    @After
    void tearDown() {
    }

    @Test
    public void testConnect() throws Exception {
        Credentials credentials = createCredentials()
        assert !credentials.randhash
        client.connect(credentials)
        assert credentials.randhash
        println "TestConnect success: credentials now: $credentials"
    }

    @Test
    public void testMultipleConnect() throws Exception {
        Credentials credentials = createCredentials()
        assert !credentials.randhash
        client.connect(credentials)
        assert credentials.randhash
        client.connect(credentials)
    }

    @Test(expected = VersionNotSupportedException.class)
    public void testUnknownVersion() throws Exception{
        client.setup(null, false)
    }

    private Credentials createCredentials() throws IOException {
        ObjectMapper mapper = ModuleRegistration.instance.configuredObjectMapper
        Credentials credentials = mapper.readValue(ClientTest.class.getResourceAsStream("/credentials.json"), Credentials.class)
        credentials.clearPassword = credentials.password
        // override from system props (can be provided from gradle.properties)
        String userName = System.properties["credentials.username"]
        if (userName)
            credentials.username = userName
        String password = System.properties["credentials.password"]
        if (password)
            credentials.clearPassword = password

        assert credentials.username && credentials.username != "CHANGE_USER_NAME"
        assert credentials.clearPassword && credentials.clearPassword != "CHANGE_PASSWORD"
        return credentials
    }

    @Test
    void testGetCommand() {
        assert client.createCommand(AddHeadendCommand.class)
        assert client.createCommand(DeleteHeadendCommand.class)
        assert client.createCommand(DeleteMessageCommand.class)
        assert client.createCommand(GetHeadendsCommand.class)
        assert client.createCommand(GetLineupsCommand.class)
        assert client.createCommand(GetProgramsCommand.class)
        assert client.createCommand(GetSchedulesCommand.class)
        assert client.createCommand(GetStatusCommand.class)
        assert client.createCommand(RandHashCommand.class)
        assert client.createCommand(UpdateMetadataCommand.class)
    }

    @Test
    public void testStatus() throws Exception {
        Credentials credentials = connect()
        GetStatusCommand cmd = client.createCommand(GetStatusCommand.class)
        cmd.parameters = new GetStatusCommandParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021)
        executeCommand(cmd);
    }

    @Test
    public void testLineups() throws Exception {
        Credentials credentials = connect()
        GetLineupsCommand cmd = client.createCommand(GetLineupsCommand.class)
        cmd.parameters =  new GetLineupsCommandParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021, ["NY67791"])
        executeCommand(cmd)
    }

    @Test
    public void testPrograms() throws Exception {
        Credentials credentials = connect()
        GetProgramsCommand cmd = client.createCommand(GetProgramsCommand.class)
        cmd.parameters =  new GetProgramsCommandParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021, ["EP017398160007", "SH013762600000", "MV003954050000"])
        executeCommand(cmd)
    }

    @Test
    public void testSchedules() throws Exception {
        Credentials credentials = connect()
        GetSchedulesCommand cmd = client.createCommand(GetSchedulesCommand.class)
        cmd.parameters =  new GetSchedulesCommandParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021, ["16689", "20360", "20453", "21868"])
        executeCommand(cmd)
    }

    @Test
    public void testGetSubscribedHeadends() throws Exception {
        Credentials credentials = connect()
        GetHeadendsCommand cmd = client.createCommand(GetHeadendsCommand.class)
        GetHeadendsParameters parameters =  new GetHeadendsParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021)
        parameters.subscribed = true
        cmd.parameters = parameters
        executeCommand(cmd)
    }

    @Test
    public void testGetHeadends() throws Exception {
        Credentials credentials = connect()
        GetHeadendsCommand cmd = client.createCommand(GetHeadendsCommand.class)
        GetHeadendsParameters parameters =  new GetHeadendsParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021)
        parameters.country = Country.UnitedStates
        parameters.postalCode = "10564"
        cmd.parameters = parameters
        executeCommand(cmd);
    }

    public void testAddAndDeleteHeadends() throws Exception{
        Credentials credentials = connect()
        GetHeadendsCommand cmd = client.createCommand(GetHeadendsCommand.class)
        GetHeadendsParameters parameters =  new GetHeadendsParameters(credentials.randhash, SchedulesDirectApiVersion.VERSION_20131021)
        parameters.country = Country.UnitedStates
        parameters.postalCode = postalCode
        cmd.parameters = parameters
        executeCommand(cmd)
        println "Got Headends, try to find the first one and add it"
        assert !cmd.results.data
        Headend headend = cmd.results.data[0]
        AddHeadendCommand addCmd = client.createCommand(AddHeadendCommand.class)
        addCmd.parameters = new AddDeleteHeadendParameters(credentials.getRandhash(), false, SchedulesDirectApiVersion.VERSION_20131021, headend.headend)
        println "Adding headend " << headend.headend
        executeCommand(addCmd)
        println "Added headend " << headend.headend
        DeleteHeadendCommand delCmd = client.createCommand(DeleteHeadendCommand.class)
        delCmd.parameters = new AddDeleteHeadendParameters(credentials.getRandhash(), true, SchedulesDirectApiVersion.VERSION_20131021, headend.headend);
        println "Deleting headend " << headend.headend
        executeCommand(delCmd)
        println "Deleted headend " << headend.headend
    }

    private Credentials connect() throws Exception {
        Credentials credentials = createCredentials()
        client.connect(credentials)
        return credentials
    }

    private void executeCommand(Command cmd) throws Exception{
        client.execute(cmd)
        println cmd.results
        assert cmd.status == CommandStatus.SUCCESS
    }

}
