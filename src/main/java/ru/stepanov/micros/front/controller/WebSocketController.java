package ru.stepanov.micros.front.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) {
        return new OutputMessage(message.getFrom(), message.getText(), LocalDateTime.now().toString());
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class Greeting {
        private String message;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class Message {
        private String from;
        private String text;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class OutputMessage {
        private String from;
        private String text;
        private String time;
    }
}
