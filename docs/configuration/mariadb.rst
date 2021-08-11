.. _mariadb:

MariaDB
=======

MariaDB 并不完全兼容 MYSQL, 它不支持 MySQL 服务器的所有可用的功能. 因此我们只能使用函数的一个子集.
Prism 可以支持此数据库, 设置 NonStandardSQL 参数为 false 即可. 但我们仍然会使用 MySQL 驱动, 因为它打包在大多数服务端之中. 或者您可以使用进阶的 Hikari 来配置. (参阅 :ref:`hikari` 配置)

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
      useNonStandardSql: false

