package com.wincomplm.wex.license.report.impl.handlers.SystemGroup;

import com.wincomplm.wex.license.report.impl.handlers.ConfigKey.WexAbstractConfigKeyHandlerGroup;
import com.wincomplm.wex.wt.framework.commons.persist.WexQueryHelper;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.query.QuerySpec;

/**
 *
 * @author CristianRomero
 */
public class WexSystemGroupNameHandler extends WexAbstractConfigKeyHandlerGroup {

    @Override
    public String toString(String t) {
        return t;
    }

    @Override
    protected Map<String, String> getMap() {
        try {
            QuerySpec qs = getSystemGroupQuery();
            QueryResult qr = PersistenceHelper.manager.find(qs);
            List<WTGroup> systemGroups = WexQueryHelper.getListFromQueryResult(qr);

            // Sorted map for consistent UI ordering
            return new TreeMap<>(populateMap(systemGroups));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new TreeMap<>();
    }
}
