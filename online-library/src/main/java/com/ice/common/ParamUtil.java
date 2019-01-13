package com.ice.common;

import org.apache.commons.lang3.StringUtils;


public class ParamUtil {

    public static void isNullParam(Object... param) throws TException {
        for (Object o : param) {
            if (o == null) {
                throw new TException(Constant.PARAM_ERROR, "参数错误");
            }
        }
    }

    public static void isBlankParam(String... param) throws TException {
        for (String s : param) {
            if (StringUtils.isBlank(s)) {
                throw new TException(Constant.PARAM_ERROR, "参数错误");
            }
        }
    }

}