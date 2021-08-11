##########
参数
##########

"参数"(Parameters) 指的是在 Prism 的 查询、预览、回滚、还原 等指令中使用的"参数"(arguments).
使用它们可以准确地定义 Prism 需要操作的数据.

您可以定义每一个参数、一部分参数、或者不定义任何参数. (如果没有定义则为安全的默认值)
您可以无序地排列它们.

对于大多数的参数类型, 您可以使用逗号来定义多个参数.
例如 ``a:break,place``.

玩家可能收到的常见错误可能是:
``您遗漏了有效的行为. 使用 /prism ? 来获取帮助.``

对于遇到这个问题的玩家, 可以给予他这个权限节点: ``prism.parameters.action.required`` - 但需要将其设置为否 - 也就是 |:x:|.

参数列表
==============

- ``a:[行为]`` - 例如 "block-break" (您可以在下面看到一个完整的列表). 默认值为所有行为.
- ``r:[半径]`` - 查询您附近多少格方块半径内的事件记录, 例如 ``r:20``. 默认值为配置文件中所设值.
- ``r:global`` ``r:全局`` - 指示 Prism 不要限制记录的位置, 在全世界各个位置中查询. 使用此参数需要特别地在配置文件中配置, 或拥有针对于 查询/回滚/还原 操作使用的权限.
- ``r:we`` - 使用 WorldEdit 选区来限制记录的位置. 可以配合 查询/回滚/还原 操作使用. 对任何支持``r``参数的记录都有效. 玩家必须拥有 WorldEdit 选区权限.
- ``r:玩家名:[半径]`` 将半径中心定义到另一玩家处.
- ``r:x,y,z:[半径]`` - 将半径中心定义到坐标 x,y,z 处.
- ``r:world`` ``r:世界`` - 不限制半径, 但将世界限制为当前所处世界或者 `w:` 参数定义的世界.
- ``r:c`` ``r:区块`` - 限制半径范围内目前所处的区块 (目前区块的 x/z, 基岩层到世界高度这个长方体范围内)
- ``b:[方块名/方块ID]`` - 例如 `b:grass` 、`b:2` 和`b:2:0`. 没有默认值.
- ``e:[实体名]`` - 例如 `e:pig`. 没有默认值.
- ``t:[时长]`` - 从 x 时长后发生的事件. 例如1(seconds|秒), 20m(minutes|分), 1h(hour|时), 7d(days|天), 2w(weeks|周). 没有默认值. 您也可以配合多个单位使用, 例如 ``1时20分``.
- ``before:[时长]`` - 在 x 时长前发生的事件.
- ``since:[时长]`` - 从 x 时长后发生的事件 (等同于 t:).
- ``p:[玩家名]`` - 例如 ``p:viveleroi``. 没有默认值.
- ``w:[世界名]`` - 例如 `w:world_nether`. 默认值为所处的世界.
- ``k:[关键字]`` - 基于文本的关键字搜索 (主要适用于 指令/聊天 事件).
- ``id:[ID]`` - 单个的记录 id.

使用 ``!`` 可以排除单个行为, 玩家, 或者实体. 例如: ``/pr rollback a:break p:!Rothes``

.. _action-list:

行为列表
============

短行为是连接号之后的词. 例如短行为 ``break`` 会查询所有以 ``*-break`` 结尾的行为.

.. list-table:: 行为列表
  :widths: auto
  :header-rows: 1
  :align: center

  * - 行为
    - 可回滚
    - 可还原
    - 简述
    - 权限状态
  * - block-break
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 任何被破坏的方块.
    - |:heavy_check_mark:|
  * - block-burn
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 任何被烧掉的方块.
    - |:heavy_check_mark:|
  * - block-dispense
    - |:x:|
    - |:x:|
    - 一个方块中发射出去的物品.
    - |:heavy_check_mark:|
  * - block-fade
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - | 一个消亡的方块, 例如雪融化,
      | 树叶在断开与树的连接后消亡.
    - |:heavy_check_mark:|
  * - block-fall
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 例如沙子坠落.
    - |:heavy_check_mark:|
  * - block-form
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 形成圆石或形成冰.
    - |:heavy_check_mark:|
  * - block-place
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 任何被放置的方块.
    - |:heavy_check_mark:|
  * - block-shift
    - |:x:|
    - |:x:|
    - 被活塞推动的方块.
    - |:heavy_check_mark:|
  * - block-spread
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 方块有机地蔓延, 如草方块.
    - |:heavy_check_mark:|
  * - block-use
    - |:x:|
    - |:x:|
    - 使用方块, 如工作台.
    - |:heavy_check_mark:|
  * - bucket-fill
    - |:x:|
    - |:x:|
    - 填装一个桶.
    - |:heavy_check_mark:|
  * - bonemeal-use
    - |:x:|
    - |:x:|
    - 使用骨粉.
    - |:heavy_check_mark:|
  * - container-access
    - |:x:|
    - |:x:|
    - 使用一个容器.
    - |:heavy_check_mark:|
  * - cake-eat
    - |:x:|
    - |:x:|
    - 食用地上的蛋糕.
    - |:heavy_check_mark:|
  * - craft-item
    - |:x:|
    - |:x:|
    - 合成一个物品.
    - |:x:|
  * - creeper-explode
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 苦力怕爆炸.
    - |:heavy_check_mark:|
  * - crop-trample
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 损坏农作物.
    - |:heavy_check_mark:|
  * - dragon-eat
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 末影龙吃掉方块.
    - |:heavy_check_mark:|
  * - enchant-item
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 附魔一个物品.
    - |:x:|
  * - enderman-pickup
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 末影人拿起方块.
    - |:heavy_check_mark:|
  * - enderman-place
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 末影人放置方块.
    - |:heavy_check_mark:|
  * - entity-break
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 实体破坏一个方块.
    - |:heavy_check_mark:|
  * - entity-dye
    - |:x:|
    - |:x:|
    - 染色一个物品.
    - |:x:|
  * - entity-explode
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 实体爆炸.
    - |:heavy_check_mark:|
  * - entity-follow
    - |:x:|
    - |:x:|
    - 实体跟随一个玩家.
    - |:heavy_check_mark:|
  * - entity-form
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 形成一个实体.
    - |:heavy_check_mark:|
  * - entity-kill
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 实体被击杀.
    - |:heavy_check_mark:|
  * - entity-leash
    - |:x:|
    - |:x:|
    - 实体被栓绳拴住.
    - |:heavy_check_mark:|
  * - entity-shear
    - |:x:|
    - |:x:|
    - 实体被剪刀剪.
    - |:heavy_check_mark:|
  * - entity-spawn
    - |:x:|
    - |:x:|
    - 实体被生成.
    - |:heavy_check_mark:|
  * - entity-unleash
    - |:x:|
    - |:x:|
    - 实体被解拴.
    - |:heavy_check_mark:|
  * - fireball
    - |:x:|
    - |:x:|
    - 使用火球点火.
    - |:heavy_check_mark:|
  * - fire-spread
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 火焰蔓延.
    - |:heavy_check_mark:|
  * - firework-launch
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 发射烟花.
    - |:heavy_check_mark:|
  * - hangingitem-break
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 例如画被破坏.
    - |:heavy_check_mark:|
  * - hangingitem-place
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 例如画被放置.
    - |:heavy_check_mark:|
  * - item-drop
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 丢弃一个物品到地上.
    - |:heavy_check_mark:|
  * - item-insert
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 将物品放入容器.
    - |:heavy_check_mark:|
  * - item-pickup
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 拾起地上的掉落物.
    - |:heavy_check_mark:|
  * - item-remove
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 拿出容器内的物品.
    - |:heavy_check_mark:|
  * - item-rotate
    - |:x:|
    - |:x:|
    - 旋转物品展示框内的物品.
    - |:heavy_check_mark:|
  * - lava-break
    - |:x:|
    - |:x:|
    - 熔岩破坏一个方块.
    - |:heavy_check_mark:|
  * - lava-bucket
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 收集熔岩.
    - |:heavy_check_mark:|
  * - lava-flow
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 熔岩流动.
    - |:heavy_check_mark:|
  * - lava-ignite
    - |:x:|
    - |:x:|
    - 熔岩点燃周围的环境.
    - |:heavy_check_mark:|
  * - leaf-decay
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 树叶凋落.
    - |:heavy_check_mark:|
  * - lighter
    - |:x:|
    - |:x:|
    - 使用打火石.
    - |:heavy_check_mark:|
  * - lightning
    - |:x:|
    - |:x:|
    - 闪电劈下来.
    - |:heavy_check_mark:|
  * - mushroom-grow
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 蘑菇树生长.
    - |:heavy_check_mark:|
  * - player-chat
    - |:x:|
    - |:x:|
    - 玩家聊天.
    - |:x:|
  * - player-command
    - |:x:|
    - |:x:|
    - 玩家执行指令.
    - |:x:|
  * - player-death
    - |:x:|
    - |:x:|
    - 玩家死亡.
    - |:heavy_check_mark:|
  * - player-join
    - |:x:|
    - |:x:|
    - 玩家进入服务器.
    - |:x:|
  * - player-kill
    - |:heavy_check_mark:|
    - |:x:|
    - 击杀玩家.
    - |:x:|
  * - player-quit
    - |:x:|
    - |:x:|
    - 玩家离开服务器.
    - |:x:|
  * - player-teleport
    - |:x:|
    - |:x:|
    - 玩家传送.
    - |:x:|
  * - potion-splash
    - |:x:|
    - |:x:|
    - 玩家掷出喷溅药水.
    - |:heavy_check_mark:|
  * - sheep-eat
    - |:x:|
    - |:x:|
    - 绵羊吃草.
    - |:heavy_check_mark:|
  * - sign-change
    - |:x:|
    - |:heavy_check_mark:|
    - 修改告示牌上的文本.
    - |:heavy_check_mark:|
  * - spawnegg-use
    - |:x:|
    - |:x:|
    - 使用刷怪蛋.
    - |:heavy_check_mark:|
  * - tnt-explode
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - TNT 爆炸.
    - |:heavy_check_mark:|
  * - tnt-prime
    - |:x:|
    - |:x:|
    - 点燃 TNT.
    - |:x:|
  * - tree-grow
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 树生长.
    - |:heavy_check_mark:|
  * - vehicle-break
    - |:heavy_check_mark:|
    - |:x:|
    - 破坏载具.
    - |:heavy_check_mark:|
  * - vehicle-enter
    - |:x:|
    - |:x:|
    - 进入载具.
    - |:heavy_check_mark:|
  * - vehicle-exit
    - |:x:|
    - |:x:|
    - 离开载具.
    - |:heavy_check_mark:|
  * - vehicle-place
    - |:x:|
    - |:x:|
    - 放置载具.
    - |:heavy_check_mark:|
  * - water-break
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 水破坏方块.
    - |:heavy_check_mark:|
  * - water-bucket
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 收集水.
    - |:heavy_check_mark:|
  * - water-flow
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 水流动.
    - |:x:|
  * - world-edit
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 编辑世界.
    - |:x:|
  * - xp-pickup
    - |:x:|
    - |:x:|
    - 拾起经验球.
    - |:x:|
  * - target-hit
    - |:x:|
    - |:x:|
    - 标靶方块被箭击中.
    - |:x:|
  * - player-trade
    - |:x:|
    - |:x:|
    - 玩家与村民交易.
    - |:x:|
  * - item-receive
    - |:heavy_check_mark:|
    - |:heavy_check_mark:|
    - 与村民交易收到的物品.
    - |:x:|

理解行为间的关系
=================

Prism 会将类似的行为分成不同的子行为, 所以您可以更加高效地来查找、回滚、还原您需要的内容.

Prism 以下面两种方式来使用关系.

科属
========
行为科属定义很简单, 指两种行为十分相似, 比如 `creeper-explode` 和 `tnt-explode` 都是因爆炸破坏方块, 但是是由两种不同的原因而导致的.
如果使用短行为名 `explode`, 插件会查询匹配这个短行为名的所有行为.
如果指定了一个具体的行为名称, 插件就只会查询这一个行为.

比较一下具体的行为名称和短行为名称, 您可以了解更多. `block-break` 和 `water-break` 这两个都是具体的行为名称且属于同一科属, 可以单独地追踪, 也可以通过短行为名 `break` 来追踪双方.

因果
=========
有一些事件是相关联的, 一个事件会导致另一个事件. 要想掌握正确回滚被熊区域的技术, 您真的需要了解它们.

下面举出一个简单的例子.
一个插着火把的木栅栏被烧掉了.
方块被烧掉的记录会以行为 `block-burn` 记录, 然后火把脱离了放置着它的方块, 火把也会被移除, 以行为 `block-break` 记录.
Prism 会将事件都清晰准确地记录下来, 不会将这个火把被破坏的行为记录为 block-burn , 毕竟火把不可燃烧.

所有 附近/查询/检查 操作都会清晰地向你展示出事件行为记录.

如果要回滚整个栅栏, 应该使用 `/prism rollback a:burn,break`.
如果只要特别具体的话, 使用 `/prism rollback a:block-burn,block-break`.

Prism 会智能地先回滚木栅栏, 然后再插上火把.

只要知道了每一个行为代表什么, 您就可以理解行为间的关系了.

这里有两个参考例子:
- `/prism rollback a:water-flow,water-break` - 水流动之后破坏了一个方块.
- `/prism rollback a:block-break,block-fade` - 树被砍之后树叶消亡了.