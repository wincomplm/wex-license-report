package com.wincomplm.wex.license.report.impl.handlers.ConfigKey;

import com.wincomplm.wex.config.impl.handlers.IValueSetHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import wt.org.WTGroup;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

/**
 *
 * @author CristianRomero
 */
public abstract class WexAbstractConfigKeyHandlerGroup implements IValueSetHandler<String> {

    @Override
    public Map<String, String> getValueSet(Locale locale) {
        return getMap();
    }

    @Override
    public abstract String toString(String t);

    protected abstract Map<String, String> getMap();

    protected Map<String, String> populateMap(List<WTGroup> listOfGroups) {
        Map<String, String> theMap = new HashMap<>();
        for (WTGroup aGroup : listOfGroups) {
            theMap.put(aGroup.getName(), aGroup.getName());
        }
        return theMap;
    }

    protected QuerySpec getPublicGroupQuery() throws QueryException {
        QuerySpec querySpec = new QuerySpec(WTGroup.class);
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.appendWhere(
                new SearchCondition(WTGroup.class, WTGroup.DISABLED, SearchCondition.IS_FALSE),
                new int[]{0}
        );
        querySpec.appendAnd();
        querySpec.appendWhere(
                new SearchCondition(WTGroup.class, WTGroup.STATUS, SearchCondition.EQUAL, "public"),
                new int[]{0}
        );
        return querySpec;
    }

    protected QuerySpec getSystemGroupQuery() throws QueryException {
        QuerySpec querySpec = new QuerySpec(WTGroup.class);
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.appendWhere(
                new SearchCondition(WTGroup.class, WTGroup.DISABLED, SearchCondition.IS_FALSE),
                new int[]{0}
        );
        querySpec.appendAnd();
        querySpec.appendWhere(
                new SearchCondition(WTGroup.class, WTGroup.STATUS, SearchCondition.EQUAL, "system"),
                new int[]{0}
        );
        return querySpec;
    }
}