package singleton.estufa;

public final class PainelOperador {
    public ConfiguracaoEstufa getConfiguracao() {
        return ConfiguracaoEstufa.getInstance();
    }

    public void aplicarPerfilMudas() {
        getConfiguracao().configurarLimites(20, 28, 55, 75);
    }
}
