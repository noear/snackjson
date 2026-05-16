package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * 测试：能根根据属性配置，自动选择更适合的构造方便
 *
 * @author noear 2026/5/16 created
 */
public class Issue_IJNQHU {

    @Test
    public void case1() {
        ONode oNode = new ONode();
        oNode.set("accessKey", "a");
        oNode.set("accessSecret", "b");

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assert "b".equals(tmp.getAccessSecret());
    }

    @Test
    public void case2() {
        ONode oNode = new ONode();
        oNode.set("accessKey", "a");
        oNode.set("accessSecret", "b");
        oNode.set("securityToken", "c");

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assert "c".equals(tmp.getSecurityToken());
    }

    public static class SessionCredentials {
        private final String accessKey;
        private final String accessSecret;
        private final String securityToken;

        public SessionCredentials(String accessKey, String accessSecret, String securityToken) {
            this.accessKey = accessKey;
            this.accessSecret = accessSecret;
            this.securityToken = securityToken;
        }

        public SessionCredentials(String accessKey, String accessSecret) {
            this.accessKey = accessKey;
            this.accessSecret = accessSecret;
            this.securityToken = null;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public String getAccessSecret() {
            return accessSecret;
        }

        public String getSecurityToken() {
            return securityToken;
        }

        @Override
        public String toString() {
            return "SessionCredentials{" +
                    "accessKey='" + accessKey + '\'' +
                    ", accessSecret='" + accessSecret + '\'' +
                    ", securityToken='" + securityToken + '\'' +
                    '}';
        }
    }
}