package com.tikinou.schedulesdirect

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.core.CommandResult
import com.tikinou.schedulesdirect.core.HttpMethod
import com.tikinou.schedulesdirect.core.ParameterizedCommand
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.commands.AuthenticatedBaseCommandParameter
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.domain.Credentials
import com.tikinou.schedulesdirect.core.exceptions.AuthenticationException
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
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

    def executeRequest(SchedulesDirectClient client, ParameterizedCommand command, Class<? extends CommandResult> resultType, boolean returnRaw = false) {
        RESTClient restClient = new RESTClient(client.url.endsWith("/") ? client.url : client.url + "/")
        restClient.parser."application/json" = restClient.parser."text/plain"
        restClient.headers["User-Agent"] = client.userAgent ?: "tikinou-sd-api";
        if (command.parameters instanceof AuthenticatedBaseCommandParameter) {
            def token = ((AuthenticatedBaseCommandParameter) command.parameters).token
            restClient.headers["token"] = token
        }

        def reqBody = objectMapper.writeValueAsString(command.parameters)
        def response
        switch (command.method) {
            case HttpMethod.PUT:
                restClient.put(path: command.endPoint, requestContentType: ContentType.JSON, body: reqBody)
                break;
            case HttpMethod.DELETE:
                restClient.delete(path: command.endPoint, requestContentType: ContentType.JSON, body: reqBody)
                break;
            case HttpMethod.POST:
                response = restClient.post(path: command.endPoint, requestContentType: ContentType.JSON, body: reqBody)
                break;
            case HttpMethod.GET:
                def reqParams = command.parameters.toRequestParameters()
                response = restClient.get(path: command.endPoint, query: reqParams)
                break;

        }

        command.status = CommandStatus.FAILURE
        if (response.status == HttpStatus.SC_OK) {
            if(returnRaw){
                command.status = CommandStatus.SUCCESS
                return response.data
            }
            else if (response.data != null) {
                command.results = objectMapper.readValue(response.data, resultType)
                command.status = CommandStatus.SUCCESS
            }

        }
        return null
    }

    void failIfUnauthenticated(Credentials credentials) throws AuthenticationException {
        if (credentials.token == null)
            throw new AuthenticationException("Not authenticated")
    }

    int retryConnection(SchedulesDirectClient client, AuthenticatedBaseCommandParameter params, HttpResponseException ex, int numRetries) throws Exception {
        numRetries--
        if(numRetries < 0)
            throw ex
        if(ex.statusCode == HttpStatus.SC_FORBIDDEN) {
            client.credentials.resetTokenInfo()
            params.token = null
            client.connect(client.credentials, false)
            params.token = client.credentials.token
        }
        return numRetries;
    }
}
