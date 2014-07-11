package net.danieljurado.dialer.modelo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import net.danieljurado.dialer.configuracoes.Configuracoes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import al.jdi.dao.model.Cliente;

public class DiscavelFactoryTsaImplTest {

  @Mock
  private Configuracoes configuracoes;
  @Mock
  private Cliente cliente;

  private DiscavelFactoryTsaImpl discavelFactoryTsaImpl;

  @Before
  public void setUp() throws Exception {
    discavelFactoryTsaImpl = new DiscavelFactoryTsaImpl(configuracoes);
  }

  @Test
  public void createDeveriaCriarDiscavel() {
    Discavel discavel = discavelFactoryTsaImpl.create(cliente);
    assertThat(discavel, is(not(nullValue(Discavel.class))));
  }

  @Test
  public void createDeveriaCriarDiscavelComCliente() {
    Discavel discavel = discavelFactoryTsaImpl.create(cliente);
    assertThat(discavel.getCliente(), is(sameInstance(cliente)));
  }

}
