package ru.stepanov.micros.front.controller;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static java.time.LocalTime.now;

@Controller
public class GracefullyShutdownController {

    HashMap<String, SseEmitter> notificationCache = new HashMap<>();

    @GetMapping("/gs")
    public String home(Model model) {
        return "gracefully";
    }

    @GetMapping(path = "/doWork", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> doWork() {
        return Flux.interval(Duration.ofSeconds(10))
                .map(number -> number + " - " + now().format(DateTimeFormatter.ISO_LOCAL_TIME))
                .doOnNext(System.out::println);
    }

    @SneakyThrows
    @GetMapping(path = "/doSleepWork")
    @ResponseBody
    public String doSleepWork() {
        Thread.sleep(5_000);
        String work = now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        new RestTemplate().getForObject("http://localhost:8081/log?work=" + work, String.class);
        return work;
    }

    @SneakyThrows
    @GetMapping(path = "/doHardWork")
    @ResponseBody
    public String doHardWork() {
        long current = System.currentTimeMillis() / 1_000;
        while (current + 5 > System.currentTimeMillis() / 1_000) {
        }
        String work = now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        new RestTemplate().getForObject("http://localhost:8081/log?work=" + work, String.class);
        return work;
    }

    @GetMapping(path = "/log")
    @ResponseBody
    public void log(@RequestParam String work) {
        System.out.println("RECEIVED: " + work);
    }
}
