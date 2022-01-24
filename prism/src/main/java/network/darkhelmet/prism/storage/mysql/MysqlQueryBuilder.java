package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.prism.api.activities.ActivityQuery;

import org.intellij.lang.annotations.Language;

public class MysqlQueryBuilder {
    /**
     * Query the activies table given an activity query object.
     *
     * @param query The activity query
     * @param prefix The table prefix
     * @return A list of DbRow results
     * @throws SQLException Database exception
     */
    public static List<DbRow> queryActivities(ActivityQuery query, String prefix) throws SQLException {
        @Language("SQL") String sql = "SELECT * FROM " + prefix + "activities ";

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

        return DB.getResults(sql, parameters.toArray());
    }
}
