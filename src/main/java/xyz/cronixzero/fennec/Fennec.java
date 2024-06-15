package xyz.cronixzero.fennec;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import lombok.extern.slf4j.Slf4j;
import xyz.cronixzero.fennec.config.Configuration;
import xyz.cronixzero.fennec.notifications.NotificationModule;
import xyz.cronixzero.fennec.scheduler.SchedulerModule;
import xyz.cronixzero.fennec.services.ServicesModule;

@Slf4j
public class Fennec {

  public static void main(String[] args) {
    log.info("Starting Fennec");

    Configuration config;
    try {
      config = Configuration.getSuitableConfig();
    } catch (IllegalArgumentException e) {
      log.error("Configuration is incomplete.");
      log.error(e.getMessage());
      return;
    }

    Guice.createInjector(new NotificationModule(config), new ApplicationModule(config),
        new ServicesModule(), new SchedulerModule());

    MoreExecutors.newDirectExecutorService().submit(() -> {
      try {
        Thread.currentThread().join();
      } catch (InterruptedException e) {
        log.error("Infinite Loop Interrupted", e);
        Thread.currentThread().interrupt();
      }
    });
  }
}
