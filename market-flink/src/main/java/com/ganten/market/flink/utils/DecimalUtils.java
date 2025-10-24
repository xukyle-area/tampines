package com.ganten.market.flink.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import com.google.type.Decimal;

public abstract class DecimalUtils {

    @Nullable
    public static BigDecimal toBigDecimal(Decimal decimal) {
        if (decimal == null || StringUtils.isEmpty(decimal.getValue())) {
            return null;
        }
        return new BigDecimal(decimal.getValue());
    }

    @Nullable
    public static BigDecimal toBigDecimal(Decimal decimal, int scale) {
        if (decimal == null || StringUtils.isEmpty(decimal.getValue())) {
            return null;
        }
        return new BigDecimal(decimal.getValue()).setScale(scale, RoundingMode.HALF_EVEN);
    }

    @Nullable
    public static BigDecimal toBigDecimal(Decimal decimal, BigDecimal size) {
        return toBigDecimal(decimal, getScale(size));
    }

    @Nullable
    public static Decimal toDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return Decimal.newBuilder().setValue(bigDecimal.toString()).build();
    }

    @Nullable
    public static Decimal toDecimal(BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            return null;
        }
        return Decimal.newBuilder().setValue(bigDecimal.setScale(scale, RoundingMode.HALF_EVEN).toPlainString())
                .build();
    }

    @Nullable
    public static Decimal toDecimal(BigDecimal bigDecimal, BigDecimal size) {
        return toDecimal(bigDecimal, getScale(size));
    }

    public static int getScale(BigDecimal bd) {
        return bd.setScale(14, RoundingMode.HALF_EVEN).stripTrailingZeros().scale();
    }

    public static String decimalToString(BigDecimal num) {
        return num == null ? "" : num.toPlainString();
    }

    public static String decimalToString(Decimal decimal, int scale) {
        return decimal == null || StringUtils.isEmpty(decimal.getValue()) ? ""
                : new BigDecimal(decimal.getValue()).setScale(scale, RoundingMode.HALF_EVEN).toPlainString();
    }

    public static String setScale(String num, int scale) {
        return StringUtils.isEmpty(num) ? ""
                : new BigDecimal(num).setScale(scale, RoundingMode.HALF_EVEN).toPlainString();
    }

}
