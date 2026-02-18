package com.wincomplm.wex.license.report.impl.handlers.PublicGroup;

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
public class WexPublicGroupNameHandler extends WexAbstractConfigKeyHandlerGroup {

    @Override
    public String toString(String t) {
        return t;
    }

    @Override
    protected Map<String, String> getMap() {
        try {
            QuerySpec qs = getPublicGroupQuery();
            QueryResult qr = PersistenceHelper.manager.find(qs);
            List<WTGroup> publicGroups = WexQueryHelper.getListFromQueryResult(qr);

            // Sorted map for clean UI ordering
            Map<String, String> sortedMap = new TreeMap<>();
            for (WTGroup group : publicGroups) {
                String name = group.getName();
                sortedMap.put(name, name);
            }
            return sortedMap;

        } catch (Exception ex) {
            System.out.println("Exception loading groups");
            ex.printStackTrace();
        }

        return new TreeMap<>();
    }
}