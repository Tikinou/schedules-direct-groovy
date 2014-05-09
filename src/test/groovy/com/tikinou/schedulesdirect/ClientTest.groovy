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
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsCommand
import com.tikinou.schedulesdirect.core.commands.headend.GetHeadendsParameters
import com.tikinou.schedulesdirect.core.commands.image.GetImageCommand
import com.tikinou.schedulesdirect.core.commands.image.GetImageParameters
import com.tikinou.schedulesdirect.core.commands.lineup.*
import com.tikinou.schedulesdirect.core.commands.message.DeleteMessageCommand
import com.tikinou.schedulesdirect.core.commands.metadata.UpdateMetadataCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommand
import com.tikinou.schedulesdirect.core.commands.program.GetProgramsCommandParameters
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommand
import com.tikinou.schedulesdirect.core.commands.schedules.GetSchedulesCommandParameters
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommand
import com.tikinou.schedulesdirect.core.commands.status.GetStatusCommandParameters
import com.tikinou.schedulesdirect.core.commands.token.TokenCommand
import com.tikinou.schedulesdirect.core.domain.*
import com.tikinou.schedulesdirect.core.exceptions.VersionNotSupportedException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * @author: Sebastien Astie
 */
class ClientTest {
    private static final int NUM_TRIES = 2
    private SchedulesDirectClient client
    private def postalCode = "94105"
    @Before
    void setUp() {
        client = new SchedulesDirectClientImpl()
        client.setup(SchedulesDirectApiVersion.VERSION_20131021, null, true)
    }

    @After
    void tearDown() {
    }

    @Test
    public void testConnect() throws Exception {
        Credentials credentials = createCredentials()
        assert !credentials.token
        client.connect(credentials)
        assert credentials.token
        println "TestConnect success: credentials now: $credentials"
    }

    @Test
    public void testMultipleConnect() throws Exception {
        Credentials credentials = createCredentials()
        assert !credentials.token
        client.connect(credentials)
        assert credentials.token
        client.connect(credentials)
    }

    @Test(expected = VersionNotSupportedException.class)
    public void testUnknownVersion() throws Exception{
        client.setup(null, null, false)
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
        assert client.createCommand(AbstractAddLineupCommand.class)
        assert client.createCommand(AbstractDeleteLineupCommand.class)
        assert client.createCommand(DeleteMessageCommand.class)
        assert client.createCommand(GetHeadendsCommand.class)
        assert client.createCommand(GetSubscribedLineupsCommand.class)
        assert client.createCommand(GetLineupDetailsCommand.class)
        assert client.createCommand(GetProgramsCommand.class)
        assert client.createCommand(GetSchedulesCommand.class)
        assert client.createCommand(GetStatusCommand.class)
        assert client.createCommand(TokenCommand.class)
        assert client.createCommand(UpdateMetadataCommand.class)
    }

    @Test
    public void testStatus() throws Exception {
        Credentials credentials = connect()
        GetStatusCommand cmd = client.createCommand(GetStatusCommand.class)
        cmd.parameters = new GetStatusCommandParameters()
        executeCommand(cmd);
    }

    @Test
    public void testLineups() throws Exception {
        Credentials credentials = connect()
        GetLineupDetailsCommand cmd = client.createCommand(GetLineupDetailsCommand.class)
        cmd.parameters =  new LineupCommandParameters("NY67791")
        executeCommand(cmd)
    }

    @Test
    public void testPrograms() throws Exception {
        Credentials credentials = connect()
        GetProgramsCommand cmd = client.createCommand(GetProgramsCommand.class)
        cmd.parameters =  new GetProgramsCommandParameters(["EP017398160007", "SH013762600000", "MV003954050000"])
        executeCommand(cmd)
    }

    @Test
    public void testSchedules() throws Exception {
        Credentials credentials = connect()
        GetSchedulesCommand cmd = client.createCommand(GetSchedulesCommand.class)
        cmd.parameters =  new GetSchedulesCommandParameters(["16689", "20360", "20453", "21868"])
        executeCommand(cmd)
    }

    @Test
    public void testGetSubscribedHeadends() throws Exception {
        Credentials credentials = connect()
        GetSubscribedLineupsCommand cmd = client.createCommand(GetSubscribedLineupsCommand.class)
        cmd.parameters =  new GetSubscribedLineupsCommandParameters()
        executeCommand(cmd)
    }

    @Test
    public void testGetHeadends() throws Exception {
        Credentials credentials = connect()
        GetHeadendsCommand cmd = client.createCommand(GetHeadendsCommand.class)
        cmd.parameters = new GetHeadendsParameters(country: Country.UnitedStates, postalCode: "10562")
        executeCommand(cmd);
    }

    @Test
    public void testGetImage() throws Exception {
        Credentials credentials = connect()
        GetImageCommand cmd = client.createCommand(GetImageCommand.class)
        cmd.parameters = new GetImageParameters("assets/p3561420_b_v5_aa.jpg")
        executeCommand(cmd);
    }

    public void testDeleteLineup() throws Exception {
        connect()
        AbstractDeleteLineupCommand delCmd = client.createCommand(AbstractDeleteLineupCommand.class)
        delCmd.parameters = new LineupCommandParameters("USA-NY3232-X")
        println "Deleting lineup " << delCmd.parameters.lineupId
        executeCommand(delCmd)
        println "Deleted lineup " << delCmd.parameters.lineupId
    }

    public void testAddAndDeleteHeadends() throws Exception{
        Credentials credentials = connect()
        GetHeadendsCommand cmd = client.createCommand(GetHeadendsCommand.class)
        cmd.parameters = new GetHeadendsParameters(country: Country.UnitedStates, postalCode: postalCode)
        executeCommand(cmd)
        println "Got Headends, try to find the first one and add it"
        assert cmd.results.headends
        Headend headend
        for(Headend a : cmd.results.headends.values()){
            headend = a
            break;
        }
        String uri = headend.lineups[0].uri
        LineupCommandParameters p = new LineupCommandParameters(uri.substring(uri.lastIndexOf('/') + 1))

        AbstractAddLineupCommand addCmd = client.createCommand(AbstractAddLineupCommand.class)
        addCmd.parameters = p
        println "Adding lineup " << p.lineupId
        executeCommand(addCmd)
        println "Added lineup " << p.lineupId
        AbstractDeleteLineupCommand delCmd = client.createCommand(AbstractDeleteLineupCommand.class)
        delCmd.parameters = p
        println "Deleting lineup " << p.lineupId
        executeCommand(delCmd)
        println "Deleted lineup " << p.lineupId
    }

    private Credentials connect() throws Exception {
        Credentials credentials = createCredentials()
        client.connect(credentials)
        return credentials
    }

    private void executeCommand(Command cmd) throws Exception{
        client.execute(cmd, NUM_TRIES)
        println cmd.results
        assert cmd.status == CommandStatus.SUCCESS
    }

}
