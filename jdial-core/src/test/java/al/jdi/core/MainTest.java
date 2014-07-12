package al.jdi.core;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.jdial.common.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class MainTest {

  @Mock
  private Service configuracoesService;
  @Mock
  private Service devolveRegistroService;
  @Mock
  private Service estoqueLivresService;
  @Mock
  private Service estoqueLivresAgendados;
  @Mock
  private Service gerenciadorAgentesService;
  @Mock
  private Service gerenciadorLigacoesService;
  @Mock
  private Service gerenciadorFatorKService;
  @Mock
  private Service dialerService;
  @Mock
  private ShutdownHook.Factory shutdownHookFactory;
  @Mock
  private Service dialerCtiManagerService;

  private Main main;

  @Before
  public void setUp() {
    initMocks(this);
    main =
        new Main(configuracoesService, devolveRegistroService, estoqueLivresService,
            estoqueLivresAgendados, dialerCtiManagerService, gerenciadorAgentesService,
            gerenciadorLigacoesService, gerenciadorFatorKService, dialerService,
            shutdownHookFactory);
  }

  @Test
  public void start() throws Exception {
    main.start();
    verify(configuracoesService).start();
    verify(devolveRegistroService).start();
    verify(estoqueLivresService).start();
    verify(estoqueLivresAgendados).start();
    verify(dialerCtiManagerService).start();
    verify(gerenciadorAgentesService).start();
    verify(gerenciadorLigacoesService).start();
    verify(gerenciadorFatorKService).start();
    verify(dialerService).start();
  }

}
