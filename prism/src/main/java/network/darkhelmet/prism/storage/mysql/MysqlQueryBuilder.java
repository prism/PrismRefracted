package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activities.ActivityQuery;

import org.intellij.lang.annotations.Language;

public class MysqlQueryBuilder {
    /**
     * Query the activities table given an activity query object.
     *
     * @param query The activity query
     * @param prefix The table prefix
     * @return A list of DbRow results
     * @throws SQLException Database exception
     */
    public static List<DbRow> queryActivities(ActivityQuery query, String prefix) throws SQLException {
        @Language("SQL") String sql = "SELECT "
            + "HEX(`world_uuid`) AS worldUuid,"
            + "`x`,"
            + "`y`,"
            + "`z`,"
            + "`action`,"
            + "`timestamp`,"
            + "`material`,"
            + "`materials`.`data` AS material_data,"
            + "`custom_data`.`data` AS custom_data,"
            + "`cause` "
            + "FROM " + prefix + "activities AS activities "
            + "JOIN " + prefix + "actions AS actions ON `actions`.`action_id` = `activities`.`action_id` "
            + "JOIN " + prefix + "causes AS causes ON `causes`.`cause_id` = `activities`.`cause_id` "
            + "JOIN " + prefix + "worlds AS worlds ON `worlds`.`world_id` = `activities`.`world_id` "
            + "LEFT JOIN " + prefix + "activities_custom_data AS custom_data "
                + "ON `custom_data`.`activity_id` = `activities`.`activity_id`"
            + "LEFT JOIN " + prefix + "players AS players ON `players`.`player_id` = `causes`.`player_id` "
            + "LEFT JOIN " + prefix + "material_data AS materials "
                + "ON `materials`.`material_id` = `activities`.`material_id` ";

        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (query.minVector() != null && query.maxVector() != null) {
            conditions.add("(`x` BETWEEN ? AND ?)");
            conditions.add("(`y` BETWEEN ? AND ?)");
            conditions.add("(`z` BETWEEN ? AND ?)");

            parameters.add(query.minVector().getX());
            parameters.add(query.maxVector().getX());
            parameters.add(query.minVector().getY());
            parameters.add(query.maxVector().getY());
            parameters.add(query.minVector().getZ());
            parameters.add(query.maxVector().getZ());
        }

        if (conditions.size() > 0) {
            sql += "WHERE " + String.join(" AND ", conditions);
        }

        Prism.getInstance().debug(String.format("Querying activities: %s", sql));

        return DB.getResults(sql, parameters.toArray());
    }
}
