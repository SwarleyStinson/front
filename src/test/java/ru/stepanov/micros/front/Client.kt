package ru.stepanov.micros.front

import org.junit.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalTime

class Client {

    @Test
    fun consumeServerSentEvent() {
        val client = WebClient.create("http://localhost:8080")
        val type: ParameterizedTypeReference<ServerSentEvent<String>> = object : ParameterizedTypeReference<ServerSentEvent<String>>() {}
        val eventStream = client.get()
                .uri("/stream-sse")
                .retrieve()
                .bodyToFlux(type)

        eventStream
                .subscribe(
                        { content: ServerSentEvent<String> ->
                            System.out.println("Time: ${LocalTime.now()} - event: name[${content.event()}]," +
                                    " id [${content.id()}], content[${content.data()}] ")
                        },
                        { error: Throwable? -> System.err.println("Error receiving SSE: $error") }
                ) { System.out.println("Completed!!!") }

        Thread.sleep(6_000)
    }

}