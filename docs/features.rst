行为追踪
===============

下面的列表中的行为可以通过配置文件设定追踪或忽略. 要确保一个行为是会被追踪的, 请在您的配置文件中添加特定选项. 任何不存在于列表之中的行为都会被设为 false.

例如

.. code-block:: yaml

  prism:
    tracking:
      item-remove: true

可追踪行为列表
-----------------------

参阅 :ref:`action-list`

报告
-------

Prism 可以针对单个玩家的方块和行为数据提供一组有限的报告 offers a limited set of reports on block and action data per player.

`/prism (rp|report) sum (blocks|actions) (player)`

这将列出玩家A summary report for blocks will list the total number of block break/place actions for the player, broken down by block.

The actions summary will display a count for each action type for the specific player.

挖矿警告
----------

This system alerts you when a player discovers a natural (non-placed) vein of ores.
It reports the ore type and the lighting levLw:els (not always indicative of xray due to brightness differences).
The ore type messages are colored for easy recognition.

We've found this extremely effective at spotting xray. Players who show very clear patterns are very easy to identify.

物品使用警告
---------------

Item use alerts tell when you a player is using something that's possibly related to griefing.
Lighting fires, placing tnt, placing pistons, etc.

If you wish, you may also define a list blocks that will alert you when broken.

原版活塞卡透视警告
-------------------------

There's a known exploit involving a piston that lets you see through blocks.
Prism will attempt to alert you when it seems like a player is trying to use this trick.

灭火与排水
----------

Use `/prism ex [radius]` to extinguish all fires in the radius, or `/prism drain [radius]` to remove all liquid (water and lava) in the radius.

You may also drain specific subtypes of liquid, so `/prism drain water` or `lava`, and if providing a radius, `/prism drain lava 10`.

When performing a rollback of block-burn actions, Prism will automatically extinguish the fires as well.


区块边界视图
-------------------
Use `/pr view chunk` to view a glowstone preview of the chunk edges.
Repeat the command to disable.

删除
------

Server operators can use `/prism delete [timeframe]` to manually purge records from the database.
This isn't usually necessary, as Prism will automatically purge records (per config) every 12 hours.
See :ref:`purging` .