package network.darkhelmet.prism.api;

public interface ChangeResult {

    BlockStateChange getBlockStateChange();

    ChangeResultType getType();
}
