package net.danieljurado.dialer.gerenciadorligacoes;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.jdi.cti.PredictiveListener;

class PredictiveListenerImpl implements PredictiveListener {

  public interface Factory {
    PredictiveListener create(GerenciadorLigacoesImpl owner);
  }

  private static final Logger logger = LoggerFactory.getLogger(PredictiveListenerImpl.class);

  private final GerenciadorLigacoesImpl owner;

  @Inject
  PredictiveListenerImpl(GerenciadorLigacoesImpl owner) {
    this.owner = owner;
    logger.debug("Iniciando {}", this);
  }

  @Override
  public void chamadaAtendida(int callId) {
    try {
      owner.chamadaAtendida(this, callId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public void chamadaEmFila(int callId) {
    try {
      owner.chamadaEmFila(this, callId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public void chamadaEncerrada(int callId, int causa) {
    try {
      owner.chamadaEncerrada(this, callId, causa);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public void chamadaErro(Exception e) {
    try {
      owner.chamadaErro(this, e);
    } catch (Exception e1) {
      logger.error(e.getMessage(), e1);
    }
  }

  @Override
  public void chamadaIniciada(int callId) {
    try {
      owner.chamadaIniciada(this, callId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public void chamadaInvalida(int callId, int causa) {
    try {
      owner.chamadaInvalida(this, callId, causa);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public void chamadaNoAgente(int callId, String agente) {
    try {
      owner.chamadaNoAgente(this, callId, agente);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }

}
