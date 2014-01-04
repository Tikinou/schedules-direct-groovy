package com.tikinou.schedulesdirect

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.core.CommandResult
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.Credentials
import com.tikinou.schedulesdirect.core.exceptions.AuthenticationException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus

/**
 * @author Sebastien Astie.
 */
class ClientUtils {
    private static ClientUtils INSTANCE = new ClientUtils()

    public static ClientUtils getInstance(){
        return INSTANCE
    }

    private ObjectMapper objectMapper;

    private ClientUtils(){
        objectMapper = ModuleRegistration.getInstance().getConfiguredObjectMapper()
    }

    void executeRequest(SchedulesDirectClient client, com.tikinou.schedulesdirect.core.Command command, Class<? extends CommandResult> resultType){
        def postBody = "request=" + URLEncoder.encode(objectMapper.writeValueAsString(command.parameters), "UTF-8")
        RESTClient restClient = new RESTClient(client.baseUrl)
        def response = restClient.post(path:client.endpoint, requestContentType: ContentType.URLENC, body: postBody)
        if(response.status == HttpStatus.SC_OK){
            if (response.data != null) {
                if (response.data instanceof InputStream)
                    command.results = objectMapper.readValue(new InputStreamReader(response.data), resultType)
                else
                    command.results = objectMapper.readValue((String)response.data.text(), resultType)
            }
            command.status = CommandStatus.SUCCESS
        } else {
            command.status = CommandStatus.FAILURE
        }
    }

    void failIfUnauthenticated(Credentials credentials) throws AuthenticationException {
        if (credentials.getRandhash() == null)
            throw new AuthenticationException("Not authenticated");
    }
}
