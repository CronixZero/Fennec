package xyz.cronixzero.fennec.notifications;

import com.github.dockerjava.api.model.Container;
import java.util.List;

public interface NotificationService {

  void sendUnknownIpNotification(Container container);

  void sendStoppedNotification(Container container);

  void sendKilledNotification(Container container);

  void sendMoreResultsNotification(Container container, List<String> results);

}
