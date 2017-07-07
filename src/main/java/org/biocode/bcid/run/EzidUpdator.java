//package org.biocode.bcid.run;
//
//import org.biocode.bcid.BcidAppConfig;
//import org.biocode.bcid.BcidProperties;
//import org.biocode.bcid.ezid.EzidException;
//import org.biocode.bcid.ezid.EzidService;
//import org.biocode.bcid.ezid.EzidUtils;
//import org.biocode.bcid.models.Bcid;
//import org.biocode.bcid.service.BcidService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
//import java.util.HashMap;
//import java.util.Set;
//
///**
// * Created by rjewing on 9/6/16.
// */
//public class EzidUpdator {
//    private static final Logger logger = LoggerFactory.getLogger(EzidUpdator.class);
//    private BcidService bcidService;
//    private BcidProperties props;
//
//    public EzidUpdator(BcidService bcidService, BcidProperties props) {
//        this.bcidService = bcidService;
//        this.props = props;
//    }
//
//    /**
//     * Update EZID Bcid metadata for this particular ID
//     */
//    private void updateBcidsEZID(EzidService ezidService, Bcid bcid) throws EzidException {
//        EzidUtils ezidUtils = new EzidUtils(props);
//        // Build the hashmap to pass to ezidService
//        // Get creator, using any system defined creator to override the default which is based on user data
//        HashMap<String, String> map = ezidUtils.getDcMap(bcid);
//
//        try {
//            ezidService.setMetadata(String.valueOf(bcid.identifier()), map);
//            logger.info("  Updated Metadata for " + bcid.identifier());
//        } catch (EzidException e1) {
//            // After attempting to set the Metadata, if another exception is thrown then who knows,
//            // probably just a permissions issue.
//            throw new EzidException("  Exception thrown in attempting to create EZID " + bcid.identifier() + ", likely a permission issue", e1);
//        }
//    }
//
//    public void run() throws EzidException {
//        Set<Bcid> bcidsWithEzidRequest = bcidService.getBcidsWithEzidRequest();
//        EzidService ezidService = new EzidService();
//        ezidService.login(props.ezidUser(), props.ezidPass());
//
//        for (Bcid bcid : bcidsWithEzidRequest) {
//            try {
//                updateBcidsEZID(ezidService, bcid);
//                bcid.setEzidMade(true);
//                bcidService.update(bcid);
//            } catch (EzidException e) {
//                System.out.println("Failed to update EZID for bcidId: " + bcid.id());
//            }
//        }
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BcidAppConfig.class);
//        EzidUpdator updator = applicationContext.getBean(EzidUpdator.class);
//
//        updator.run();
//    }
//}
