package xyz.cronixzero.fennec.notifications;

import com.github.dockerjava.api.model.Container;
import java.util.List;

public class DiscordWebhookNotificationService implements NotificationService {

  @Override
  public void sendUnknownIpNotification(Container container) {

  }

  @Override
  public void sendStoppedNotification(Container container) {

  }

  @Override
  public void sendKilledNotification(Container container) {

  }

  @Override
  public void sendMoreResultsNotification(Container container, List<String> results) {

  }
}
