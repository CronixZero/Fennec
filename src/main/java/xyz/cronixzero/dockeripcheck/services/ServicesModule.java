package xyz.cronixzero.dockeripcheck.services;

import com.google.inject.Binder;
import com.google.inject.Module;

public class ServicesModule implements Module {

  @Override
  public void configure(Binder binder) {
    binder.bind(DockerService.class).asEagerSingleton();
  }
}
