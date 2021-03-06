package al.jdi.core.devolveregistro;

import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import al.jdi.core.modelo.Ligacao;
import al.jdi.core.tenant.Tenant;
import al.jdi.core.tratadorespecificocliente.TratadorEspecificoCliente;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Agendamento;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.HistoricoCliente;
import al.jdi.dao.model.ResultadoLigacao;

class ProcessaAgendamento implements ProcessoDevolucao {

  private static final Logger logger = getLogger(ProcessaAgendamento.class);

  private final TratadorEspecificoCliente.Factory tratadorEspecificoClienteFactory;

  @Inject
  ProcessaAgendamento(TratadorEspecificoCliente.Factory tratadorEspecificoClienteFactory) {
    this.tratadorEspecificoClienteFactory = tratadorEspecificoClienteFactory;
  }

  @Override
  public boolean accept(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    if (resultadoLigacao == null || resultadoLigacao.getIntervaloReagendamento() <= 0) {
      logger.info("Nao vai agendar {}", ligacao.getDiscavel().getCliente());
      return false;
    }
    return true;
  }

  @Override
  public boolean executa(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    Cliente cliente = ligacao.getDiscavel().getCliente();

    DateTime cal =
        new DateTime().minusMinutes(resultadoLigacao.getIntervaloDesteResultadoReagenda());

    if (daoFactory.getHistoricoLigacaoDao().procura(cliente, resultadoLigacao, cal).size() > 1) {
      logger.info("Nao vai agendar por ja ter resultado no intervalo {}", cliente);
      cliente.getAgendamento().clear();
      tratadorEspecificoClienteFactory.create(tenant, daoFactory).obtemClienteDao()
          .atualiza(cliente);
      return true;
    }

    logger.info("Reagendando para {} minutos por resultado {} {}",
        new Object[] {resultadoLigacao.getIntervaloReagendamento(), resultadoLigacao, cliente});

    DateTime calAgendamento =
        new DateTime().plusMinutes(resultadoLigacao.getIntervaloReagendamento());

    Agendamento agendamento = daoFactory.getAgendamentoDao().procura(cliente);

    if (agendamento == null) {
      agendamento = new Agendamento();
      agendamento.setCliente(cliente);
      agendamento.setAgendamento(calAgendamento);
      daoFactory.getAgendamentoDao().adiciona(agendamento);
    } else {
      agendamento.setAgendamento(calAgendamento);
      daoFactory.getAgendamentoDao().atualiza(agendamento);
    }

    HistoricoCliente historicoCliente = new HistoricoCliente();
    historicoCliente.setCliente(cliente);
    historicoCliente.setAgendamento(calAgendamento);
    historicoCliente.setEstadoCliente(cliente.getEstadoCliente());
    historicoCliente
        .setDescricao(String.format("Agendamento de registro por %s", resultadoLigacao));
    daoFactory.getHistoricoClienteDao().adiciona(historicoCliente);
    return true;
  }

  @Override
  public int compareTo(ProcessoDevolucao o) {
    return new CompareToBuilder().append(getOrdem(), o.getOrdem()).toComparison();
  }

  @Override
  public int getOrdem() {
    return 8;
  }

}
