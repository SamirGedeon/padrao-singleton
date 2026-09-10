package singleton.estufa;

public final class ControladorClima {
    public ConfiguracaoEstufa getConfiguracao() {
        return ConfiguracaoEstufa.getInstance();
    }

    public String processarLeitura(double temperatura, double umidade) {
        return getConfiguracao().avaliarCondicoes(temperatura, umidade);
    }
}
