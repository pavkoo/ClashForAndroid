package com.github.kr328.clash.common.ucss.http;

import java.util.List;

/**
 * @author shangji_cd
 */
public class UserInONe {
    public Profile profile;
    public List<TradeService> services;

    public static class Profile{
//{"profile":{"userid":2085,"uuid":"8c69e9a7-7435-4f5b-ab22-02ab7fe525ba","email":"hello@undercurrentss.com","firstname":"TEST","lastname":"UCSS"}
        public int userid;
        public String uuid;
        public String email;
        public String firstname;
        public String lastname;
    }
}
