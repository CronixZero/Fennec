package xyz.cronixzero.dockeripcheck.notifications;

import com.github.dockerjava.api.model.Container;
import com.google.inject.Inject;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateException;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import jakarta.activation.FileDataSource;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import xyz.cronixzero.dockeripcheck.annotations.HtmlTemplateEngine;
import xyz.cronixzero.dockeripcheck.annotations.PlaintextTemplateEngine;

@Slf4j
public class MailNotificationService implements NotificationService {

  private static final String LOGO = "resources/images/logo.png";
  private static final String BACKGROUND = "resources/images/background.png";

  private static final String MORE_RESULTS_TEMPLATE = "MoreResults.jte";
  private static final String CONTAINER_STOPPED_TEMPLATE = "ContainerStopped.jte";
  private static final String CONTAINER_KILLED_TEMPLATE = "ContainerKilled.jte";
  private static final String IP_UNKNOWN_TEMPLATE = "IpUnknown.jte";

  @Inject
  @HtmlTemplateEngine
  private TemplateEngine htmlTemplateEngine;
  @Inject
  @PlaintextTemplateEngine
  private TemplateEngine plaintextTemplateEngine;
  @Inject
  private Mailer mailer;

  @Override
  public void sendUnknownIpNotification(Container container) {
    mailer.sendMail(generateUnknownIpMail(container));
  }

  @Override
  public void sendStoppedNotification(Container container) {
    mailer.sendMail(generateStoppedMail(container));
  }

  @Override
  public void sendKilledNotification(Container container) {
    mailer.sendMail(generateKilledMail(container));
  }

  @Override
  public void sendMoreResultsNotification(Container container, List<String> results) {
    mailer.sendMail(generateMoreResultsMail(container, results));
  }

  private Email generateMoreResultsMail(Container container, List<String> results) {
    EnumMap<ContentType, String> content = renderMoreResultsMail(container, results);

    return EmailBuilder.startingBlank()
        .withSubject("There have been more Results to the IP Fetch Request than one")
        .withHTMLText(content.get(ContentType.Html))
        .withPlainText(content.get(ContentType.Plain))
        .buildEmail();
  }

  private Email generateStoppedMail(Container container) {
    EnumMap<ContentType, String> content = renderStoppedMail(container);

    return EmailBuilder.startingBlank()
        .withSubject("IP leaked! Container has been stopped")
        .withHTMLText(content.get(ContentType.Html))
        .withPlainText(content.get(ContentType.Plain))
        .buildEmail();
  }

  private Email generateKilledMail(Container container) {
    log.info("Generating Killed Mail");
    EnumMap<ContentType, String> content = renderKilledMail(container);
    log.info("Generated Killed Mail");

    return EmailBuilder.startingBlank()
        .withSubject("IP leaked! Container has been killed")
        .withHTMLText(content.get(ContentType.Html))
        .withPlainText(content.get(ContentType.Plain))
        .withEmbeddedImage("background", new FileDataSource(BACKGROUND))
        .withEmbeddedImage("logo", new FileDataSource(LOGO))
        .buildEmail();
  }

  private Email generateUnknownIpMail(Container container) {
    EnumMap<ContentType, String> content = renderUnknownIpMail(container);

    return EmailBuilder.startingBlank()
        .withSubject("Could not retrieve the IP Address of a Container. Container has been stopped")
        .withHTMLText(content.get(ContentType.Html))
        .withPlainText(content.get(ContentType.Plain))
        .buildEmail();
  }

  private EnumMap<ContentType, String> renderMoreResultsMail(Container container, List<String> results) {
    EnumMap<ContentType, String> content = new EnumMap<>(ContentType.class);
    Map<String, Object> model = Map.of("container", container, "results", results);

    TemplateOutput htmlOutput = new StringOutput();
    TemplateOutput plainOutput = new StringOutput();

    try {
      htmlTemplateEngine.render(MORE_RESULTS_TEMPLATE, model, htmlOutput);
      plaintextTemplateEngine.render(MORE_RESULTS_TEMPLATE, model, plainOutput);
    } catch (TemplateException e) {
      log.error("Encountered an error while rendering the MoreResults template", e);
      throw new IllegalStateException("Could not render the Template", e);
    }

    content.put(ContentType.Html, htmlOutput.toString());
    content.put(ContentType.Plain, plainOutput.toString());

    return content;
  }

  private EnumMap<ContentType, String> renderStoppedMail(Container container) {
    EnumMap<ContentType, String> content = new EnumMap<>(ContentType.class);

    TemplateOutput htmlOutput = new StringOutput();
    TemplateOutput plainOutput = new StringOutput();

    try {
      htmlTemplateEngine.render(CONTAINER_STOPPED_TEMPLATE, container, htmlOutput);
      plaintextTemplateEngine.render(CONTAINER_STOPPED_TEMPLATE, container, plainOutput);
    } catch (TemplateException e) {
      log.error("Encountered an error while rendering the ContainerStopped template", e);
      throw new IllegalStateException("Could not render the Template", e);
    }

    content.put(ContentType.Html, htmlOutput.toString());
    content.put(ContentType.Plain, plainOutput.toString());

    return content;
  }

  private EnumMap<ContentType, String> renderKilledMail(Container container) {
    EnumMap<ContentType, String> content = new EnumMap<>(ContentType.class);

    TemplateOutput htmlOutput = new StringOutput();
    TemplateOutput plaintextOutput = new StringOutput();

    try {
      htmlTemplateEngine.render(CONTAINER_KILLED_TEMPLATE, container, htmlOutput);
      plaintextTemplateEngine.render(CONTAINER_KILLED_TEMPLATE, container, plaintextOutput);
    } catch (TemplateException e) {
      log.error("Encountered an error while rendering the ContainerKilled template", e);
      throw new IllegalStateException("Could not render the Template", e);
    }
    content.put(ContentType.Html, htmlOutput.toString());
    content.put(ContentType.Plain, plaintextOutput.toString());

    return content;
  }

  private EnumMap<ContentType, String> renderUnknownIpMail(Container container) {
    EnumMap<ContentType, String> content = new EnumMap<>(ContentType.class);

    TemplateOutput htmlOutput = new StringOutput();
    TemplateOutput plainOutput = new StringOutput();
    try {
      htmlTemplateEngine.render(IP_UNKNOWN_TEMPLATE, container, htmlOutput);
      plaintextTemplateEngine.render(IP_UNKNOWN_TEMPLATE, container, htmlOutput);
    } catch (TemplateException e) {
      log.error("Encountered an error while rendering the IpUnknown template", e);
      throw new IllegalStateException("Could not render the Template", e);
    }

    content.put(ContentType.Html, htmlOutput.toString());
    content.put(ContentType.Plain, plainOutput.toString());

    return content;
  }
}
