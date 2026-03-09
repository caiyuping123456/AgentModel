package org.example.agent.controller;

import jakarta.annotation.Resource;
import org.example.agent.Agent.chatAgentStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author caiyuping
 * @date 2026/3/9 15:08
 * @description: 业务
 */
@RestController
public class TestController {
    @Resource
    private chatAgentStream stream;

    @GetMapping("/{msg}")
    public Flux<String> getStreamChat(@PathVariable("msg") String msg){
        return Flux.create(sink->{
            stream.chat("解释一下什么是python")
                    .onPartialResponse(token -> {
                        sink.next(token);
                    })
                    .onCompleteResponse(response -> {
                        sink.complete();
                    })
                    .onError(error -> {
                        sink.error(error);
                    })
                    .start();
        });
    }
}
