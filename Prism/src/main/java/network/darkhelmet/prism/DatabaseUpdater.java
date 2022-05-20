package network.darkhelmet.prism;

import network.darkhelmet.prism.database.PrismDataSourceUpdater;
import network.darkhelmet.prism.database.PrismDatabaseFactory;
import network.darkhelmet.prism.settings.Settings;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class DatabaseUpdater {


    protected final Prism plugin;
    private final int currentDbSchemaVersion = 8;
    private final int currentDbSchemaVersionCN = 2;
    private final ArrayList<Runnable> updates = new ArrayList<>(currentDbSchemaVersion);
    private final ArrayList<Runnable> updatesCN = new ArrayList<>(currentDbSchemaVersion);
    private final Callable<Boolean> checkColumnCN;
    private final Runnable restoreCNChanges;

    /**
     * The plugin.
     *
     * @param plugin Prism.
     */
    DatabaseUpdater(Prism plugin) {
        this.plugin = plugin;
        PrismDataSourceUpdater prismDataSourceUpdater = PrismDatabaseFactory.createUpdater(Prism.config);
        updates.add(prismDataSourceUpdater::v1_to_v2);
        updates.add(prismDataSourceUpdater::v2_to_v3);
        updates.add(prismDataSourceUpdater::v3_to_v4);
        updates.add(prismDataSourceUpdater::v4_to_v5);
        updates.add(prismDataSourceUpdater::v5_to_v6);
        updates.add(prismDataSourceUpdater::v6_to_v7);
        updates.add(prismDataSourceUpdater::v7_to_v8);
        updates.add(prismDataSourceUpdater::v8_to_v9);

        updatesCN.add(prismDataSourceUpdater::v1_to_v2_cn);
        checkColumnCN = prismDataSourceUpdater::hasCNColumn;
        restoreCNChanges = prismDataSourceUpdater::restoreCNChanges;
    }

    private int getClientDbSchemaVersion() {
        final String schema_ver = Settings.getSetting("schema_ver");
        if (schema_ver != null) {
            return Integer.parseInt(schema_ver);
        }
        return currentDbSchemaVersion;
    }

    private int getClientDbSchemaVersionCN() {
        final String schema_ver = Settings.getSetting("schema_cn_ver");
        if (schema_ver != null) {
            return Integer.parseInt(schema_ver);
        }
        try {
            if (checkColumnCN.call()) {
                // Restore the change CN Edition made before for later official changes.
                Settings.saveSetting("schema_ver", "" + 8);
                Prism.log("已检测到中文版架构为 v2, 跳过更新.");
                return 2;
            }
            Prism.log("检测到您未使用过中文版, 将更新至中文版架构.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Run any queries lower than current currentDbSchemaVersion.
     */
    void applyUpdates() {
        applyUpdatesCN();

        int clientSchemaVer = getClientDbSchemaVersion();

        for (int i = clientSchemaVer; i < currentDbSchemaVersion; ++i) {
            Runnable update = updates.get(i - 1);

            if (update != null) {
                Prism.log("正在更新 Prism 架构 v" + i + " 至 v" + (i + 1) + ". 这可能需要一段时间.");
                update.run();
            }
        }

        // Save current version
        Settings.saveSetting("schema_ver", "" + currentDbSchemaVersion);
        Prism.log("已完成更新检查: 架构 v" + currentDbSchemaVersion);
    }

    /**
     * Run any queries lower than current currentDbSchemaVersion in Chinese Edition.
     */
    void applyUpdatesCN() {
        int clientSchemaVer = getClientDbSchemaVersionCN();

        for (int i = clientSchemaVer; i < currentDbSchemaVersionCN; ++i) {
            Runnable update = updatesCN.get(i - 1);

            if (update != null) {
                Prism.log("正在更新 Prism 中文版架构 v" + i + " 至 v" + (i + 1) + ". 这可能需要一段时间.");
                update.run();
            }
        }

        // Save current version
        Settings.saveSetting("schema_cn_ver", "" + currentDbSchemaVersionCN);
        Prism.log("已完成更新检查: 中文版架构 v" + currentDbSchemaVersionCN);
    }

    void restoreCNChanges() {
        restoreCNChanges.run();
        Settings.saveSetting("schema_cn_ver", "1");
    }
}
