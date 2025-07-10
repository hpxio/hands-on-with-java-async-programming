package io.dice28.codelabs.multithreading.server.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SleepUtils {
  private static final int DEFAULT_DELAY = 4000;
  private static final int SHORT_DELAY = 1000;
  private static final int LONG_DELAY = 8000;

  public static void defaultDelay() {
    delay(DEFAULT_DELAY);
  }

  public static void longDelay() {
    delay(LONG_DELAY);
  }

  public static void shortDelay() {
    delay(SHORT_DELAY);
  }

  private static void delay(long delayTimeInMilliSeconds) {
    try {
      Thread.sleep(delayTimeInMilliSeconds);
    } catch (InterruptedException e) {
      log.error("Thread sleep() call failed!");
    }
  }
}
