package al.jdi.core;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import al.jdi.common.Service;
import al.jdi.core.devolveregistro.DevolveRegistroModule.DevolveRegistroService;
import al.jdi.cti.DialerCtiManagerModule.DialerCtiManagerService;

class Main {

  private final Service devolveRegistroService;
  private final Service dialerCtiManagerService;
  private final ShutdownHook.Factory shutdownHookFactory;

  @Inject
  Main(@DevolveRegistroService Service devolveRegistroService,
      @DialerCtiManagerService Service dialerCtiManagerService,
      ShutdownHook.Factory shutdownHookFactory) {
    this.devolveRegistroService = devolveRegistroService;
    this.dialerCtiManagerService = dialerCtiManagerService;
    this.shutdownHookFactory = shutdownHookFactory;
  }

  @PostConstruct
  public void start() {
    ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
    Runtime.getRuntime().addShutdownHook(
        new Thread(shutdownHookFactory.create(devolveRegistroService, dialerCtiManagerService),
            "EventoShutdown"));

    devolveRegistroService.start();
    dialerCtiManagerService.start();
  }

  public void run(@Observes ContainerInitialized event) {}

}
