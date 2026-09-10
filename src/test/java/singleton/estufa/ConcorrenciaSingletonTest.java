package singleton.estufa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

class ConcorrenciaSingletonTest {
    @Test
    @Timeout(30)
    void deveCriarUmaUnicaInstanciaComCemThreads() throws Exception {
        // Surefire inicia uma JVM por classe: ninguém aquece o Singleton antes da corrida.
        var prontas = new CountDownLatch(100);
        var inicio = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(100);
        var resultados = new ArrayList<Future<ConfiguracaoEstufa>>();
        try {
            for (int i = 0; i < 100; i++) {
                resultados.add(executor.submit(() -> {
                    prontas.countDown();
                    if (!inicio.await(15, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Barreira não liberada.");
                    }
                    return ConfiguracaoEstufa.getInstance();
                }));
            }
            assertTrue(prontas.await(15, TimeUnit.SECONDS), "As 100 threads devem estar prontas.");
            inicio.countDown();
            var identidades = Collections.newSetFromMap(
                    new IdentityHashMap<ConfiguracaoEstufa, Boolean>());
            for (var resultado : resultados) {
                identidades.add(resultado.get(5, TimeUnit.SECONDS));
            }
            assertEquals(1, identidades.size());
            System.out.println("Concorrência: 100 threads; instâncias distintas por identidade: " + identidades.size());
        } finally {
            inicio.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}
