package xyz.cronixzero.dockeripcheck.config;

import com.google.common.base.MoreObjects;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentVariableConfiguration implements Configuration {

  private static final String ENV_STOP_ON_TRUE_IP_MATCH = "STOP_ON_TRUE_IP_MATCH";
  private static final String ENV_KILL_ON_TRUE_IP_MATCH = "KILL_ON_TRUE_IP_MATCH";
  private static final String ENV_CHECK_INTERVAL = "CHECK_INTERVAL";
  private static final String ENV_NOTIFY_DISCORD_WEBHOOK = "NOTIFY_DISCORD_WEBHOOK";
  private static final String ENV_DISCORD_WEBHOOK_URL = "DISCORD_WEBHOOK_URL";
  private static final String ENV_DOCKER_CONTAINER_IDS = "DOCKER_CONTAINER_IDS";
  private static final String ENV_NOTIFY_EMAIL = "NOTIFY_EMAIL";
  private static final String ENV_EMAIL_SMTP_HOST = "EMAIL_SMTP_HOST";
  private static final String ENV_EMAIL_SMTP_PORT = "EMAIL_SMTP_PORT";
  private static final String ENV_EMAIL_SMTP_USERNAME = "EMAIL_SMTP_USERNAME";
  private static final String ENV_EMAIL_SMTP_PASSWORD = "EMAIL_SMTP_PASSWORD";
  private static final String ENV_EMAIL_FROM_ADDRESS = "EMAIL_FROM_ADDRESS";
  private static final String ENV_EMAIL_FROM_NAME = "EMAIL_FROM_NAME";
  private static final String ENV_EMAIL_TO_ADDRESS = "EMAIL_TO_ADDRESS";

  private final boolean shouldStopOnTrueIpMatch;
  private final boolean shouldKillOnTrueIpMatch;
  private final InetAddress trueIp;
  private final Duration checkInterval;
  private final boolean shouldNotifyDiscordWebhook;
  private String discordWebhookUrl;
  private final String[] dockerContainerIds;
  private final boolean shouldNotifyEmail;
  private String emailSmtpHost;
  private int emailSmtpPort;
  private String emailSmtpUsername;
  private String emailSmtpPassword;
  private String emailFromAddress;
  private String emailFromName;
  private String emailToAddress;

  public EnvironmentVariableConfiguration() {
    log.info("Using environment variable configuration");
    shouldStopOnTrueIpMatch = Boolean.parseBoolean(System.getenv(ENV_STOP_ON_TRUE_IP_MATCH));
    shouldKillOnTrueIpMatch = Boolean.parseBoolean(System.getenv(ENV_KILL_ON_TRUE_IP_MATCH));

    if ((shouldStopOnTrueIpMatch && shouldKillOnTrueIpMatch)
        || !(shouldStopOnTrueIpMatch || shouldKillOnTrueIpMatch)) {
      throw new IllegalArgumentException(
          "You must chose either kill or stop on true ip match, not both or neither.");
    }

    try {
      trueIp = InetAddress.getByName(Unirest.get("https://ifconfig.me/ip")
          .asString().getBody());
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Could not get true IP Address via ifconfig.me", e);
    }

    try {
      checkInterval = Duration.ofMillis(Long.parseLong(System.getenv(ENV_CHECK_INTERVAL)));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid check interval.", e);
    }

    shouldNotifyDiscordWebhook = Boolean.parseBoolean(System.getenv(ENV_NOTIFY_DISCORD_WEBHOOK));
    if (shouldNotifyDiscordWebhook) {
      discordWebhookUrl = System.getenv(ENV_DISCORD_WEBHOOK_URL);
    }

    shouldNotifyEmail = Boolean.parseBoolean(System.getenv(ENV_NOTIFY_EMAIL));
    if (shouldNotifyEmail) {
      emailSmtpHost = System.getenv(ENV_EMAIL_SMTP_HOST);
      emailSmtpPort = Integer.parseInt(System.getenv(ENV_EMAIL_SMTP_PORT));
      emailSmtpUsername = System.getenv(ENV_EMAIL_SMTP_USERNAME);
      emailSmtpPassword = System.getenv(ENV_EMAIL_SMTP_PASSWORD);
      emailFromAddress = System.getenv(ENV_EMAIL_FROM_ADDRESS);
      emailFromName = System.getenv(ENV_EMAIL_FROM_NAME);
      emailToAddress = System.getenv(ENV_EMAIL_TO_ADDRESS);
    }

    if (System.getenv(ENV_DOCKER_CONTAINER_IDS) == null) {
      throw new IllegalArgumentException("You must specify at least one docker container id.");
    }
    dockerContainerIds = System.getenv(ENV_DOCKER_CONTAINER_IDS).split(",");

    log.info("Extracted following Configuration:\n {}", this);
  }

  @Override
  public boolean shouldStopOnTrueIpMatch() {
    return shouldStopOnTrueIpMatch;
  }

  @Override
  public boolean shouldKillOnTrueIpMatch() {
    return shouldKillOnTrueIpMatch;
  }

  @Override
  public InetAddress getTrueIp() {
    return trueIp;
  }

  @Override
  public Duration getCheckInterval() {
    return checkInterval;
  }

  @Override
  public boolean shouldNotifyDiscordWebhook() {
    return shouldNotifyDiscordWebhook;
  }

  @Override
  public String getDiscordWebhookUrl() {
    return discordWebhookUrl;
  }

  @Override
  public boolean shouldNotifyEmail() {
    return shouldNotifyEmail;
  }

  @Override
  public String getEmailSmtpHost() {
    return emailSmtpHost;
  }

  @Override
  public int getEmailSmtpPort() {
    return emailSmtpPort;
  }

  @Override
  public String getEmailSmtpUsername() {
    return emailSmtpUsername;
  }

  @Override
  public String getEmailSmtpPassword() {
    return emailSmtpPassword;
  }

  @Override
  public String getEmailFromName() {
    return emailFromName;
  }

  @Override
  public String getEmailFromAddress() {
    return emailFromAddress;
  }

  @Override
  public String getEmailToAddress() {
    return emailToAddress;
  }

  @Override
  public String[] getDockerContainerIds() {
    return dockerContainerIds;
  }

  public static boolean supports() {
    return System.getenv(ENV_CHECK_INTERVAL) != null;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("shouldStopOnTrueIpMatch", shouldStopOnTrueIpMatch)
        .add("shouldKillOnTrueIpMatch", shouldKillOnTrueIpMatch)
        .add("trueIp", trueIp)
        .add("checkInterval", checkInterval)
        .add("shouldNotifyDiscordWebhook", shouldNotifyDiscordWebhook)
        .add("discordWebhookUrl", discordWebhookUrl)
        .add("dockerContainerIds", dockerContainerIds)
        .add("shouldNotifyEmail", shouldNotifyEmail)
        .add("emailSmtpHost", emailSmtpHost)
        .add("emailSmtpPort", emailSmtpPort)
        .add("emailSmtpUsername", emailSmtpUsername)
        .add("emailSmtpPassword", emailSmtpPassword)
        .add("emailFromAddress", emailFromAddress)
        .add("emailFromName", emailFromName)
        .add("emailToAddress", emailToAddress)
        .toString();
  }
}
