package network.darkhelmet.prism.api.actions;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public abstract class Action {
    /**
     * The key.
     */
    private String key;

    /**
     * Construct a new action.
     *
     * @param key
     */
    public Action(String key) {
        this.key = key;
    }

    /**
     * Get the key.
     *
     * @return The key
     */
    public String key() {
        return key;
    }

    /**
     * Get a builder.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * The block state.
         */
        private BlockState blockState;

        /**
         * The type.
         */
        private String key;

        /**
         * Set the block state from a block.
         *
         * @param block The block
         * @return The builder
         */
        public Builder block(Block block) {
            return blockState(block.getState());
        }

        /**
         * Set the block state.
         *
         * @param blockState The block state
         * @return The builder
         */
        public Builder blockState(BlockState blockState) {
            this.blockState = blockState;
            return this;
        }

        /**
         * Set the key.
         *
         * @param key The key
         * @return The builder
         */
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Build a new action.
         *
         * @return The action
         */
        public Action build() {
            if (this.blockState != null) {
                return new BlockStateAction(key, blockState);
            }

            return null;
        }
    }
}
