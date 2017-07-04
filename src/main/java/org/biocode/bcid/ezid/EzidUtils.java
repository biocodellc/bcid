package org.biocode.bcid.ezid;

import org.apache.commons.lang.StringUtils;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.EmailUtils;
import org.biocode.bcid.models.Bcid;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to help with EZID creation
 */
public class EzidUtils {
    private final BcidProperties props;

    public EzidUtils(BcidProperties props) {
        this.props = props;
    }

    private HashMap<String, String> ercMap(String target, String what, String who, String when) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("_profile", "erc");

        // _target needs to be resolved by biscicol for now
        map.put("_target", target);
        // what is always dataset
        map.put("erc.what", what);
        // who is the user who loaded this
        map.put("erc.who", who);
        // when is timestamp of data loading
        map.put("erc.when", when);
        return map;
    }

    private HashMap<String, String> dcMap(String target, String creator, String title, String publisher, String when, String type) {
        HashMap<String, String> map = new HashMap<String, String>();
        // _target needs to be resolved by biscicol for now
        map.put("_target", target);
        map.put("dc.creator", creator);
        map.put("dc.title", title);
        map.put("dc.publisher", publisher);
        map.put("dc.date", when);
        map.put("dc.type", type);
        map.put("_profile", "dc");
        return map;
    }

    public HashMap<String, String> getDcMap(Bcid bcid) {
        return dcMap(
                this.props.resolverTargetPrefix() + bcid.identifier(),
                bcid.creator(),
                bcid.title(),
                bcid.publisher(),
                String.valueOf(bcid.modified()),
                bcid.resourceType());
    }

    /**
     * send an email report for the failed Ezids
     * @param errorMap key = Bcid.identifier, value = stacktrace
     */
    public void sendErrorEmail(Map<String, String> errorMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("EZIDs were not created for the following Bcids: \n\n\n");

        for (Map.Entry<String, String> error: errorMap.entrySet()) {
            sb.append("identifier: ");
            sb.append(error.getKey());
            sb.append("\n");

            sb.append("\t");
            sb.append(error.getValue());
            sb.append("\n\n");
        }

        // Send an Email that this completed
        new EmailUtils(props).sendAdminEmail(
                "Error creating Ezid(s)",
                sb.toString()
        );
    }
}
