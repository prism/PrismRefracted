挂钩到 API
===============

这个代码块可以用于获取并储存 API, 用于之后的使用. 通常您需要将此调用放于包装类中, 并在加载包装类前检查插件是否为 NULL - 这能确保您不需要 shade Prism 的 API.

.. code-block:: java

    static void hookPrismApi() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Prism");
        if (plugin != null & plugin.isEnabled()) {
            PrismApi prismApi = (PrismApi) plugin;
        }
    }

API 用法
================

.. code-block:: java

        CommandSender sender;
        PrismParameters parameters = this.createParameters();
        parameters.addActionType("block-place");
        parameters.addPlayerName("Rothes");
        final Future<Result> result = this.performLookup(parameters, sender);
        Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
            @Override
            public void run() {
                try {
                    Result done = result.get(); //在完成前会阻塞
                    for (me.botsko.prism.api.actions.Handler handler : done.getActionResults()) {
                        ///用 handler 做点啥. 记住这是异步的.
                    }
                } catch (InterruptedException | ExecutionException e) {
                        //处理异常
                }
            }
        });

导入 Prism 到项目
-------------------------------------

首先, 添加存储库:

.. tabs::

   .. group-tab:: Maven

      .. code:: xml

         <repositories>
             <!-- ... -->
                <repository>
                    <id>maven.addstar.com.au-snapshots</id>
                    <name>addstar-maven-snapshots</name>
                    <url>https://maven.addstar.com.au/artifactory/all-snapshot</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>maven.addstar.com.au</id>
                    <name>addstar-maven-releases</name>
                    <url>https://maven.addstar.com.au/artifactory/all-release</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                </repository>
             <!-- ... -->
         </repositories>

   .. group-tab:: Gradle (Groovy)

      .. code:: groovy

         repositories {
            // 开发版本
            maven {
                name = "maven-addstar-snapshot"
                url = "https://maven.addstar.com.au/artifactory/all-snapshot"
            }
            // 稳定版本
            maven {
                name = "maven-addstar-release"
                url = "https://maven.addstar.com.au/artifactory/all-releases"
            }
         }

   声明依赖:

.. tabs::

   .. group-tab:: Maven

      .. code:: xml

        <dependency>
            <groupId>me.botsko</groupId>
            <artifactId>Prism-Api</artifactId>
            <version>2.2.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>

   .. group-tab:: Gradle (Groovy)

      .. code:: groovy

         dependencies {
            provided "me.botsko:Prism-Api:2.2.0-SNAPSHOT"
         }
