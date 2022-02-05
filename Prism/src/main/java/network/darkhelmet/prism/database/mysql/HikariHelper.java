package network.darkhelmet.prism.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.util.PropertyElf;
import network.darkhelmet.prism.Prism;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

class HikariHelper {

    public static void createPropertiesFile(File propFile, HikariConfig dbConfig, boolean skipCoreValue) {
        dbConfig.setPoolName("prism");
        Properties prop = new Properties();
        Set<String> keys = PropertyElf.getPropertyNames(HikariConfig.class);
        for (String k : keys) {
            if (skipCoreValue) {
                if ("jbdcUrl".equals(k) || "username".equals(k) || "password".equals(k)) {
                    continue;
                }
            }
            if ("dataSourceProperties".equals(k) || "healthCheckProperties".equals(k)) {
                continue;
            }
            Object out = PropertyElf.getProperty(k, dbConfig);
            if (out != null) {
                prop.setProperty(k, out.toString());
            }
        }
        Properties datasourceProps = dbConfig.getDataSourceProperties();
        for (String name : datasourceProps.stringPropertyNames()) {
            String val = datasourceProps.getProperty(name);
            if (val != null) {
                prop.setProperty("dataSource." + name, val);
            }
        }
        try {
            if (!propFile.getParentFile().exists() && !propFile.getParentFile().mkdirs()) {
                Prism.log("无法创建 Prism 文件夹目录");
            }
            OutputStream out = new FileOutputStream(propFile);
            prop.store(out, "Prism Hikari 连接数据源配置文件. "
                    + "用于高级数据库配置");
            Prism.log("数据库配置文件已保存至 - " + propFile.getPath());
        } catch (IOException e) {
            Prism.log("无法保存 Hikari.properties - " + e.getMessage());
        }
    }
}
