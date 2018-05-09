package demo

import demo.services.AuthenticationProviderUserPassword
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpRequest

class BasicAuthSpec extends Specification {

    @Shared
    @AutoCleanup // <1>
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer) // <2>

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL()) // <3>

    def "Verify HTTP Basic Auth works"() {
        when: 'Accessing a secured URL without authenticating'
        client.toBlocking().exchange(HttpRequest.GET('/')) // <4>

        then: 'returns unauthorized'
        HttpClientResponseException e = thrown(HttpClientResponseException) // <5>
        e.status == HttpStatus.UNAUTHORIZED

        when: 'A secured URL is accessed with Basic Auth'
        HttpRequest request = HttpRequest.GET('/')
                .basicAuth("sherlock", "password") // <6>
        HttpResponse<String> rsp = client.toBlocking().exchange(request, String) // <7>

        then: 'the endpoint can be accessed'
        rsp.status == HttpStatus.OK
        rsp.body() == 'sherlock' // <10>
    }
}
