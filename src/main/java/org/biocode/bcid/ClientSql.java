package org.biocode.bcid;

import java.util.Properties;

/**
 * @author rjewing
 */
public class ClientSql {
    private final Properties props;

    public ClientSql(Properties props) {
        this.props = props;
    }

    public String insert() {
        return props.getProperty("insert");
    }

    public String getClientById() {
        return props.getProperty("clientById");
    }

    public String getClientByAccessToekn() {
        return props.getProperty("clientByAccessToken");
    }

    public String clientIdentifierIsAssociated() {
        return props.getProperty("clientIdentifierIsAssociated");
    }

    public String clientIdentifierAssociate() {
        return props.getProperty("clientIdentifierAssociate");
    }
}
