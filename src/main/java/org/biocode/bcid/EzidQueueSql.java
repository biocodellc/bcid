package org.biocode.bcid;

import java.util.Properties;

/**
 * @author rjewing
 */
public class EzidQueueSql {
    private final Properties props;

    public EzidQueueSql(Properties props) {
        this.props = props;
    }

    public String getEzidRequests() {
        return props.getProperty("getEzidRequests");
    }

    public String insertEzidRequest() {
        return props.getProperty("insertEzidRequest");
    }

    public String incrementId() {
        return props.getProperty("incrementId");
    }

    public String deleteEzidRequests() {
        return props.getProperty("deleteEzidRequests");
    }
}
