.. _mysql:

Mysql
=====

在 Ubuntu 上安装 - 实际上只需要运行 `sudo apt-get install mysql-server` 并按照提示来完成安装, 但是如果需要, 您也可以在这里找到全面的指南: `安装 MySQL <https://wangxin1248.github.io/linux/2018/07/ubuntu18.04-install-mysqlserver.html>`_  您可能需要百度来寻找您的特定服务器类型.

在 Windows 上安装 (\ **原作者:**\ 我重申, Windows 并不是适合运行 Minecraft 服务器的操作系统) , 但如果您需要的话, 可参考  `在 Windows 上安装 MySQL <https://www.cnblogs.com/kendoziyu/p/MySQL.html>`_

\ *上面中文的链接都是我瞎找的*\

Once installed configurating Prism is pretty straight forward.  Open a MYSQL command line and type

.. code:: sql

  CREATE SCHEMA `prism`;
  CREATE USER `prism`@`localhost` identified by `prism`;
  GRANT ALL PRIVILEGES ON prism.* TO 'prism'@'localhost';

记住如果您的 MySQL 和 Minecraft 服务器并不运行在同一个机器, 您就需要调整 `localhost` 字符串为 MySQL 服务器主机名. 如果您不确定, 可以将其调为 `%`, 但是这并不安全.


MySQL 配置一般应该调整成这样:-

.. code:: yaml

  datasource:
    type: mysql
    properties:
      hostname: 127.0.0.1
      username: prism
      password: prism
      databaseName: prism
      prefix: prism_
      port: '3306'
      useNonStandardSql: true

您需要调整配置项 "hostname"(主机名), "username"(用户名) 和 "password"(密码), 让它们符合您的配置. 如果您使用的是 MySQL, 设置 "useNonStandardSql" 为 true. 如果是 MariaDb (参阅 :ref:`mariadb`\ ) 请设置为 false.
Percona 是 MYSQL 的一个分叉, 旨在完全兼容 MySQL 的前提下提供更高的性能.
