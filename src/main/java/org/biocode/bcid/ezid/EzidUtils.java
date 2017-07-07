package org.biocode.bcid.ezid;

import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.EmailUtils;
import org.biocode.bcid.models.Bcid;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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

    private HashMap<String, String> dcMap(URI target, String creator, String title, String publisher, String when, String type) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (target != null) {
            map.put("_target", target.toString());
        }
        map.put("dc.creator", creator);
        map.put("dc.title", title);
        map.put("dc.publisher", publisher);
        map.put("dc.type", type);
        map.put("_profile", "dc");
        return map;
    }

    public HashMap<String, String> getDcMap(Bcid bcid) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String created = formatter.format(bcid.created());

        return dcMap(
                bcid.webAddress(),
                bcid.creator(),
                bcid.title(),
                bcid.publisher(),
                created,
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
