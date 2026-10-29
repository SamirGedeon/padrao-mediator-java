import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Demonstra Mediator coordenando participantes de uma sala de conversa. */
public class Main {
    interface MediadorChat {
        void registrar(Participante participante);
        void enviar(String mensagem, Participante remetente);
    }

    static final class SalaChat implements MediadorChat {
        private final List<Participante> participantes = new ArrayList<>();
        public void registrar(Participante participante) {
            Objects.requireNonNull(participante, "participante");
            if (!participantes.contains(participante)) {
                participantes.add(participante);
                participante.definirMediador(this);
            }
        }
        public void enviar(String mensagem, Participante remetente) {
            Objects.requireNonNull(mensagem, "mensagem");
            Objects.requireNonNull(remetente, "remetente");
            if (!participantes.contains(remetente)) {
                throw new IllegalArgumentException("O remetente não está registrado na sala.");
            }
            for (Participante participante : new ArrayList<>(participantes)) {
                if (participante != remetente) participante.receber(mensagem, remetente.getNome());
            }
        }
    }

    static final class Participante {
        private final String nome;
        private MediadorChat mediador;
        private final List<String> caixaDeEntrada = new ArrayList<>();
        Participante(String nome) {
            if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("O nome não pode ser vazio.");
            this.nome = nome;
        }
        public String getNome() { return nome; }
        private void definirMediador(MediadorChat mediador) { this.mediador = mediador; }
        public void enviar(String mensagem) {
            if (mediador == null) throw new IllegalStateException("Participante ainda não está registrado.");
            mediador.enviar(Objects.requireNonNull(mensagem, "mensagem"), this);
        }
        private void receber(String mensagem, String remetente) {
            caixaDeEntrada.add(remetente + ": " + mensagem);
        }
        public List<String> getMensagensRecebidas() {
            return Collections.unmodifiableList(caixaDeEntrada);
        }
    }

    public static void main(String[] args) {
        SalaChat sala = new SalaChat();
        Participante ana = new Participante("Ana");
        Participante bruno = new Participante("Bruno");
        Participante carla = new Participante("Carla");
        sala.registrar(ana);
        sala.registrar(bruno);
        sala.registrar(carla);
        ana.enviar("Olá, pessoal!");
        bruno.enviar("Oi, Ana!");
        System.out.println("Mensagens de Bruno: " + bruno.getMensagensRecebidas());
        System.out.println("Mensagens de Carla: " + carla.getMensagensRecebidas());
    }
}
