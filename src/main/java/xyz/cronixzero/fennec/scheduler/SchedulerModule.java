package xyz.cronixzero.fennec.scheduler;

import com.google.inject.Binder;
import com.google.inject.Module;

public class SchedulerModule implements Module {

  @Override
  public void configure(Binder binder) {
    binder.bind(IpCheckScheduler.class).asEagerSingleton();
  }
}
