package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Duration 简化格式 与 Path 编码 单测
 *
 * @author noear 2026/5/31 created
 * @since 4.0
 */
public class DurationAndPathTest {

    // ============================================================
    // Duration 相关测试
    // ============================================================

    @Test
    public void duration_default_usesISO() {
        // 默认模式：使用 Duration.toString()，输出 ISO-8601 格式
        Duration d = Duration.ofHours(2);
        String json = ONode.ofBean(d).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"PT2H\"", json);
    }

    @Test
    public void duration_simple_hours() {
        Duration d = Duration.ofHours(2);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"2h\"", json);
    }

    @Test
    public void duration_simple_days() {
        Duration d = Duration.ofDays(3);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"3d\"", json);
    }

    @Test
    public void duration_simple_minutes() {
        Duration d = Duration.ofMinutes(30);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"30m\"", json);
    }

    @Test
    public void duration_simple_seconds() {
        Duration d = Duration.ofSeconds(45);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"45s\"", json);
    }

    @Test
    public void duration_simple_millis() {
        Duration d = Duration.ofMillis(500);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"500ms\"", json);
    }

    @Test
    public void duration_simple_nanos() {
        Duration d = Duration.ofNanos(100);
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"100ns\"", json);
    }

    @Test
    public void duration_simple_zero() {
        Duration d = Duration.ZERO;
        String json = ONode.ofBean(d, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"0ms\"", json);
    }

    @Test
    public void duration_in_bean_simple() {
        DurationBean bean = new DurationBean();
        bean.duration = Duration.ofHours(5);

        String json = ONode.ofBean(bean, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"duration\":\"5h\"}", json);
    }

    @Test
    public void duration_in_bean_default() {
        DurationBean bean = new DurationBean();
        bean.duration = Duration.ofHours(5);

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"duration\":\"PT5H\"}", json);
    }

    @Test
    public void duration_simple_roundtrip_hours() {
        // 简化格式编码 -> 解码还原
        Duration original = Duration.ofHours(2);
        String json = ONode.ofBean(original, Feature.Write_DurationUsingSimple).toJson();
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_simple_roundtrip_days() {
        Duration original = Duration.ofDays(1);
        String json = ONode.ofBean(original, Feature.Write_DurationUsingSimple).toJson();
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_simple_roundtrip_minutes() {
        Duration original = Duration.ofMinutes(90);
        String json = ONode.ofBean(original, Feature.Write_DurationUsingSimple).toJson();
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_simple_roundtrip_seconds() {
        Duration original = Duration.ofSeconds(120);
        String json = ONode.ofBean(original, Feature.Write_DurationUsingSimple).toJson();
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_simple_roundtrip_millis() {
        Duration original = Duration.ofMillis(1500);
        String json = ONode.ofBean(original, Feature.Write_DurationUsingSimple).toJson();
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_default_roundtrip() {
        Duration original = Duration.ofHours(1).plusMinutes(30).plusSeconds(15);
        String json = ONode.ofBean(original).toJson();
        System.out.println(json);
        Duration restored = ONode.ofJson(json).toBean(Duration.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void duration_in_map_simple() {
        Map<String, Object> data = new HashMap<>();
        data.put("timeout", Duration.ofSeconds(30));

        String json = ONode.ofBean(data, Feature.Write_DurationUsingSimple).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"timeout\":\"30s\"}", json);
    }

    // ============================================================
    // Path 编解码测试
    // ============================================================

    @Test
    public void path_encode() {
        Path path = Paths.get("tmp", "test.txt");
        String json = ONode.ofBean(path).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"tmp/test.txt\"", json);
    }

    @Test
    public void path_decode() {
        String json = "\"tmp/test.txt\"";
        Path path = ONode.ofJson(json).toBean(Path.class);
        System.out.println(path);
        Assertions.assertEquals(Paths.get("tmp", "test.txt"), path);
    }

    @Test
    public void path_roundtrip() {
        Path original = Paths.get("home", "user", "data", "file.json");
        String json = ONode.ofBean(original).toJson();
        System.out.println(json);
        Path restored = ONode.ofJson(json).toBean(Path.class);
        Assertions.assertEquals(original, restored);
    }

    @Test
    public void path_in_bean_encode() {
        PathBean bean = new PathBean();
        bean.path = Paths.get("var", "log", "app.log");

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"path\":\"var/log/app.log\"}", json);
    }

    @Test
    public void path_in_bean_decode() {
        String json = "{\"path\":\"var/log/app.log\"}";
        PathBean bean = ONode.ofJson(json).toBean(PathBean.class);
        System.out.println(bean.path);
        Assertions.assertEquals(Paths.get("var", "log", "app.log"), bean.path);
    }

    @Test
    public void path_in_bean_roundtrip() {
        PathBean bean = new PathBean();
        bean.path = Paths.get("etc", "conf", "settings.xml");

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        PathBean restored = ONode.ofJson(json).toBean(PathBean.class);
        Assertions.assertEquals(bean.path, restored.path);
    }

    @Test
    public void path_in_map() {
        Map<String, Object> data = new HashMap<>();
        data.put("file", Paths.get("docs", "readme.md"));

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"file\":\"docs/readme.md\"}", json);
    }

    @Test
    public void path_root() {
        Path path = Paths.get("/");
        String json = ONode.ofBean(path).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"/\"", json);
    }

    @Test
    public void path_simple_name() {
        Path path = Paths.get("config.yml");
        String json = ONode.ofBean(path).toJson();
        System.out.println(json);
        Assertions.assertEquals("\"config.yml\"", json);
    }

    @Test
    public void path_null() {
        PathBean bean = new PathBean();
        // path is null
        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);
    }

    @Test
    public void path_null_json() {
        String json = "{\"path\":null}";
        PathBean bean = ONode.ofJson(json).toBean(PathBean.class);
        Assertions.assertNull(bean.path);
    }

    // ============================================================
    // 内部数据类
    // ============================================================

    static class DurationBean {
        public Duration duration;
    }

    static class PathBean {
        public Path path;
    }
}
