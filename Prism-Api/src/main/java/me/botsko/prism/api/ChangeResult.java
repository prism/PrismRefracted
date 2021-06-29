package me.botsko.prism.api;

public interface ChangeResult {

    BlockStateChange getBlockStateChange();

    ChangeResultType getType();
}
