package xyz.cronixzero.dockeripcheck;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import lombok.extern.slf4j.Slf4j;
import xyz.cronixzero.dockeripcheck.config.Configuration;
import xyz.cronixzero.dockeripcheck.notifications.NotificationModule;
import xyz.cronixzero.dockeripcheck.scheduler.SchedulerModule;
import xyz.cronixzero.dockeripcheck.services.ServicesModule;

@Slf4j
public class DockerIpCheck {

  public static void main(String[] args) {
    log.info("Starting DockerIpCheck");

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
