package xyz.cronixzero.fennec.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import xyz.cronixzero.fennec.notifications.Notifier;

@Slf4j
public class DockerService {

  private final DockerClient docker;
  private final Notifier notifier;

  @Inject
  public DockerService(DockerClient docker, Notifier notifier) {
    this.docker = docker;
    this.notifier = notifier;
  }

  public String getContainerIp(String containerId) {
    Container container = getContainer(containerId);

    if(container == null) {
      throw new IllegalArgumentException("Container with id " + containerId + " not found!");
    }

    log.debug("Started to create cmd");
    var cmd = docker.execCreateCmd(containerId)
        .withCmd("/usr/bin/curl", "https://ifconfig.me/ip")
        .withAttachStdout(true)
        .exec();

    log.debug("Created Exec with id: {} in container: {}", cmd.getId(), containerId);
    Observable<String> execObservable = Observable.create(
        emitter -> docker.execStartCmd(cmd.getId()).exec(new Adapter<Frame>() {
          @Override
          public void onNext(Frame item) {
            log.debug("Exec next: {}", item);
            super.onNext(item);
            emitter.onNext(item.toString());
          }

          @Override
          public void onError(Throwable throwable) {
            log.debug("Error occurred", throwable);
            super.onError(throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onComplete() {
            log.debug("Exec completed");
            super.onComplete();
            emitter.onComplete();
          }
        }));

    List<String> result = execObservable.blockingStream().toList();
    log.debug("Result: {}", result);
    if(result.size() > 1) {
      log.warn("Result Size for IP Getter Execution has more than 1 result! \n {}", result.toArray());
      notifier.sendMoreResultsNotification(container, result);
    }

    for(String output : result) {
      if(output.startsWith("STDOUT: ")) {
        return output.replace("STDOUT: ", "");
      }
    }

    notifier.sendUnknownIpNotification(container);
    throw new IllegalStateException("Could not determine the IP Address through ifconfig.me");
  }

  public void stopContainer(String id) {
    docker.stopContainerCmd(id)
        .exec();
  }

  public void killContainer(String id) {
    docker.killContainerCmd(id)
        .exec();
  }

  public Container getContainer(String containerId) {
    for (Container container : docker.listContainersCmd().exec()) {
      if (container.getId().equals(containerId)) {
        return container;
      }
    }

    return null;
  }
}
