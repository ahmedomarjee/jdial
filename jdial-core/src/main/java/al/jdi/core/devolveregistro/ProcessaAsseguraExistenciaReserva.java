package al.jdi.core.devolveregistro;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;

import al.jdi.core.modelo.Ligacao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.EstadoCliente;
import al.jdi.dao.model.MotivoSistema;
import al.jdi.dao.model.ResultadoLigacao;

class ProcessaAsseguraExistenciaReserva implements ProcessoDevolucao {

  private final Logger logger;

  @Inject
  ProcessaAsseguraExistenciaReserva(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean accept(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory) {
    EstadoCliente estadoCliente =
        daoFactory.getEstadoClienteDao().procura("Reservado pelo Discador");
    if (cliente.getEstadoCliente().equals(estadoCliente)) {
      logger.info("Possui reserva e esta com status reservado {}", cliente);
      return false;
    }
    return true;
  }

  @Override
  public boolean executa(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory) {
    int motivoFinalizacao = ligacao.getMotivoFinalizacao();
    if (motivoFinalizacao == MotivoSistema.LEI_NAO_PERTURBE.getCodigo()
        || motivoFinalizacao == MotivoSistema.SEM_TELEFONES.getCodigo()) {
      logger.info("Nao tem reserva por DNC ou SEMTELEFONES {}", cliente);
    }
    logger.info("Nao tinha reserva {}", cliente);
    return true;
  }

  @Override
  public int compareTo(ProcessoDevolucao o) {
    return new CompareToBuilder().append(getOrdem(), o.getOrdem()).toComparison();
  }

  @Override
  public int getOrdem() {
    return 4;
  }

}
