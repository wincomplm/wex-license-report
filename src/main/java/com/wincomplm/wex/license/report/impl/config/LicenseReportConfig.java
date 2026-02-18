package com.wincomplm.wex.license.report.impl.config;

/**
 *
 * @author jbaileyjr
 */
import com.wincomplm.wex.config.impl.annotations.ConfigOption;
import com.wincomplm.wex.config.impl.exceptions.WexValidationException;
import com.wincomplm.wex.config.impl.ifc.IWexConfiguration;
import com.wincomplm.wex.kernel.impl.annotations.WexComponent;
import com.wincomplm.wex.security.commons.impl.WexSanitizer;
import com.wincomplm.wex.license.report.impl.handlers.PublicGroup.WexPublicGroupNameHandler;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WexComponent(uid = "License-Report-Config", description = "License Report configuration")
public class LicenseReportConfig implements IWexConfiguration<LicenseReportConfig>, Externalizable, Serializable {

    public static final long serialVersionUID = 2L;

    @ConfigOption(
            category = "ePLM License Group Mapping",
            description = "Configure the groups to report licensing data",
            required = false,
            handler = WexPublicGroupNameHandler.class
    )
    private List<String> publicGroups = new ArrayList();
    /**
     * @ConfigOption( category = "System groups", description = "Set Mapping for
     * max group capacity on System groups", required = false, hidden = true,
     * keyhandler = WexSystemGroupNameHandler.class
     *
     * )
     */
    private Map<String, String> systemGroups = new HashMap<>();

    public List<String> getPublicGroups() {
        return publicGroups;
    }

    public void setPublicGroups(List<String> publicGroups) {
        this.publicGroups = publicGroups;
    }

    public Map<String, String> getSystemGroups() {
        return WexSanitizer.sanitize(systemGroups);
    }

    public void setSystemGroups(Map<String, String> systemGroups) {
        this.systemGroups = systemGroups;
    }

    @Override
    public void assign(LicenseReportConfig configuration) throws WexValidationException {
        // Optional: handle special assignment logic.
    }

    @Override
    public void validate() throws WexValidationException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(WexSanitizer.sanitize(publicGroups));
        out.writeObject(WexSanitizer.sanitize(systemGroups));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            this.publicGroups = (List<String>) in.readObject();
            this.systemGroups = (Map<String, String>) in.readObject();
        } catch (OptionalDataException ode) {
            System.out.println("Failed to process LicenseReportConfig [OK if post install]");
        }
    }
}
