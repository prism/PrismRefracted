.. _hikari:


==========================
自定义数据库连接
==========================
有时, 您可能想要一个完全可自定义的数据库连接. 只要您选择的数据库连接器支持大部分现代 MySQL 风格的 SQL 指令, 它就应该可以工作. 您可以使用 "Hikari" 来配置它.
您可以调整您的配置文件成如下所示

.. code-block:: yaml

  datasource:
    type: hikari
    properties:
      prefix: prism_
      useNonStandardSql: true

您需要适当地调整 Hikari.properties 文件, 并提供 JDBC 连接器环境变量. 请参阅 `HikariCP 项目 <https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby>`_ 来获取例子. 数据表前缀和是否使用非标准的SQL仍然在这里设置.

.. code-block:: properties

    #Prism Hikari 连接数据源配置文件.
    #Sat Jan 02 21:47:38 AEST 2021
    initializationFailTimeout=10
    validationTimeout=5000
    readOnly=false
    registerMbeans=false
    isolateInternalQueries=false
    maxLifetime=1800000
    leakDetectionThreshold=0
    minimumIdle=1
    allowPoolSuspension=false
    idleTimeout=600000
    jdbcUrl=jdbc\:mysql\://localhost\:3306/prism?useUnicode\=true&characterEncoding\=UTF-8&useSSL\=false
    maximumPoolSize=4
    autoCommit=true
    connectionTimeout=30000
    poolName=prism
    username=prism
    password=prism

这是一个 hikari 配置文件的示例 ....它仿造了 MySQL 连接.