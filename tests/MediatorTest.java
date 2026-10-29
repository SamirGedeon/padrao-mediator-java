import java.util.List;

/** Casos de teste para a comunicação coordenada pelo Mediator. */
public final class MediatorTest {
    private static int verificacoes;
    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        verificacoes++;
    }
    private static void falha(Class<? extends RuntimeException> tipo, Runnable acao, String mensagem) {
        try { acao.run(); }
        catch (RuntimeException erro) {
            verificar(tipo.isInstance(erro), mensagem + " (tipo de erro inesperado)");
            return;
        }
        throw new AssertionError(mensagem + " (deveria falhar)");
    }
    private static void contem(Main.Participante participante, String esperado, String mensagem) {
        verificar(participante.getMensagensRecebidas().contains(esperado), mensagem);
    }
    public static void main(String[] args) {
        Main.SalaChat sala = new Main.SalaChat();
        Main.Participante ana = new Main.Participante("Ana");
        Main.Participante bruno = new Main.Participante("Bruno");
        Main.Participante carla = new Main.Participante("Carla");
        sala.registrar(ana);
        sala.registrar(bruno);
        sala.registrar(carla);
        ana.enviar("Bom dia");
        verificar(ana.getMensagensRecebidas().isEmpty(), "Remetente não recebe a própria mensagem");
        contem(bruno, "Ana: Bom dia", "Bruno recebe mensagem de Ana");
        contem(carla, "Ana: Bom dia", "Carla recebe mensagem de Ana");
        bruno.enviar("Olá");
        contem(ana, "Bruno: Olá", "Ana recebe mensagem de Bruno");
        contem(carla, "Bruno: Olá", "Carla recebe mensagem de Bruno");
        verificar(bruno.getMensagensRecebidas().size() == 1, "Bruno não recebe a própria mensagem");

        sala.registrar(carla);
        ana.enviar("Mensagem após registro repetido");
        verificar(carla.getMensagensRecebidas().size() == 3, "Registro repetido não duplica participante");
        falha(IllegalArgumentException.class,
            () -> sala.enviar("intrusa", new Main.Participante("Dora")),
            "Remetente não registrado é rejeitado");
        Main.Participante naoRegistrado = new Main.Participante("Eva");
        falha(IllegalStateException.class, () -> naoRegistrado.enviar("sem sala"), "Envio antes do registro é rejeitado");
        falha(NullPointerException.class, () -> ana.enviar(null), "Mensagem nula é rejeitada");
        falha(IllegalArgumentException.class, () -> new Main.Participante("  "), "Nome vazio é rejeitado");
        List<String> mensagens = ana.getMensagensRecebidas();
        falha(UnsupportedOperationException.class, () -> mensagens.add("alteração externa"), "Caixa de entrada não pode ser alterada externamente");
        verificar(bruno.getMensagensRecebidas().size() == 2, "Caixas de entrada dos participantes são independentes");
        System.out.println(verificacoes + " verificações aprovadas.");
    }
}

