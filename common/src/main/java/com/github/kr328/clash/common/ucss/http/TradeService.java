package com.github.kr328.clash.common.ucss.http;

import java.util.List;

/**
 * @author shangji_cd
 */
public class TradeService {
    public int serviceid;
    public String name;
    public String billingcycle;
    public String nextduedate;
    public boolean selected;
    public TradeServiceDetail details;
    public List<TradeServiceDetail.Node> servers;

    public long total;
    public long download;
    public long remain;
    public int progress;
}
