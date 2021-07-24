package me.botsko.prism.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.botsko.prism.Prism;
import me.botsko.prism.database.PrismDataSource;
import me.botsko.prism.database.sql.SqlPrismDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;



/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 1/01/2021.
 */
public class PrismHikariDataSource extends SqlPrismDataSource {

    private static final File propFile = new File(Prism.getInstance().getDataFolder(),
            "hikari.properties");
    private static final HikariConfig dbConfig;

    static {
        if (propFile.exists()) {
            Prism.log("正在根据 " + propFile.getName() + " 配置 Hikari");
            dbConfig = new HikariConfig(propFile.getPath());
        } else {
            Prism.log("您可能需要为您的设定调整这些设置.");
            Prism.log("要设置表的前缀, 您需要创建一个配置, 如下:");
            Prism.log("prism:");
            Prism.log("  datasource:");
            Prism.log("    prefix: 你的前缀");
            String jdbcUrl = "jdbc:mysql://localhost:3306/prism?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
            Prism.log("默认 jdbcUrl: " + jdbcUrl);
            Prism.log("默认 Username: 用户名");
            Prism.log("默认 Password: 密码");
            Prism.log("您需要为您的数据库提供所需的jar库.");
            dbConfig = new HikariConfig();
            dbConfig.setJdbcUrl(jdbcUrl);
            dbConfig.setUsername("用户名");
            dbConfig.setPassword("密码");
            HikariHelper.createPropertiesFile(propFile, dbConfig, false);
        }
    }

    /**
     * Create a dataSource.
     *
     * @param section Config
     */
    public PrismHikariDataSource(ConfigurationSection section) {
        super(section);
        name = "hikari";
    }

    @Override
    public PrismDataSource createDataSource() {
        try {
            database = new HikariDataSource(dbConfig);
            createSettingsQuery();
            return this;
        } catch (HikariPool.PoolInitializationException e) {
            Prism.warn("Hikari Pool did not Initialize: " + e.getMessage());
            database = null;
        }
        return this;

    }

    @Override
    public void setFile() {

    }
}
