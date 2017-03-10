package com.martin.servlet;

import javax.servlet.http.HttpServlet;

/**
 * @author ZXY
 * @ClassName: TokenServer
 * @Description:
 * @date 2017/3/8 16:02
 */
public class TokenServer extends HttpServlet {

    private static final long serialVersionUID = 2266454704285130147L;

    public static String accessToken = "I8-PN3yATBipfnUEwEsCzhUl4t9nJ0p30kNM8aO4aZigXkh_s6wNG65sbhwTf4gBMNF6O_3XazIsOBPa8AAezUb9nr7nycTWvPAXMuXBiVXHDPks9BRfYFlLaYebyW_IXZCgAFAIKN";

    static {
        try {
//            accessToken = TenPublicUtils.getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
