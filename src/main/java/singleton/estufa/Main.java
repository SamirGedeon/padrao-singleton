package singleton.estufa;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        PainelOperador painel = new PainelOperador();
        ControladorClima controlador = new ControladorClima();
        painel.aplicarPerfilMudas();
        System.out.println("Mesma instância: "
                + (painel.getConfiguracao() == controlador.getConfiguracao()));
        System.out.println("Sensores: 19°C e 45% -> " + controlador.processarLeitura(19, 45));
        System.out.println("Sensores: 25°C e 60% -> " + controlador.processarLeitura(25, 60));
    }
}
