package xyz.cronixzero.dockeripcheck.notifications;

import com.github.dockerjava.api.model.Container;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Notifier {

  private final List<NotificationService> notificationServices;

  public Notifier(List<NotificationService> notificationServices) {
    this.notificationServices = notificationServices;
  }

  public void sendUnknownIpNotification(Container container) {
    log.info("Sending \"Unknown IP\" Notification");
    notificationServices.forEach(service -> service.sendUnknownIpNotification(container));
    log.info("\"Unknown IP\" Notification sent");
  }

  public void sendStoppedNotification(Container container) {
    log.info("Sending \"Stopped\" Notification");
    notificationServices.forEach(service -> service.sendStoppedNotification(container));
    log.info("\"Stopped\" Notification sent");
  }

  public void sendKilledNotification(Container container) {
    log.info("Sending \"Killed\" Notification");
    notificationServices.forEach(service -> service.sendKilledNotification(container));
    log.info("\"Killed\" Notification sent");
  }

  public void sendMoreResultsNotification(Container container, List<String> results) {
    log.info("Sending \"More Results\" Notification");
    notificationServices.forEach(service -> service.sendMoreResultsNotification(container, results));
    log.info("\"More Results\" Notification sent");
  }
}
