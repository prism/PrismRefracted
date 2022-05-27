package network.darkhelmet.prism.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.database.SelectQuery;
import network.darkhelmet.prism.database.sql.SqlPrismDataSource;
import network.darkhelmet.prism.database.sql.SqlSelectQueryBuilder;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.HashMap;

public class MySqlPrismDataSource extends SqlPrismDataSource {

    private static final File propFile = new File(Prism.getInstance().getDataFolder(),
            "hikari.properties");
    private static final HikariConfig dbConfig;
    private static final HashMap<String, String> dbInfo = new HashMap<>();

    static {
        if (propFile.exists()) {
            Prism.log("正在根据 " + propFile.getName() + " 配置 Hikari");
            Prism.debug("此文件不会存储 jdbcURL, username 或 password - 默认下它们会从普通的 Prism "
                    + "配置文件中加载. 但如果您明确地在 properties 配置文件 中设定了这些设置, "
                    + "普通的配置文件中的设置会被忽略.");
            dbConfig = new HikariConfig(propFile.getPath());
        } else {
            dbConfig = new HikariConfig();
        }
    }

    private final Boolean nonStandardSql;

    /**
     * Create a dataSource.
     *
     * @param section Config
     */
    public MySqlPrismDataSource(ConfigurationSection section) {
        super(section);
        nonStandardSql = this.section.getBoolean("useNonStandardSql", false);
        detectNonStandardSql();
        name = "mysql";
    }

    /**
     * The adds the new requirements to an old configuration file.
     *
     * @param section a {@link ConfigurationSection}
     */
    public static void updateDefaultConfig(ConfigurationSection section) {
        section.addDefault("hostname", "127.0.0.1");
        section.addDefault("username", "prism");
        section.addDefault("password", "prism");
        section.addDefault("databaseName", "prism");
        section.addDefault("prefix", "prism_");
        section.addDefault("port", "3306");
        section.addDefault("useNonStandardSql", true);
        setupDefaultProperties(section);
    }

    private static void setupDefaultProperties(@Nonnull ConfigurationSection section) {
        int maxPool = section.getInt("database.max-pool-connections", 10);
        int minIdle = section.getInt("database.min-idle-connections", 2);
        if (maxPool > 0 && minIdle > 0 && !propFile.exists()) {
            dbConfig.addDataSourceProperty("maximumPoolSize", maxPool);
            dbConfig.addDataSourceProperty("minimumIdle", minIdle);
            dbConfig.setMaximumPoolSize(maxPool);
            dbConfig.setMinimumIdle(minIdle);
        }
        if (!propFile.exists()) {
            HikariHelper.createPropertiesFile(propFile,dbConfig,true);
        }
    }

    @Override
    public MySqlPrismDataSource createDataSource() {
        if (dbConfig.getJdbcUrl() == null) {
            final String dns = "jdbc:mysql://" + this.section.getString("hostname") + ":"
                    + this.section.getString("port") + "/" + this.section.getString("databaseName")
//                    + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
            dbConfig.setJdbcUrl(dns);
            dbConfig.setUsername(this.section.getString("username"));
            dbConfig.setPassword(this.section.getString("password"));
        }
        if (dbConfig.getDriverClassName() == null) {
            try {
                dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            } catch (RuntimeException e) {
                // Some servers still use Connector/J 5.1 API
                dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
            }
        }
        dbConfig.addHealthCheckProperty("connectivityCheckTimeoutMs", "1000");
        dbConfig.addHealthCheckProperty("expected99thPercentileMs", "10");
        if (Prism.getInstance().monitoring) {
            Prism.log("Hikari 已配置 Metric 监控.");
        } else {
            Prism.log("未发现挂钩到 Hikari 的 metric 记录器.");
        }
        try {
            database = new HikariDataSource(dbConfig);
            createSettingsQuery();
            return this;
        } catch (HikariPool.PoolInitializationException e) {
            Prism.warn("Hikari 数据池没有初始化: " + e.getMessage());
            database = null;
        }
        return this;
    }

    @Override
    public void setFile() {
        //not required here.
    }

    @Override
    public SelectQuery createSelectQuery() {
        if (nonStandardSql) {
            return new MySqlSelectQueryBuilder(this);
        } else {
            return new SqlSelectQueryBuilder(this);
        }
    }

    private void detectNonStandardSql() {
        try (
            Connection conn = getConnection();
            PreparedStatement st = (conn != null) ? conn.prepareStatement("SHOW VARIABLES") : null;
            ResultSet rs = (st != null) ? st.executeQuery() : null;
        ) {
            if (rs == null ) {
                throw new SQLNonTransientConnectionException("数据库没有正确地初始化.");
            }
            while (rs.next()) {
                dbInfo.put(rs.getString(1).toLowerCase(), rs.getString(2));
            }

            String version = dbInfo.get("version");
            String versionComment = dbInfo.get("version_comment");
            Prism.log("Prism 已检测到您的数据库版本为:" + version + " / " + versionComment);
            Prism.log("您已设定 nonStandardSql 为 " + nonStandardSql);
            Prism.log("您可以使用 不规范的 SQL");
            if (!nonStandardSql) {
                Prism.log("Prism 将会使用标准的 sql 查询");
            }
        } catch (SQLNonTransientConnectionException e) {
            Prism.warn(e.getMessage());
        } catch (SQLException e) {
            Prism.log("您不可以使用 非标准的 SQL");
            if (nonStandardSql) {
                Prism.log("看起来这是一个配置错误.  如果您遇到了数据库访问错误, "
                        + "请设置 nonStandardSql 为 false");
            }
        }
    }
}
