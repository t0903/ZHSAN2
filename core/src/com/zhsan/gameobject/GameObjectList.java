package com.zhsan.gameobject;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> implements Iterable<T> {

    private SortedMap<Integer, T> content = new TreeMap<>();
    private boolean unmodifiable = false;

    public GameObjectList(){}

    public GameObjectList(GameObjectList<T> old, boolean unmodifiable) {
        if (unmodifiable) {
            content = Collections.unmodifiableSortedMap(old.content);
        } else {
            content = new TreeMap<>(old.content);
        }
        this.unmodifiable = unmodifiable;
    }

    public void add(T obj) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        content.put(obj.getId(), obj);
    }

    public void addAll(GameObjectList<T> objs) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        content.putAll(objs.content);
    }

    public T get(int id) {
        return content.get(id);
    }

    public T getFirst() {
        return content.get(content.firstKey());
    }

    @Override
    public Iterator<T> iterator() {
        // TODO make this be unmodifiable
        return content.values().iterator();
    }

    public GameObjectList<T> filter(Predicate<T> predicate) {
        return content.values().stream().filter(predicate).collect(new ToGameObjectList<>());
    }

    public int size() {
        return content.size();
    }

    /**
     * Return a new GameObjectList that is not modifiable
     * @return
     */
    public GameObjectList<T> asUnmodifiable() {
        GameObjectList<T> result = new GameObjectList<>(this, true);
        return result;
    }

    public class ToGameObjectList<T extends GameObject> implements Collector<T, GameObjectList<T>, GameObjectList<T>> {

        @Override
        public Supplier<GameObjectList<T>> supplier() {
            return GameObjectList<T>::new;
        }

        @Override
        public BiConsumer<GameObjectList<T>, T> accumulator() {
            return GameObjectList::add;
        }

        @Override
        public BinaryOperator<GameObjectList<T>> combiner() {
            return (x, y) -> {
                x.addAll(y);
                return x;
            };
        }

        @Override
        public Function<GameObjectList<T>, GameObjectList<T>> finisher() {
            return x -> x;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.IDENTITY_FINISH,
                    Characteristics.CONCURRENT,
                    Characteristics.UNORDERED));
        }
    }

}
