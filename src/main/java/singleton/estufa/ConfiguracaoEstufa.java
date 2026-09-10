package singleton.estufa;

import java.io.Serial;
import java.io.Serializable;

public final class ConfiguracaoEstufa implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static volatile ConfiguracaoEstufa instance;

    private double temperaturaMinima = 18.0;
    private double temperaturaMaxima = 30.0;
    private double umidadeMinima = 40.0;
    private double umidadeMaxima = 80.0;

    private ConfiguracaoEstufa() {
        // O mesmo monitor da criação normal impede reflexão após a publicação.
        // Não é uma barreira contra reflexão antes do primeiro getInstance().
        synchronized (ConfiguracaoEstufa.class) {
            if (instance != null) {
                throw new IllegalStateException("A configuração da estufa já existe.");
            }
        }
    }

    public static ConfiguracaoEstufa getInstance() {
        // DCL mantém a criação lazy; volatile publica o objeto completamente
        // inicializado e a segunda verificação evita duas criações concorrentes.
        ConfiguracaoEstufa resultado = instance;
        if (resultado == null) {
            synchronized (ConfiguracaoEstufa.class) {
                resultado = instance;
                if (resultado == null) {
                    resultado = new ConfiguracaoEstufa();
                    instance = resultado;
                }
            }
        }
        return resultado;
    }

    public synchronized void configurarLimites(double temperaturaMinima,
            double temperaturaMaxima, double umidadeMinima, double umidadeMaxima) {
        validarFaixa(temperaturaMinima, temperaturaMaxima, "temperatura");
        validarFaixa(umidadeMinima, umidadeMaxima, "umidade");
        if (umidadeMinima < 0 || umidadeMaxima > 100) {
            throw new IllegalArgumentException("Umidade deve estar entre 0 e 100%.");
        }
        // Valida tudo antes de atualizar, evitando configuração parcialmente aplicada.
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
        this.umidadeMinima = umidadeMinima;
        this.umidadeMaxima = umidadeMaxima;
    }

    public synchronized String avaliarCondicoes(double temperatura, double umidade) {
        if (!Double.isFinite(temperatura) || !Double.isFinite(umidade)
                || umidade < 0 || umidade > 100) {
            throw new IllegalArgumentException("Leitura dos sensores inválida.");
        }
        String acaoTemperatura = temperatura < temperaturaMinima ? "AQUECER"
                : temperatura > temperaturaMaxima ? "VENTILAR" : "TEMPERATURA_OK";
        String acaoUmidade = umidade < umidadeMinima ? "IRRIGAR"
                : umidade > umidadeMaxima ? "REDUZIR_UMIDADE" : "UMIDADE_OK";
        return acaoTemperatura + " | " + acaoUmidade;
    }

    private static void validarFaixa(double minimo, double maximo, String nome) {
        if (!Double.isFinite(minimo) || !Double.isFinite(maximo) || minimo >= maximo) {
            throw new IllegalArgumentException("Faixa de " + nome + " inválida.");
        }
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton não permite clonagem.");
    }

    @Serial
    private Object readResolve() {
        // Desserialização recupera a identidade canônica, sem restaurar limites antigos.
        return getInstance();
    }
}
