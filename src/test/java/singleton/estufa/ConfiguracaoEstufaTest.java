package singleton.estufa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoEstufaTest {
    @BeforeEach
    void prepararLimites() {
        ConfiguracaoEstufa.getInstance().configurarLimites(18, 30, 40, 80);
    }

    @Test
    void deveRetornarMesmaIdentidade() {
        var primeira = ConfiguracaoEstufa.getInstance();
        var segunda = ConfiguracaoEstufa.getInstance();
        assertTrue(primeira == segunda);
        System.out.println("Identidade por ==: " + (primeira == segunda));
    }

    @Test
    void deveCompartilharEstadoEntreClientes() {
        var painel = new PainelOperador();
        var controlador = new ControladorClima();
        painel.aplicarPerfilMudas();
        assertSame(painel.getConfiguracao(), controlador.getConfiguracao());
        assertEquals("AQUECER | IRRIGAR", controlador.processarLeitura(19, 45));
    }

    @Test
    void deveBloquearReflexaoAposInicializacao() throws Exception {
        var construtor = ConfiguracaoEstufa.class.getDeclaredConstructor();
        construtor.setAccessible(true);
        var erro = assertThrows(InvocationTargetException.class, construtor::newInstance);
        assertInstanceOf(IllegalStateException.class, erro.getCause());
    }

    @Test
    void deveBloquearClonagem() {
        assertThrows(CloneNotSupportedException.class,
                () -> ConfiguracaoEstufa.getInstance().clone());
    }

    @Test
    void devePreservarIdentidadeEConfiguracaoAtualNaDesserializacao() throws Exception {
        var original = ConfiguracaoEstufa.getInstance();
        var bytes = new ByteArrayOutputStream();
        try (var saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(original);
        }
        original.configurarLimites(20, 28, 55, 75);
        try (var entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            var restaurada = (ConfiguracaoEstufa) entrada.readObject();
            assertSame(original, restaurada);
            assertEquals("AQUECER | IRRIGAR", restaurada.avaliarCondicoes(19, 45));
        }
    }

    @Test
    void deveRecomendarAcoesConformeLimites() {
        var configuracao = ConfiguracaoEstufa.getInstance();
        assertEquals("AQUECER | IRRIGAR", configuracao.avaliarCondicoes(17, 39));
        assertEquals("VENTILAR | REDUZIR_UMIDADE", configuracao.avaliarCondicoes(31, 81));
        assertEquals("TEMPERATURA_OK | UMIDADE_OK", configuracao.avaliarCondicoes(24, 60));
    }

    @Test
    void deveAceitarLeiturasExatamenteNosLimites() {
        var configuracao = ConfiguracaoEstufa.getInstance();
        assertEquals("TEMPERATURA_OK | UMIDADE_OK", configuracao.avaliarCondicoes(18, 40));
        assertEquals("TEMPERATURA_OK | UMIDADE_OK", configuracao.avaliarCondicoes(30, 80));
    }

    @Test
    void deveRejeitarConfiguracaoInvalidaSemAlterarEstado() {
        var configuracao = ConfiguracaoEstufa.getInstance();
        assertThrows(IllegalArgumentException.class, () -> configuracao.configurarLimites(10, 35, -1, 90));
        assertThrows(IllegalArgumentException.class, () -> configuracao.configurarLimites(30, 18, 40, 80));
        assertThrows(IllegalArgumentException.class, () -> configuracao.configurarLimites(Double.NaN, 30, 40, 80));
        assertThrows(IllegalArgumentException.class, () -> configuracao.configurarLimites(18, 30, 40, 101));
        assertEquals("AQUECER | IRRIGAR", configuracao.avaliarCondicoes(17, 39));
    }

    @Test
    void deveRejeitarLeiturasInvalidas() {
        var configuracao = ConfiguracaoEstufa.getInstance();
        assertThrows(IllegalArgumentException.class, () -> configuracao.avaliarCondicoes(Double.POSITIVE_INFINITY, 60));
        assertThrows(IllegalArgumentException.class, () -> configuracao.avaliarCondicoes(24, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> configuracao.avaliarCondicoes(24, 101));
        assertThrows(IllegalArgumentException.class, () -> configuracao.avaliarCondicoes(24, -1));
    }
}
