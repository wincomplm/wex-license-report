/*
 * Copyright (c) 2021 Wincom Consulting S.L.
 * All Rights Reserved.
 * This source is subject to the terms of a software license agreement.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms and conditions of the license agreement.
 */
package com.wincomplm.wex.license.report.impl.resources;

import com.wincomplm.wex.kernel.impl.annotations.WexComponent;
import wt.util.resource.RBEntry;
import wt.util.resource.RBNameException;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@WexComponent(uid = "com.wincomplm.wex.license.report.impl.resources.WexResources", description = "Resource bundle")
@RBUUID("com.wincomplm.wex.license.report.impl.resources.WexResources")
@RBNameException
public class WexResources extends WTListResourceBundle {

    @RBEntry("License Report")
    public static final String PRIVATE_CONSTANT_0 = "com.wincomplm.wex.license.report.licenseReport.description";
    @RBEntry("License Report")
    public static final String PRIVATE_CONSTANT_1 = "com.wincomplm.wex.license.report.licenseReport.tooltip";
    @RBEntry("License Report")
    public static final String PRIVATE_CONSTANT_2 = "com.wincomplm.wex.license.report.licenseReport.activetooltip";
}
