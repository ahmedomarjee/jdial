package al.jdi.core.devolveregistro;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import al.jdi.core.devolveregistro.ModificadorResultado.ResultadosConhecidos;
import al.jdi.core.modelo.Ligacao;
import al.jdi.core.tenant.Tenant;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.MotivoSistema;
import al.jdi.dao.model.ResultadoLigacao;

class ModificadorResultadoUraReversa implements ModificadorResultadoFilter {

  private static final Logger logger = getLogger(ModificadorResultadoUraReversa.class);

  @Override
  public boolean accept(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    if (!tenant.getConfiguracoes().isUraReversa())
      return false;

    if (ligacao.isNoAgente())
      return false;

    ResultadoLigacao resultadoLigacaoAtendida =
        daoFactory.getResultadoLigacaoDao().procura(MotivoSistema.ATENDIDA.getCodigo(),
            tenant.getCampanha());
    ResultadoLigacao resultadoLigacaoSemAgentes =
        daoFactory.getResultadoLigacaoDao().procura(ResultadosConhecidos.SEM_AGENTES.getCodigo(),
            tenant.getCampanha());

    boolean semAgentes = resultadoLigacao.equals(resultadoLigacaoSemAgentes);
    boolean atendida = resultadoLigacao.equals(resultadoLigacaoAtendida);

    if (!(semAgentes || atendida))
      return false;

    return true;
  }

  @Override
  public ResultadoLigacao modifica(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    Cliente cliente = ligacao.getDiscavel().getCliente();
    if (ligacao.isFoiPraFila()) {
      logger.info("Alterando resultado por abandono Ura reversa {}", cliente);
      return daoFactory.getResultadoLigacaoDao().procura(MotivoSistema.ABANDONO_URA.getCodigo(),
          tenant.getCampanha());
    }
    logger.info("Alterando resultado por sem interesse Ura reversa {}", cliente);
    return daoFactory.getResultadoLigacaoDao().procura(MotivoSistema.SEM_INTERESSE_URA.getCodigo(),
        tenant.getCampanha());
  }

}
