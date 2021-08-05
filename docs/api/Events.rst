##############
挂钩到事件
##############

Prism 有 5 个事件可以被挂钩, 来监视 Prism 和其 API.

PrismLoadedEvent
================

.. code:: java

  public class PrismLoadedEvent extends Event {

    public PrismApi getApi() {
        return api;
    }
  }

如果您不想让您的插件依赖于 Prism, 在它之后加载, 可以挂钩到这个事件, 也可以让您获得完整加载的 API.

PrismUnloadEvent
================

这个事件没有任何方法. 挂钩到这个事件可以让您知道何时 Prism API 不再可用.


PrismRollbackEvent
==================

.. code:: java

    PrismRollBackEvent{
        /**
         * List.
         *
         * @return List BlockStateChange's
         */
        public List<BlockStateChange> getBlockStateChanges() {
            return blockStateChanges;
        }

        public ApplierResult getResult() {
            return result;
        }

        public Player getOnBehalfOf() {
            return onBehalfOf;
        }

        public PrismParameters getParameters() {
            return parameters;
        }
    }

返回回滚事件的参数以及调用事件的玩家和发生的 BlockStateChanges.

PrismExtinguishEvent
====================

.. code:: java

    public class PrismExtinguishEvent extends Event {
        public ArrayList<BlockStateChange> getBlockStateChanges() {
            return blockStateChanges;
        }

        public Player onBehalfOf() {
            return onBehalfOf;
        }

        public int getRadius() {
            return radius;
        }
    }

方法见上.


PrismDrainEvent
===============

.. code:: java

    public class PrismBlocksDrainEvent extends Event {

        public ArrayList<BlockStateChange> getBlockStateChanges() {
            return blockStateChanges;
        }

        public Player onBehalfOf() {
            return onBehalfOf;
        }

        public int getRadius() {
            return radius;
        }
    }

方法见上.

