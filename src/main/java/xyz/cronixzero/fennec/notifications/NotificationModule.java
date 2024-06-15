package xyz.cronixzero.fennec.notifications;

import com.google.inject.Binder;
import com.google.inject.Module;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import xyz.cronixzero.fennec.annotations.HtmlTemplateEngine;
import xyz.cronixzero.fennec.annotations.PlaintextTemplateEngine;
import xyz.cronixzero.fennec.config.Configuration;

public class NotificationModule implements Module {

  private final Configuration config;

  public NotificationModule(Configuration config) {
    this.config = config;
  }

  @Override
  public void configure(Binder binder) {
    List<NotificationService> notificationServiceList = new ArrayList<>();
    binder.bind(Notifier.class).toInstance(new Notifier(notificationServiceList));

    binder.bind(TemplateEngine.class).annotatedWith(HtmlTemplateEngine.class)
        .toInstance(getHtmlTemplateEngine());
    binder.bind(TemplateEngine.class).annotatedWith(PlaintextTemplateEngine.class)
        .toInstance(getPlaintextTemplateEngine());

    Mailer mailer = getMailer();
    mailer.testConnection();
    binder.bind(Mailer.class).toInstance(mailer);

    if (config.shouldNotifyEmail()) {
      MailNotificationService mailNotificationService = new MailNotificationService();
      binder.requestInjection(mailNotificationService);
      notificationServiceList.add(mailNotificationService);
    }
  }

  private Mailer getMailer() {
    Email defaultEmail = EmailBuilder
        .startingBlank()
        .from(config.getEmailFromName(), config.getEmailFromAddress())
        .to(config.getEmailToAddress())
        .buildEmail();
    return MailerBuilder
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .withSMTPServer(config.getEmailSmtpHost(), config.getEmailSmtpPort(),
            config.getEmailSmtpUsername(), config.getEmailSmtpPassword())
        .withEmailDefaults(defaultEmail)
        .async()
        .buildMailer();
  }

  private TemplateEngine getHtmlTemplateEngine() {
    CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("resources", "mails"));
    return TemplateEngine.create(codeResolver, ContentType.Html);
  }

  private TemplateEngine getPlaintextTemplateEngine() {
    CodeResolver codeResolver = new DirectoryCodeResolver(
        Path.of("resources", "mails", "plaintext"));
    return TemplateEngine.create(codeResolver, ContentType.Plain);
  }
}
