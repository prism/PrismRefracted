package network.darkhelmet.prism.actions;

import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IAction;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public abstract class Action implements IAction {
    /**
     * The type.
     */
    private ActionType type;

    /**
     * Construct a new action.
     *
     * @param type The action type
     */
    public Action(ActionType type) {
        this.type = type;
    }

    /**
     * Get the action type.
     *
     * @return The action type
     */
    public ActionType type() {
        return type;
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
        private ActionType type;

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
         * Set the action type.
         *
         * @param type The type
         * @return The builder
         */
        public Builder type(ActionType type) {
            this.type = type;
            return this;
        }

        /**
         * Build a new action.
         *
         * @return The action
         */
        public Action build() {
            if (this.blockState != null) {
                return new BlockStateAction(type, blockState);
            }

            return null;
        }
    }
}
