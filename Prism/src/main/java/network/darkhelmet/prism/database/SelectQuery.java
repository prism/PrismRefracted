package network.darkhelmet.prism.database;

import network.darkhelmet.prism.actionlibs.QueryResult;
import network.darkhelmet.prism.api.PrismParameters;
import network.darkhelmet.prism.measurement.TimeTaken;

public interface SelectQuery {
    String getQuery(PrismParameters parameters, boolean shouldGroup);

    void setParameters(PrismParameters parameters);

    void setShouldGroup(boolean shouldGroup);

    QueryResult executeSelect(TimeTaken eventTimer);
    /*    These methods should exist in a selectQuery and it should extend QueryBuilder
    String select();
    String where() ;
    void worldCondition() ;
    void actionCondition();
    void playerCondition();
    void radiusCondition();
    void blockCondition() ;
    void entityCondition() ;
    void timeCondition() ;
    void keywordCondition() ;
    void coordinateCondition();
    String buildWhereConditions();
    String group();
    String order() ;
    String limit();
    String buildMultipleConditions(HashMap<String, MatchRule> origValues, String field_name, String format);
    String buildGroupConditions(String fieldname, String[] arg_values, String matchFormat, String matchType,
                                                   String dataFormat) ;
    void buildRadiusCondition(Vector minLoc, Vector maxLoc) ;
    String buildTimeCondition(Long dateFrom, String equation) ;*/
}

