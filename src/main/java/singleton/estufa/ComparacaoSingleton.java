package singleton.estufa;

public final class ComparacaoSingleton {
    private ComparacaoSingleton() {
    }

    // Eager: publicação segura e simples; cria mesmo sem chamada a getInstance().
    public static final class Eager {
        private static final Eager instance = new Eager();

        private Eager() {
        }

        public static Eager getInstance() {
            return instance;
        }
    }

    // Holder: lazy e seguro pela inicialização de classes; sem volatile ou lock manual.
    public static final class Lazy {
        private Lazy() {
        }

        private static final class Holder {
            private static final Lazy instance = new Lazy();
        }

        public static Lazy getInstance() {
            return Holder.instance;
        }
    }

    // Effective Java, Item 3: enum protege identidade em reflexão e serialização;
    // sintaxe específica e impossibilidade de estender outra classe são limitações.
    // As variantes acima são comparações mínimas, sem o endurecimento da classe principal.
    public enum EnumSingleton {
        INSTANCE
    }
}
