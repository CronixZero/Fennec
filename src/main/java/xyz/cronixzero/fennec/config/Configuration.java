package xyz.cronixzero.fennec.config;

import java.net.InetAddress;
import java.time.Duration;

public interface Configuration {

  boolean shouldStopOnTrueIpMatch();

  boolean shouldKillOnTrueIpMatch();

  InetAddress getTrueIp();

  Duration getCheckInterval();

  boolean shouldNotifyDiscordWebhook();

  String getDiscordWebhookUrl();

  boolean shouldNotifyEmail();
  String getEmailSmtpHost();
  int getEmailSmtpPort();
  String getEmailSmtpUsername();
  String getEmailSmtpPassword();
  String getEmailFromName();
  String getEmailFromAddress();
  String getEmailToAddress();


  String[] getDockerContainerIds();

  static Configuration getSuitableConfig() {
    if (EnvironmentVariableConfiguration.supports()) {
      return new EnvironmentVariableConfiguration();
    }

    throw new IllegalStateException("No suitable configuration found.");
  }
}
