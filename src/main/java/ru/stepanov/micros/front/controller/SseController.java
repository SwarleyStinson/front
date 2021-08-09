package ru.stepanov.micros.front.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.time.LocalTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Controller
public class SseController {

    HashMap<String, SseEmitter> notificationCache = new HashMap<>();

    @GetMapping("/")
    public String home(Model model) {
        return "sse";
    }

    @GetMapping(path = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> streamFlux() {
        return Flux.interval(Duration.ofSeconds(90))
                .map(sequence -> "Flux<String> - " + now().toString());
    }

    @GetMapping("/sse-flux")
    @ResponseBody
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(90))
                .map(seq -> ServerSentEvent.<String>builder()
                        .id("" + seq)
                        .data("Flux<ServerSentEvent<String> - " + now())
                        .build()
                );
    }

    @GetMapping(value = "/sse-mvc")
    @ResponseBody
    public SseEmitter getSseEmitter(@RequestParam String operationId) {
        System.out.println("получен запрос на getSseEmitter()");
        SseEmitter notification = notificationCache.get(operationId);
        if (notification != null) {
            return notification;
        }

        SseEmitter emitter = new SseEmitter(600_000L);
        notificationCache.put(operationId, emitter);

        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    try {
                        sleep(1_000);
                        SseEmitter.SseEventBuilder event =
                                SseEmitter.event()
                                        .name("message")
                                        .data("SseEmitter.send(*) - " + now().format(ofPattern("HH:mm:ss")))
                                        .id("asdcsdsvdfvfdv");
                        emitter.send(event);
                        sleep(1_500);
                        event.data("SseEmitter.send(*) - " + now().format(ofPattern("HH:mm:ss")));
                        emitter.send(event);
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                        System.err.println(ex);
                        System.err.println(ex.getStackTrace());
                    }
                });
        return emitter;
    }

    @GetMapping("/createEvents")
    @ResponseBody
    public void createEvents() {
        for (int i = 0; i < 10; i++) {
            notificationCache.put(randomNumeric(10), new SseEmitter(600_000L));
        }
    }

    @GetMapping("/getEventList")
    @ResponseBody
    public List<String> getEvents() {
        return notificationCache.keySet().stream().collect(Collectors.toList());
    }

    @GetMapping("/getEvent")
    @ResponseBody
    public SseEmitter getEvent(@RequestParam String operationId) {
        System.out.println("получено запрос на SSE " + operationId);
        return notificationCache.get(operationId);
    }

    @GetMapping("/sendEvents")
    @ResponseBody
    public void sendEvents() {
        System.out.println("получен запрос на отпарвку SSE");
        Executors.newSingleThreadExecutor().execute(() -> {
                    notificationCache.forEach((id, emitter) -> {
                        try {
                            SseEmitter.SseEventBuilder event =
                                    SseEmitter.event()
                                            .name("test_event")
                                            .data("[" + id + "] some event: " + now().format(ofPattern("HH:mm:ss")))
                                            .id(UUID.randomUUID().toString());
                            emitter.send(event);
                            sleep(30_000);

                            event.name("business_message");
                            event.data("[" + id + "] some event: " + now().format(ofPattern("HH:mm:ss")));
                            emitter.send(event);
                            emitter.complete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            emitter.completeWithError(e);
                        }
                    });
                    notificationCache.clear();
                }
        );
    }

}
