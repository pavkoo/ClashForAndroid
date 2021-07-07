package com.github.kr328.clash.common.ucss.http;

/**
 * @author shangji_cd
 */
public class TradeServiceDetail {
    public Brand bandwidth;


    public static class Brand {
        public long total;
        public long download;
        public long upload;
    }


    /**
     * "name": "Taiwan-3",
     * "server": "tw-3.ucss.ninja",
     * "remark": "Netflix",
     * "location": "tw",
     * "is_single_port_mode": true,
     * "mu_port": "12303",
     * "mu_protocol": "origin",
     * "mu_method": "chacha20-ietf",
     * "mu_obfs": "http_simple",
     * "mu_password": "undercurrentss",
     * "mu_obfs_param": "windowsupdate.com",
     * "mu_custom_dns1": "8.8.8.8",
     * "mu_custom_dns2": "1.1.1.1"
     */
    public static class Node {
        public String name;
    }
}
