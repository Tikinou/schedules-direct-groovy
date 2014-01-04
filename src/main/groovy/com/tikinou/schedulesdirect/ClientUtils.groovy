package com.tikinou.schedulesdirect

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikinou.schedulesdirect.core.SchedulesDirectClient
import com.tikinou.schedulesdirect.core.domain.CommandStatus
import com.tikinou.schedulesdirect.core.jackson.ModuleRegistration
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus

/**
 * @author Sebastien Astie.
 */
class ClientUtils {
    static ObjectMapper OBJECT_MAPPER = ModuleRegistration.getInstance().getConfiguredObjectMapper()

    static void executeRequest(SchedulesDirectClient client, com.tikinou.schedulesdirect.core.Command command, Class resultType){
        def postBody = "request=" + URLEncoder.encode(OBJECT_MAPPER.writeValueAsString(command.parameters), "UTF-8")
        RESTClient restClient = new RESTClient(client.baseUrl)
        def response = restClient.post(path:client.endpoint, requestContentType: ContentType.URLENC, body: postBody)
        if(response.status == HttpStatus.SC_OK){
            if (response.data != null) {
                if (response.data instanceof InputStream)
                    command.results = OBJECT_MAPPER.readValue(new InputStreamReader(response.data), resultType)
                else
                    command.results = OBJECT_MAPPER.readValue((String)response.data.text(), resultType)
            }
            command.status = CommandStatus.SUCCESS
        } else {
            command.status = CommandStatus.FAILURE
        }
    }
}
