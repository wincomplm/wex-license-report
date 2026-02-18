/*
 * Copyright (c) 2025 Wincom Consulting S.L.
 * All Rights Reserved.
 * This source is subject to the terms of a software license agreement.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms and conditions of the license agreement.
 */
package com.wincomplm.wex.license.report.impl.service;

import com.wincomplm.wex.config.impl.helpers.WexConfigRegistryHelper;
import com.wincomplm.wex.config.impl.registry.WexConfigRegistry;
import com.wincomplm.wex.license.report.impl.config.LicenseReportConfig;
import com.wincomplm.wex.license.report.impl.model.LicenseGroupDto;
import com.wincomplm.wex.log.api.WexLogger;
import com.wincomplm.wex.log.base.api.IWexLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.licenseusage.licensing.LicenseManagerHelper;
import wt.org.LicenseGroupHelper;
import wt.org.LicenseGroups;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

/**
 * License Report Service (config-driven)
 *
 * Config Map meaning: key = WTGroup.getName() value = total licenses (string
 * integer, validated in LicenseReportConfig)
 */
public class LicenseReportService {

    private static final IWexLogger logger = WexLogger.getLogger(LicenseReportService.class);
    private static final LicenseReportConfig config = (LicenseReportConfig) WexConfigRegistry.instance.getConfiguration("com.wincomplm.wex-license-report");
    // Exclusion groups (nested groups skipped during recursive enumeration)
    private static final Set<String> EXCLUDED_GROUP_NAMES
            = new HashSet<String>(java.util.Arrays.asList(
                    "license exclusion",
                    "license exclusion group"
            ));
    private Map<LicenseGroups, Integer> ALL_LICENSE_GROUPS_SEATS_MAP = null;

    /**
     * Fetch license group data for ONLY the groups configured in
     * LicenseReportConfig.publicGroups.
     */
    public List<LicenseGroupDto> fetchLicenseGroups() throws WTException {
        logger.trace(">>> fetchLicenseGroups() entered");
        Set<String> addedGroups = new HashSet();
        List<String> configured = config.getPublicGroups(); // group names
        if (configured == null || configured.isEmpty()) {
            logger.warn(">>> fetchLicenseGroups(): cfg.getPublicGroups() is empty or null");
            return Collections.emptyList();
        }

        logger.trace(">>> fetchLicenseGroups(): configured group count = " + configured.size());
        logger.trace(">>> fetchLicenseGroups(): configured group names = " + configured);

        List<WTGroup> groups = findGroupsByExactNames(configured);

        logger.trace(">>> fetchLicenseGroups(): WTGroups found = " + groups.size());

        List<LicenseGroupDto> result = new ArrayList<>(groups.size());
        populateResultData(groups, addedGroups, result);
        logger.info(">>> fetchLicenseGroups(): returning " + result.size() + " rows");
        result.sort(Comparator.comparing(LicenseGroupDto::getGroupName,
                String.CASE_INSENSITIVE_ORDER));

        return result;
    }

    private void populateResultData(List<WTGroup> groups, Set<String> addedGroups, List<LicenseGroupDto> result) throws WTException {
        for (WTGroup group : groups) {
            
            String groupName = group.getName();
            Integer totalCount = countGroupMembers(group);
            LicenseGroupDto data;
            if (LicenseGroupHelper.isLicenseGroup(group)) {
                int totalLicenseCount = getLicenseTotal(group);

                logger.info(
                        ">>> populateResultData(): processing group '"
                                + groupName
                                + "' with configured total '"
                                + totalLicenseCount + "'"
                );
                data = buildLicenseGroupData(group, totalCount);
                data.setTotalLicenseCount(String.valueOf(totalLicenseCount));

            } else {
                   logger.info(
                        ">>> populateResultData(): processing group '"
                                + groupName
                                + "' with  '"
                                + totalCount + " members'"
                );
                data = new LicenseGroupDto();
                data.setId(group.getPersistInfo().getObjectIdentifier().getStringValue());
                data.setGroupName(groupName);
                data.setMembers(String.valueOf(totalCount));
                data.setRemainingLicenseCount("Not a License group");
                data.setTotalLicenseCount("Not a License group");
                data.setContext(getGroupContext(group));

            }
            if (!addedGroups.contains(data.getGroupName())) {
                result.add(data);
                addedGroups.add(data.getGroupName());
            }
        }
    }

    /**
     * Safely retrieve LicenseReportConfig from WexConfigRegistryHelper without
     * ClassCastException.
     */
    private LicenseReportConfig getLicenseReportConfigSafe() {
        Object cfg = WexConfigRegistryHelper.instance.getConfig();
        if (cfg instanceof LicenseReportConfig) {
            return (LicenseReportConfig) cfg;
        }
        return null;
    }

    private Integer safeParseInt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            // Config.validate() should prevent this, but fail safe.
            return null;
        }
    }

    /**
     * Query WTGroups by exact group name using OR conditions.
     */
    private List<WTGroup> findGroupsByExactNames(Collection<String> groupNamesInConfiguration) throws WTException {
        List<WTGroup> groups = new ArrayList<WTGroup>();
        if (groupNamesInConfiguration == null || groupNamesInConfiguration.isEmpty()) {
            return groups;
        }

        QuerySpec qs = new QuerySpec(WTGroup.class);
        int added = 0;

        for (String groupName : groupNamesInConfiguration) {
            if (groupName == null || groupName.trim().isEmpty()) {
                continue;
            }
            if (added > 0) {
                qs.appendOr();
            }
            qs.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, groupName.trim()), new int[]{0});
            added++;
        }

        if (added == 0) {
            return groups;
        }

        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            Object obj = qr.nextElement();
            if (obj instanceof WTGroup) {
                groups.add((WTGroup) obj);
            }
        }
        logger.info("Found {0} configured license groups", groups.size());
        return groups;
    }

    private LicenseGroupDto buildLicenseGroupData(WTGroup group, Integer totalCount) throws WTException {

        // Ensure we pass a full OR: OID so Info Page treats it as persistent
        String rawOid = group.getPersistInfo().getObjectIdentifier().getStringValue();
        String id = rawOid.startsWith("OR:") ? rawOid : "OR:" + rawOid;

        String fullName = group.getName();
        String displayName = parseDisplayName(fullName);

        int memberCount = countGroupMembers(group);

        Integer remaining = null;
        if (totalCount != null) {
            remaining = totalCount - memberCount;
        }

        String context = getGroupContext(group);
        String members = String.valueOf(memberCount);
        String remainingLicenses = String.valueOf(remaining);
        String total = String.valueOf(totalCount);

        return new LicenseGroupDto(id, displayName, total, members, remainingLicenses, context);
    }

    private int countGroupMembers(WTGroup group) throws WTException {
        return countGroupMembersInternal(group, new HashSet<String>());
    }

    private int countGroupMembersInternal(WTGroup group, Set<String> visitedGroupIds) throws WTException {

        // Avoid infinite recursion if groups reference each other
        String groupId = group.getPersistInfo().getObjectIdentifier().getStringValue();
        if (!visitedGroupIds.add(groupId)) {
            return 0;
        }

        int count = 0;

        // Get direct members (WTUser and WTGroup)
        Enumeration<?> members = OrganizationServicesHelper.manager.members(group, false);
        while (members.hasMoreElements()) {
            Object member = members.nextElement();

            if (member instanceof WTUser) {
                count++;
            } else if (member instanceof WTGroup) {
                WTGroup subgroup = (WTGroup) member;

                String subgroupName = subgroup.getName();
                if (isExcludedGroupName(subgroupName)) {
                    continue;
                }

                count += countGroupMembersInternal(subgroup, visitedGroupIds);
            }
        }

        return count;
    }

    private boolean isExcludedGroupName(String groupName) {
        if (groupName == null) {
            return false;
        }
        String lowerName = groupName.toLowerCase(Locale.ROOT);
        return EXCLUDED_GROUP_NAMES.contains(lowerName);
    }

    /**
     * Keep existing display name behavior: if name has newline, use first line.
     */
    private String parseDisplayName(String fullName) {
        if (fullName == null) {
            return "";
        }
        if (fullName.contains("\n")) {
            String[] parts = fullName.split("\\n");
            return parts[0].trim();
        }
        return fullName;
    }

    private String getGroupContext(WTGroup group) {
        try {
            if (group.getContainerReference() != null) {
                return group.getContainerReference().getName();
            }
        } catch (Exception e) {
            logger.error("Failed to get context for group: {0}\n{1}", group.getName(), e);
        }
        return "";
    }

    private int getLicenseTotal(WTGroup group) {
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(group);
            List<LicenseGroups> namedLicenseGroups = LicenseManagerHelper.manager.getNamedLicenseGroups(arrayList);
            for (LicenseGroups namedLicenseGroup : namedLicenseGroups) {
                String licenseFeatureName = namedLicenseGroup.name;
                if (licenseFeatureName.equalsIgnoreCase(group.getName())) {
                    long licenseFeatureTotalCount = LicenseManagerHelper.manager.getLicenseFeatureTotalCount(namedLicenseGroup.getLicenseFeatureName());
                    return (int) licenseFeatureTotalCount;
                }
            }
        } catch (WTException ex) {
            logger.error("Failed to get group license count: {0}\n{1}", group.getName(), ex);
        }
        return 0;
    }
}
