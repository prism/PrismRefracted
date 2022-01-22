package network.darkhelmet.prism.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

import network.darkhelmet.prism.config.StorageConfiguration;

import network.darkhelmet.prism.utils.TypeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;

public class MysqlSchemaUpdater {
    public static void update_8_to_v4(
        StorageConfiguration storageConfig, HikariDataSource database) throws SQLException {
        try (
            Connection conn = database.getConnection();
            Statement stmt = conn.createStatement()
        ) {
            @Language("SQL") String updateWorlds = "ALTER TABLE `" + storageConfig.prefix() + "worlds`"
                + "ADD COLUMN `world_uuid` BINARY(16) NULL AFTER `world`,"
                + "ADD UNIQUE INDEX `world_uuid_UNIQUE` (`world_uuid` ASC);";
            stmt.executeUpdate(updateWorlds);

            for (World world : Bukkit.getServer().getWorlds()) {
                @Language("SQL") String updateWorld = "UPDATE `" + storageConfig.prefix() + "worlds`"
                    + "SET world_uuid = UNHEX(?) WHERE world = ?";

               try (PreparedStatement pstmt = conn.prepareStatement(updateWorld)) {
                   pstmt.setString(1, TypeUtils.uuidToDbString(world.getUID()));
                   pstmt.setString(2, world.getName());
                   pstmt.executeUpdate();
               }
            }
        }
    }
}
