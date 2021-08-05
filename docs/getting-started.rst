请记住: 如果您需要帮助, 欢迎向我们寻求支持.

如何安装
===============

- 将 Prism.jar 文件放入您的 Bukkit /plugins 目录中.
- 启动您的服务器, 一个初始配置文件会生成在插件目录 /plugins/Prism 中.
- 最重要的一点, 您必须配置数据库连接.
- 如果您需要架设 MySQL 或 sqlite (无需安装的数据库) 的帮助, 请看下面.

获取 MySQL
----------
请确保您已经安装 MySQL. 大多数的人应该都已经安装了, 也许有一些人还没有接触过 MySQL.
MySQL 是一个可以安装于计算机(适用于Windows, Mac 和 Linux)的软件, 可以为 Prism 以及许多其它插件提供数据库服务.
它存储数据非常迅速和高效.
下面是安装 MySQL 的一些教程.

1. Mysql - https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/ 参阅 :ref:`mysql`

2. Mariadb - https://mariadb.com/kb/en/getting-installing-and-upgrading-mariadb/ 参阅 :ref:`mariadb`

3. Percona  - https://www.percona.com/doc/percona-server/5.7/installation.html  参阅 :ref:`mysql`

第一步
------
确保 database.mode 配置为 "mysql". 您需要主机地址, 用户名, 密码和数据库名称来连接到 MySQL.

第二步
------
输入您的数据库主机名 (MySQL 服务器的地址, 一般为 127.0.0.1 或者 localhost.
输入数据库服务器的用户名和密码, 默认为 root 和空密码.

*如果您是自己架设的 MySQL 服务器, 您应该会知道上面的这些信息如何填写. 如果您使用的是别人架设的 MySQL
(如共享主机服务), 请让他们提供给您这些信息.*

请根据您的数据库类型来参阅相关的配置部分:

1. Mysql 参阅 :ref:`mysql`
2. MariaDB 参阅 :ref:`mariadb`
3. 其它的数据库: 参阅 :ref:`hikari`

一些建议
---------------
如果您担心磁盘空间不足, 或者服务器人数极高, 我们建议关闭 water-flow 和 lava-flow 追踪. 这些事件的发生频率十分高,
而且 Bukkit 会在每一个方块坐标上多次调用这些事件. 它们会很快地灌满您的数据库. 一个相对容易的替代办法是使用 ``/prism 排水(drain)`` 指令.
但, 即使您关闭了流动事件的追踪, Prism 仍会追踪 lava/water-break 事件, 所以您仍然可以回滚被液体破坏的物品.

