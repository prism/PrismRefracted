package network.darkhelmet.prism.database;

import network.darkhelmet.prism.utils.IntPair;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IdMapQuery {
    void findMaterial(int blockId, int blockSubid, BiConsumer<String, String> success);

    void findIds(String material, String state, BiConsumer<Integer, Integer> success);

    void findAllIds(String material, Consumer<List<IntPair>> success);

    void findAllIds(String material, Consumer<List<IntPair>> success, Runnable failure);

    void findAllIdsPartial(String material, String stateLike, Consumer<List<IntPair>> success);

    void map(String material, String state, int blockId, int blockSubid);

    int mapAutoId(String material, String state);
}
