package event;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Lightweight app-wide publish/subscribe hub used to keep long-lived panels in sync.
 *
 * <p>Producers (e.g. {@code ObjetivoPanel}, {@code TarefaPanel}) call {@link #publish(Topic)}
 * after a create/update/delete; consumers ({@code EventoPanel}, {@code MaterialPanel})
 * {@link #subscribe(Topic, Listener)} and reload their selector lists.
 *
 * <p>Consumers must {@link #unsubscribe(Topic, Listener)} when they leave the UI (e.g. in
 * {@code removeNotify()}) so listeners bound to disposed panels do not leak across
 * logout/login. {@link CopyOnWriteArrayList} keeps publish-while-iterating safe; all calls
 * are expected on the Swing EDT.
 */
public final class DataChangeBus {

    public enum Topic { OBJETIVOS, TAREFAS }

    public interface Listener {
        void onDataChanged();
    }

    private static final Map<Topic, List<Listener>> listeners = new EnumMap<>(Topic.class);

    static {
        for (Topic topic : Topic.values()) {
            listeners.put(topic, new CopyOnWriteArrayList<>());
        }
    }

    private DataChangeBus() {
    }

    public static void subscribe(Topic topic, Listener listener) {
        listeners.get(topic).add(listener);
    }

    public static void unsubscribe(Topic topic, Listener listener) {
        listeners.get(topic).remove(listener);
    }

    public static void publish(Topic topic) {
        for (Listener listener : listeners.get(topic)) {
            listener.onDataChanged();
        }
    }
}
