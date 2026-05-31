package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.time.Duration;

/**
 *
 * @author noear 2026/5/31 created
 * @since 4.0
 */
public class DurationEncoder implements ObjectEncoder<Duration> {
    @Override
    public ONode encode(EncodeContext ctx, Duration value, ONode target) {
        if (ctx.hasFeature(Feature.Write_DurationUsingSimple)) {
            return target.setValue(formatSimple(value));
        }

        return target.setValue(value.toString());
    }

    public static String formatSimple(Duration duration) {
        if (duration == null || duration.isZero()) {
            return "0ms";
        }

        long secs = duration.getSeconds();
        int nanos = duration.getNano();

        // 只有当纳秒部分为 0 时，才考虑 天、小时、分、秒 的整除判断
        if (nanos == 0) {
            if (secs % 86400 == 0) {
                return (secs / 86400) + "d";
            }
            if (secs % 3600 == 0) {
                return (secs / 3600) + "h";
            }
            if (secs % 60 == 0) {
                return (secs / 60) + "m";
            }
            return secs + "s";
        }

        // 如果有纳秒尾数，降级为毫秒（框架通常推荐处理到毫秒即可）
        // 或者是无法被整秒除尽的微秒/纳秒
        long totalMillis = duration.toMillis();

        // 再次兜底：如果毫秒也变成 0 了（说明是非常微小的纳秒/微秒），为了不丢精度，可以使用原生 toString 或打印 ns
        if (totalMillis == 0) {
            return duration.toNanos() + "ns"; // 极小数据量，不会溢出
        }

        return totalMillis + "ms";
    }
}