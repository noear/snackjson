package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试：能根据属性配置，自动选择更适合的构造方法
 *
 * @author noear 2026/5/16 created
 */
public class Issue_IJNQHU {

    // ========== case1~3: 基础场景（二参 vs 三参） ==========

    @Test
    public void case1() {
        ONode oNode = new ONode();
        oNode.set("accessKey", "a");
        oNode.set("accessSecret", "b");

        //应该自动选择二参的构造方法
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

        //应该自动选择三参的构造方法
        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assert "c".equals(tmp.getSecurityToken());
    }

    @Test
    public void case3() {
        ONode oNode = new ONode();
        oNode.set("accessKey", "a");
        oNode.set("accessSecret", "b");
        oNode.set("xxx", "c");
        oNode.set("yyy", "d");

        //应该自动选择二参的构造方法（多余字段不影响匹配）
        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assert "b".equals(tmp.getAccessSecret());
        assert tmp.getSecurityToken() == null;
    }

    // ========== case4~6: 边界场景 ==========

    @Test
    public void case4_empty() {
        //空对象反序列化返回 null（无字段可匹配构造器）
        ONode oNode = new ONode();

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertNull(tmp);
    }

    @Test
    public void case5_onlyOneField() {
        //只提供一个字段，参数部分匹配
        ONode oNode = new ONode();
        oNode.set("accessKey", "a");

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertEquals("a", tmp.getAccessKey());
    }

    @Test
    public void case6_allFieldsPopulated() {
        //所有字段都有值，应该选择三参构造
        ONode oNode = new ONode();
        oNode.set("accessKey", "key123");
        oNode.set("accessSecret", "secret456");
        oNode.set("securityToken", "token789");

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertEquals("key123", tmp.getAccessKey());
        assertEquals("secret456", tmp.getAccessSecret());
        assertEquals("token789", tmp.getSecurityToken());
    }

    // ========== case7~9: 多构造器场景（三个不同参数数量的构造器） ==========

    @Test
    public void case7_threeCtors_matchOne() {
        //只有一个字段匹配 -> 走单参构造
        ONode oNode = new ONode();
        oNode.set("name", "test");

        MultiCtorBean tmp = oNode.toBean(MultiCtorBean.class);

        System.out.println(tmp);
        assertEquals("test", tmp.getName());
        assertEquals(0, tmp.getAge());
        assertNull(tmp.getAddress());
    }

    @Test
    public void case8_threeCtors_matchTwo() {
        //两个字段匹配 -> 走二参构造（而非三参）
        ONode oNode = new ONode();
        oNode.set("name", "test");
        oNode.set("age", 20);

        MultiCtorBean tmp = oNode.toBean(MultiCtorBean.class);

        System.out.println(tmp);
        assertEquals("test", tmp.getName());
        assertEquals(20, tmp.getAge());
        assertNull(tmp.getAddress());
    }

    @Test
    public void case9_threeCtors_matchAll() {
        //三个字段都匹配 -> 走三参构造
        ONode oNode = new ONode();
        oNode.set("name", "test");
        oNode.set("age", 20);
        oNode.set("address", "earth");

        MultiCtorBean tmp = oNode.toBean(MultiCtorBean.class);

        System.out.println(tmp);
        assertEquals("test", tmp.getName());
        assertEquals(20, tmp.getAge());
        assertEquals("earth", tmp.getAddress());
    }

    // ========== case10: 从 JSON 字符串反序列化 ==========

    @Test
    public void case10_fromJsonString() {
        //从 JSON 字符串反序列化也应正确选择构造器
        String json = "{\"accessKey\":\"ak\",\"accessSecret\":\"as\",\"securityToken\":\"st\"}";

        SessionCredentials tmp = ONode.ofJson(json).toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertEquals("ak", tmp.getAccessKey());
        assertEquals("as", tmp.getAccessSecret());
        assertEquals("st", tmp.getSecurityToken());
    }

    @Test
    public void case11_fromJsonString_partial() {
        //从 JSON 字符串反序列化，只有部分字段
        String json = "{\"accessKey\":\"ak\",\"accessSecret\":\"as\"}";

        SessionCredentials tmp = ONode.ofJson(json).toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertEquals("ak", tmp.getAccessKey());
        assertEquals("as", tmp.getAccessSecret());
        assertNull(tmp.getSecurityToken());
    }

    // ========== case12: 不同参数类型（含基本类型和包装类型） ==========

    @Test
    public void case12_mixedTypes() {
        ONode oNode = new ONode();
        oNode.set("name", "test");
        oNode.set("age", 25);
        oNode.set("address", "mars");

        MultiCtorBean tmp = oNode.toBean(MultiCtorBean.class);

        System.out.println(tmp);
        assertEquals("test", tmp.getName());
        assertEquals(25, tmp.getAge());
        assertEquals("mars", tmp.getAddress());
    }

    // ========== case13~14: 字段名大小写/下划线场景 ==========

    @Test
    public void case13_fieldNameMismatch() {
        //字段名完全不匹配构造参数时，仍能走默认构造器
        ONode oNode = new ONode();
        oNode.set("foo", "bar");
        oNode.set("baz", 123);

        SessionCredentials tmp = oNode.toBean(SessionCredentials.class);

        System.out.println(tmp);
        assertNull(tmp.getAccessKey());
        assertNull(tmp.getAccessSecret());
        assertNull(tmp.getSecurityToken());
    }

    // ========== case15: 嵌套复杂类型构造器选择 ==========

    @Test
    public void case15_nestedType() {
        //构造器参数包含复杂类型
        ONode oNode = new ONode();
        oNode.set("title", "hello");
        oNode.set("tags", ONode.ofJson("[\"a\",\"b\",\"c\"]"));

        ComplexCtorBean tmp = oNode.toBean(ComplexCtorBean.class);

        System.out.println(tmp);
        assertEquals("hello", tmp.getTitle());
        assertNotNull(tmp.getTags());
        assertEquals(3, tmp.getTags().size());
        assertEquals("a", tmp.getTags().get(0));
    }

    @Test
    public void case16_nestedType_full() {
        ONode oNode = new ONode();
        oNode.set("title", "hello");
        oNode.set("tags", ONode.ofJson("[\"a\",\"b\"]"));
        oNode.set("priority", 5);

        ComplexCtorBean tmp = oNode.toBean(ComplexCtorBean.class);

        System.out.println(tmp);
        assertEquals("hello", tmp.getTitle());
        assertNotNull(tmp.getTags());
        assertEquals(2, tmp.getTags().size());
        assertEquals(5, tmp.getPriority());
    }

    // ===================== 内部测试类 =====================

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

    /**
     * 三个不同参数数量的构造器
     */
    public static class MultiCtorBean {
        private final String name;
        private final int age;
        private final String address;

        public MultiCtorBean(String name) {
            this.name = name;
            this.age = 0;
            this.address = null;
        }

        public MultiCtorBean(String name, int age) {
            this.name = name;
            this.age = age;
            this.address = null;
        }

        public MultiCtorBean(String name, int age, String address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "MultiCtorBean{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    /**
     * 含复杂类型参数的构造器
     */
    public static class ComplexCtorBean {
        private final String title;
        private final List<String> tags;
        private final int priority;

        public ComplexCtorBean(String title, List<String> tags) {
            this.title = title;
            this.tags = tags;
            this.priority = 0;
        }

        public ComplexCtorBean(String title, List<String> tags, int priority) {
            this.title = title;
            this.tags = tags;
            this.priority = priority;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getTags() {
            return tags;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return "ComplexCtorBean{" +
                    "title='" + title + '\'' +
                    ", tags=" + tags +
                    ", priority=" + priority +
                    '}';
        }
    }
}