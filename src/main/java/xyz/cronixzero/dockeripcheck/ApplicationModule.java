package xyz.cronixzero.dockeripcheck;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.inject.Binder;
import com.google.inject.Module;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import java.io.ObjectInputFilter.Config;
import java.nio.file.Path;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import xyz.cronixzero.dockeripcheck.annotations.HtmlTemplateEngine;
import xyz.cronixzero.dockeripcheck.annotations.PlaintextTemplateEngine;
import xyz.cronixzero.dockeripcheck.config.Configuration;

@Slf4j
public class ApplicationModule implements Module {

  private final Configuration config;

  public ApplicationModule(Configuration config) {
    this.config = config;
  }

  @Override
  public void configure(Binder binder) {
    binder.bind(Configuration.class).toInstance(config);
    binder.bind(DockerClient.class).toInstance(getDockerClient());
  }

  private DockerClient getDockerClient() {
    DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("unix:///var/run/docker.sock")
        .build();

    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
        .dockerHost(dockerConfig.getDockerHost())
        .sslConfig(dockerConfig.getSSLConfig())
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build();

    return DockerClientImpl.getInstance(dockerConfig, httpClient);
  }
}
