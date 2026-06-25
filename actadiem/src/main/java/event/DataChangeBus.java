package event;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
