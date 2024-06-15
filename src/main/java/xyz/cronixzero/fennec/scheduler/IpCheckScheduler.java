package xyz.cronixzero.fennec.scheduler;

import com.github.dockerjava.api.model.Container;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import xyz.cronixzero.fennec.config.Configuration;
import xyz.cronixzero.fennec.notifications.Notifier;
import xyz.cronixzero.fennec.services.DockerService;
import xyz.cronixzero.fennec.utils.Ansi;

@Slf4j
public class IpCheckScheduler extends AbstractScheduledService {

  private final Configuration config;
  private final DockerService dockerService;
  private final Notifier notifier;

  @Inject
  public IpCheckScheduler(Configuration config, DockerService dockerService, Notifier notifier) {
    this.config = config;
    this.dockerService = dockerService;
    this.notifier = notifier;

    startAsync();
  }

  @Override
  protected void runOneIteration() {
    log.debug(" = Running Check = ");

    for (String id : config.getDockerContainerIds()) {
      InetAddress containerIp;
      Container container = dockerService.getContainer(id);

      if(container == null) {
        log.warn("Container {} not found! Skipping for now...", id);
        continue;
      }

      try {
        String ip = dockerService.getContainerIp(id);
        log.debug("Container {} has IP {}", id, ip);
        containerIp = InetAddress.getByName(ip);
      } catch (UnknownHostException e) {
        log.error("Couldn't get the Container InetAddress through ifconfig.me", e);
        notifier.sendUnknownIpNotification(container);
        dockerService.stopContainer(id);
        return;
      }

      if (containerIp.equals(config.getTrueIp())) {
        log.warn("{}{}[!!!]{} Container {} / IP match detected!", Ansi.BACKGROUND_YELLOW, Ansi.RED,
            Ansi.Normal, id);
        sanctionViolatingContainer(container);
      }
    }

    log.debug(" = Check complete = ");
  }

  private void sanctionViolatingContainer(Container container) {
    boolean stop = config.shouldStopOnTrueIpMatch();

    if (stop) {
      dockerService.stopContainer(container.getId());
      notifier.sendStoppedNotification(container);
      return;
    }

    dockerService.killContainer(container.getId());
    notifier.sendKilledNotification(container);
  }

  @NotNull
  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedDelaySchedule(Duration.ZERO, config.getCheckInterval());
  }
}
