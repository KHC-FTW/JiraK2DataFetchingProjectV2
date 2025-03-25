package org.ha.ckh637.component;

import lombok.Getter;
import org.ha.ckh637.config.SingletonConfig;
import org.ha.ckh637.utils.TimeUtil;

@Getter
public class RequestData {
    private final String year;
    private final String batch;
    private final String jiraBiweeklyPrnApi;
    private final String jiraBiweeklyUrgentServiceApi;
    private final String jFrogApi;

    public RequestData(String year, String batch){
        this.year = year;
        this.batch = batch;
        final SingletonConfig singletonConfig = SingletonConfig.getInstance();
        final String YEAR_BATCH = year + "_" + batch;
        jiraBiweeklyPrnApi = String.format(singletonConfig.getRawJiraAPIBiweeklyPrn(), YEAR_BATCH, year);
        final String YEAR_BATCH2 = year + "-" + batch;
        final String TODAY = TimeUtil.getTodayDate("dd-MMM-yyyy");
        final String UNRESOLVED_BEGIN_DATE = TimeUtil.calculateDate(TODAY, -7, "dd-MMM-yyyy", "yyyy-MM-dd");
        jiraBiweeklyUrgentServiceApi = String.format(singletonConfig.getRawJiraAPIBiweeklyUrgentService(), YEAR_BATCH2, YEAR_BATCH2, year, UNRESOLVED_BEGIN_DATE);
        jFrogApi = singletonConfig.getJfrogAPI();
    }

}
