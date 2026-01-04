package com.app;

import static spark.Spark.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class App {

  public static void main(String[] args) {
    port(8080);

    post("/process", (req, res) -> {
      long start = System.currentTimeMillis();

      BufferedReader reader = req.raw().getReader();

      ExecutorService pool = Executors.newFixedThreadPool(
          Runtime.getRuntime().availableProcessors()
      );

      AtomicLong rows = new AtomicLong();
      AtomicLong sum = new AtomicLong();
      AtomicLong min = new AtomicLong(Long.MAX_VALUE);
      AtomicLong max = new AtomicLong(Long.MIN_VALUE);

      reader.lines().forEach(line -> pool.submit(() -> {
        String[] parts = line.split(",");
        long value = Long.parseLong(parts[1]);

        rows.incrementAndGet();
        sum.addAndGet(value);
        min.accumulateAndGet(value, Math::min);
        max.accumulateAndGet(value, Math::max);
      }));

      pool.shutdown();
      pool.awaitTermination(10, TimeUnit.MINUTES);

      long time = System.currentTimeMillis() - start;
      double avg = sum.get() / (double) rows.get();

      res.type("application/json");
      return String.format(
        "{ \"rows\": %d, \"sum\": %d, \"average\": %.2f, \"min\": %d, \"max\": %d, \"processingTimeMs\": %d }",
        rows.get(), sum.get(), avg, min.get(), max.get(), time
      );
    });
  }
}
