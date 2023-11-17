package org.kontza.longpoll.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Slf4j
public class SlowController {
    public static final String TIMED_OUT = "Timed Out.";
    public static final long TIME_OUT_MS = 3000L;
    private ExecutorService workers = Executors.newCachedThreadPool();

    @GetMapping("/register")
    public DeferredResult<String> publisher() {
        DeferredResult<String> output = new DeferredResult<>(TIME_OUT_MS, () -> {
            log.info("Timed out");
            return TIMED_OUT;
        });
        output.onError((throwable -> {
            log.error("Error occurred:", throwable);
        }));
        output.onCompletion(() -> {
            log.info("Completed");
        });
        workers.execute(() -> {
            List<Long> givenList = Arrays.asList((TIME_OUT_MS / 1000) - 1, TIME_OUT_MS / 1000, (TIME_OUT_MS / 1000) + 1);
            Random rand = new Random();
            long randomSleep = givenList.get(rand.nextInt(givenList.size()));
            log.info("Going to sleep {} seconds", randomSleep);
            try {
                TimeUnit.SECONDS.sleep(randomSleep);
                output.setResult("Task Finished");
            } catch (Exception e) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                }
                log.error("Exception:", e);
            }
        });
        return output;
    }
}
