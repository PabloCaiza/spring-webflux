package com.quesito.springreactor.controller;

import com.quesito.springreactor.model.Plate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/backpressure")
public class BackPressureController {
    @GetMapping("stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return Flux.interval(Duration.ofMillis(100))
                .map(l ->new Plate("1", "2","a",10.0,true));
    }
    @GetMapping("/buffer")
    public Flux<Integer> buffer() {
        return Flux.range(1, 10)
                .log()
                .limitRate(10)
                .delayElements(Duration.ofMillis(1));
    }

}
