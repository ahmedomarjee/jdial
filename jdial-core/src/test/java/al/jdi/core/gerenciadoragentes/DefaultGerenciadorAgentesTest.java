package al.jdi.core.gerenciadoragentes;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import al.jdi.common.Engine;
import al.jdi.core.configuracoes.Configuracoes;
import al.jdi.core.tenant.Tenant;
import al.jdi.cti.CtiManager;
import al.jdi.cti.DialerCtiManager;
import al.jdi.dao.beans.CampanhaDao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Grupo;

public class DefaultGerenciadorAgentesTest {

  private static final Integer RESERVADOS = 10;
  private static final String CAMPANHA = "CAMPANHA";
  private static final String GRUPO = "GRUPO";
  private static final Integer LIVRES = 5;
  private static final Integer SIMULTANEOS = 50;

  private DefaultGerenciadorAgentes gerenciadorAgentesImpl;

  @Mock
  private DialerCtiManager dialerCtiManager;
  @Mock
  private Configuracoes configuracoes;
  @Mock
  private Engine.Factory engineFactory;
  @Mock
  private Provider<DaoFactory> daoFactoryProvider;
  @Mock
  private DaoFactory daoFactory;
  @Mock
  private CampanhaDao campanhaDao;
  @Mock
  private Campanha campanha;
  @Mock
  private Grupo grupo;
  @Mock
  private CtiManager ctiManager;
  @Mock
  private Tenant tenant;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(configuracoes.getNomeCampanha()).thenReturn(CAMPANHA);
    when(daoFactoryProvider.get()).thenReturn(daoFactory);
    when(daoFactory.getCampanhaDao()).thenReturn(campanhaDao);
    when(campanhaDao.procura(CAMPANHA)).thenReturn(campanha);
    when(campanha.getGrupo()).thenReturn(grupo);
    when(grupo.getCodigo()).thenReturn(GRUPO);
    when(dialerCtiManager.getAgentesLivres(GRUPO)).thenReturn(LIVRES);
    when(configuracoes.getQtdMaximaCanaisSimultaneos()).thenReturn(SIMULTANEOS);
    assertThat(LIVRES, is(not(equalTo(SIMULTANEOS))));
    when(configuracoes.getSistemaAtivo()).thenReturn(true);
    when(tenant.getConfiguracoes()).thenReturn(configuracoes);
    gerenciadorAgentesImpl =
        new DefaultGerenciadorAgentes(dialerCtiManager, engineFactory, daoFactoryProvider, tenant);
  }

  @Test
  public void getLivresDeveriaDescontarReservados() throws Exception {
    when(configuracoes.getQtdAgentesReservados()).thenReturn(RESERVADOS);
    assertThat(gerenciadorAgentesImpl.getLivres(), is(-RESERVADOS));
  }

  @Test
  public void obtemLivresDeveriaRetornarLivres() throws Exception {
    assertThat(gerenciadorAgentesImpl.obtemQtdAgentesLivres(daoFactory), is(LIVRES));
  }

  @Test
  public void obtemLivresDeveriaRetornarSimultaneos() throws Exception {
    when(configuracoes.isUraReversa()).thenReturn(true);
    assertThat(gerenciadorAgentesImpl.obtemQtdAgentesLivres(daoFactory), is(SIMULTANEOS));
  }

  @Test
  public void obtemLivresDeveriaRetornar0SemAgentesUraReversa() throws Exception {
    when(configuracoes.isUraReversa()).thenReturn(true);
    when(dialerCtiManager.getAgentesLivres(GRUPO)).thenReturn(0);
    assertThat(gerenciadorAgentesImpl.obtemQtdAgentesLivres(daoFactory), is(0));
  }

  @Test
  public void runNaoDeveriaExecutarSeForaServico() throws Exception {
    gerenciadorAgentesImpl.run();
    verify(dialerCtiManager, never()).getAgentesLivres(GRUPO);
  }

  @Test
  public void runNaoDeveriaExecutarSeSistemaInativo() throws Exception {
    gerenciadorAgentesImpl.providerInService(null);
    when(configuracoes.getSistemaAtivo()).thenReturn(false);
    verify(dialerCtiManager, never()).getAgentesLivres(GRUPO);
  }

  @Test
  public void runDeveriaExecutarSeForaServico() throws Exception {
    gerenciadorAgentesImpl.providerInService(null);
    gerenciadorAgentesImpl.run();
    verify(dialerCtiManager).getAgentesLivres(GRUPO);
  }

}
