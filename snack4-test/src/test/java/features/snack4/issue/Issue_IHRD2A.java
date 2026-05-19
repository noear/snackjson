package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.LocalDateTime;

/**
 *
 * @author noear 2026/5/19 created
 *
 */
public class Issue_IHRD2A {
    String json = "{\"appId\":61,\"createTime\":\"2026-03-25 11:00:00.152636324\",\"eventCode\":\"compliance_grade\",\"msgType\":1,\"openMode\":1,\"params\":{\"item\":\"等保提醒\",\"occurAt\":\"2026-03-25 11:00:00\",\"cause\":\"等保提醒\",\"source\":\"等保合规\"}}";

    @Test
    public void case1() {
        Demo tmp =  ONode.ofJson(json).toBean(Demo.class);
        System.out.println(tmp);
    }

    public static class Demo {
        long appId;
        LocalDateTime createTime;

        @Override
        public String toString() {
            return "Demo{" +
                    "appId=" + appId +
                    ", createTime=" + createTime +
                    '}';
        }
    }
}