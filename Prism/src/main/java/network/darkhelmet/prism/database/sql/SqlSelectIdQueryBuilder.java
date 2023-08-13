package network.darkhelmet.prism.database.sql;

import network.darkhelmet.prism.database.PrismDataSource;
import network.darkhelmet.prism.database.SelectIdQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlSelectIdQueryBuilder extends SqlSelectQueryBuilder implements SelectIdQuery {
    /**
     * Constructor.
     * @param dataSource PrismDataSource
     */
    private String select = "";
    private boolean pair = false;

    public SqlSelectIdQueryBuilder(PrismDataSource dataSource) {
        super(dataSource);
        setMinMax();
    }

    @Override
    protected String select() {
        return select;
    }

    @Override
    protected String order() {
        return "";
    }

    @Deprecated
    public void setMax() {
        select = "SELECT max(" + tableNameData + ".id) FROM " + tableNameData + " ";
        select += "LEFT JOIN " + tableNameDataExtra + " ex ON ex.data_id = " + tableNameData + ".id ";
    }

    @Deprecated
    public void setMin() {
        select = "SELECT min(" + tableNameData + ".id) FROM " + tableNameData + " ";
        select += "LEFT JOIN " + tableNameDataExtra + " ex ON ex.data_id = " + tableNameData + ".id ";
    }

    public void setMinMax() {
        select = "SELECT min(" + tableNameData + ".id) as min, max(" + tableNameData + ".id) as max FROM " + tableNameData + " ";
        select += "LEFT JOIN " + tableNameDataExtra + " ex ON ex.data_id = " + tableNameData + ".id ";
        pair = true;
    }

    @Override
    public long[] execute() {
        long id1 = 0;
        long id2 = 0;
        try (
            Connection connection = dataSource.getDataSource().getConnection();
            PreparedStatement s = connection.prepareStatement(getQuery(parameters, shouldGroup));
            ResultSet rs = s.executeQuery()
        ) {
            if (rs.next()) {
                id1 = rs.getLong(1);
                if (pair) {
                    id2 = rs.getLong(2);
                }
            }
        } catch (final SQLException e) {
            dataSource.handleDataSourceException(e);
        }
        if (pair) {
            return new long[]{id1, id2};
        }
        return new long[]{id1};
    }

}
