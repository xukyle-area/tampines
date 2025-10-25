package com.ganten.market.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.lang.StringUtils;

public abstract class BigDecimalUtils {
    public static int getScale(BigDecimal bd) {
        return bd.setScale(14, RoundingMode.HALF_EVEN).stripTrailingZeros().scale();
    }

    public static String decimalToString(BigDecimal num) {
        return num == null ? "" : num.toPlainString();
    }

    public static String setScale(String num, int scale) {
        return StringUtils.isEmpty(num) ? ""
                : new BigDecimal(num).setScale(scale, RoundingMode.HALF_EVEN).toPlainString();
    }

}
