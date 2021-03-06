package al.jdi.core.devolveregistro;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

import al.jdi.core.devolveregistro.DefaultDevolveRegistro.Bean;

public class DevolveRegistroModule {

  @Retention(RUNTIME)
  @Target({METHOD, FIELD, PARAMETER, TYPE})
  @Qualifier
  public @interface DevolveRegistroService {
  }

  @Retention(RUNTIME)
  @Target({METHOD, FIELD, PARAMETER, TYPE})
  @Qualifier
  public @interface ThreadCountParameter {
  }

  private static final int THREAD_COUNT = 10;

  @Produces
  public ExecutorService getExecutorService() {
    return Executors.newFixedThreadPool(THREAD_COUNT, new ThreadFactory() {
      private int i;

      @Override
      public Thread newThread(Runnable arg0) {
        Thread t = new Thread(arg0, "DevolveRegistro-" + ++i);
        t.setDaemon(true);
        return t;
      }
    });
  }

  @Produces
  public BlockingQueue<Bean> getBlockingQueue() {
    return new LinkedBlockingQueue<Bean>();
  }

  @Produces
  public List<ProcessoDevolucao> getProcessosDevolucao(@Any Instance<ProcessoDevolucao> processos) {
    LinkedList<ProcessoDevolucao> localList = new LinkedList<ProcessoDevolucao>();
    for (ProcessoDevolucao processoDevolucao : processos)
      localList.add(processoDevolucao);
    Collections.sort(localList);
    return Collections.unmodifiableList(localList);
  }

  @Produces
  @ThreadCountParameter
  public int getThreadCount() {
    return THREAD_COUNT;
  }

}
