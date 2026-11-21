package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 属性级 excludeFeatures（排除全局写入特性）
 *
 * @author noear 2026/11/20 created
 */
public class ExcludeFeaturesTest {
    public static class LongBean {
        public long id = 1234567890123456789L;

        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public long version = 9007199254740993L;

        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public Long boxed = 7L;

        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public Long nullValue;
    }

    @Test
    public void case1_long() {
        String json = ONode.serialize(new LongBean(), Feature.Write_LongAsString);
        System.out.println(json);
        Assertions.assertEquals("{\"id\":\"1234567890123456789\",\"version\":9007199254740993,\"boxed\":7}", json);

        json = ONode.serialize(new LongBean(), Feature.Write_LongAsString, Feature.Write_Nulls);
        System.out.println(json);
        Assertions.assertEquals("{\"id\":\"1234567890123456789\",\"version\":9007199254740993,\"boxed\":7,\"nullValue\":null}", json);

        //反序列化还原
        LongBean bean = ONode.deserialize(json, LongBean.class);
        Assertions.assertEquals(1234567890123456789L, bean.id);
        Assertions.assertEquals(9007199254740993L, bean.version);
        Assertions.assertEquals(7L, bean.boxed);
        Assertions.assertNull(bean.nullValue);
    }

    @Test
    public void case2_noGlobal() {
        //未开全局特性时，排除无副作用
        String json = ONode.serialize(new LongBean());
        Assertions.assertEquals("{\"id\":1234567890123456789,\"version\":9007199254740993,\"boxed\":7}", json);
    }

    public static class Inner {
        public long a = 1L;
        public boolean b = true;
    }

    public static class TreeBean {
        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public List<Long> ids = Arrays.asList(1L, 2L);

        @ONodeAttr(excludeFeatures = {Feature.Write_LongAsString, Feature.Write_BooleanAsNumber})
        public Inner inner = new Inner();

        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public Map<String, Long> map = new LinkedHashMap<>();

        public Inner other = new Inner();

        public TreeBean() {
            map.put("k", 3L);
        }
    }

    @Test
    public void case3_subtree() {
        String json = ONode.serialize(new TreeBean(), Feature.Write_LongAsString, Feature.Write_BooleanAsNumber);
        System.out.println(json);
        Assertions.assertEquals("{\"ids\":[1,2],\"inner\":{\"a\":1,\"b\":true},\"map\":{\"k\":3},\"other\":{\"a\":\"1\",\"b\":1}}", json);

        //子树内的节点单独写出，结果一致
        ONode root = ONode.ofBean(new TreeBean(), Options.of(Feature.Write_LongAsString));
        Assertions.assertEquals("1", root.get("ids").get(0).toJson());
        Assertions.assertEquals("1", root.get("inner").get("a").toJson());
        Assertions.assertEquals("\"1\"", root.get("other").get("a").toJson());
    }

    @Test
    public void case3_pretty() {
        String json = ONode.serialize(new TreeBean(), Feature.Write_LongAsString, Feature.Write_PrettyFormat);
        String expected = ONode.serialize(new TreeBean(), Feature.Write_PrettyFormat)
                .replace("\"a\": 1,\n    \"b\": true\n  }\n}", "\"a\": \"1\",\n    \"b\": true\n  }\n}");
        System.out.println(json);
        Assertions.assertEquals(expected, json);
    }

    public static class FlatBean {
        public long id = 1L;

        @ONodeAttr(flat = true, excludeFeatures = Feature.Write_LongAsString)
        public Inner inner = new Inner();
    }

    @Test
    public void case4_flat() {
        String json = ONode.serialize(new FlatBean(), Feature.Write_LongAsString);
        System.out.println(json);
        Assertions.assertEquals("{\"id\":\"1\",\"a\":1,\"b\":true}", json);
    }

    public static class AllBean {
        public double d1 = 1.5D;
        @ONodeAttr(excludeFeatures = Feature.Write_DoubleAsString)
        public double d2 = 1.5D;

        public BigDecimal m1 = new BigDecimal("1E+3");
        @ONodeAttr(excludeFeatures = Feature.Write_BigDecimalAsPlain)
        public BigDecimal m2 = new BigDecimal("1E+3");

        public boolean b1 = true;
        @ONodeAttr(excludeFeatures = Feature.Write_BooleanAsNumber)
        public boolean b2 = true;

        public Date t1 = new Date(1000L);
        @ONodeAttr(excludeFeatures = Feature.Write_UseDateFormat)
        public Date t2 = new Date(1000L);
    }

    @Test
    public void case5_features() {
        ONode node = ONode.ofBean(new AllBean(), Options.of(
                Feature.Write_DoubleAsString,
                Feature.Write_BigDecimalAsPlain,
                Feature.Write_BooleanAsNumber,
                Feature.Write_UseDateFormat));
        String json = node.toJson();
        System.out.println(json);

        Assertions.assertEquals("\"1.5\"", node.get("d1").toJson());
        Assertions.assertEquals("1.5", node.get("d2").toJson());
        Assertions.assertEquals("\"1000\"", node.get("m1").toJson());
        Assertions.assertEquals("1E+3", node.get("m2").toJson());
        Assertions.assertEquals("1", node.get("b1").toJson());
        Assertions.assertEquals("true", node.get("b2").toJson());
        Assertions.assertTrue(node.get("t1").toJson().startsWith("\""));
        Assertions.assertEquals("1000", node.get("t2").toJson());

        Assertions.assertTrue(json.contains("\"d1\":\"1.5\""));
        Assertions.assertTrue(json.contains("\"d2\":1.5"));
        Assertions.assertTrue(json.contains("\"m2\":1E+3"));
        Assertions.assertTrue(json.contains("\"b1\":1"));
        Assertions.assertTrue(json.contains("\"b2\":true"));
        Assertions.assertTrue(json.contains("\"t2\":1000"));

        //getString 也遵循排除
        Assertions.assertEquals("1000", node.get("m1").getString());
        Assertions.assertEquals("1E+3", node.get("m2").getString());
    }

    public static class SuffixBean {
        public long a = 1L;

        @ONodeAttr(excludeFeatures = Feature.Write_NumberTypeSuffix)
        public long b = 2L;

        @ONodeAttr(excludeFeatures = Feature.Write_NumbersAsString)
        public long c = 3L;

        @ONodeAttr(excludeFeatures = {Feature.Write_NumbersAsString, Feature.Write_LongAsString})
        public long d = 4L;
    }

    @Test
    public void case6_suffix() {
        String json = ONode.serialize(new SuffixBean(), Feature.Write_NumberTypeSuffix);
        Assertions.assertEquals("{\"a\":1L,\"b\":2,\"c\":3L,\"d\":4L}", json);

        //按字面排除：全局同时开 NumbersAsString 和 LongAsString 时，需两个都排除
        json = ONode.serialize(new SuffixBean(), Feature.Write_NumbersAsString, Feature.Write_LongAsString);
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\",\"d\":4}", json);
    }

    public static class PriorityBean {
        @ONodeAttr(features = Feature.Write_LongAsString)
        public long a = 1L;

        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public long b = 2L;
    }

    @Test
    public void case7_priority() {
        //属性开启 > 属性排除 > 全局
        Assertions.assertEquals("{\"a\":\"1\",\"b\":2}", ONode.serialize(new PriorityBean()));
        Assertions.assertEquals("{\"a\":\"1\",\"b\":2}", ONode.serialize(new PriorityBean(), Feature.Write_LongAsString));
    }

    public static class ConflictBean {
        @ONodeAttr(features = Feature.Write_LongAsString, excludeFeatures = Feature.Write_LongAsString)
        public long a = 1L;
    }

    public static class UnsupportedBean {
        @ONodeAttr(excludeFeatures = Feature.Write_PrettyFormat)
        public long a = 1L;

        //不支持的项被忽略，合法的项照常生效
        @ONodeAttr(excludeFeatures = {Feature.Write_PrettyFormat, Feature.Write_LongAsString})
        public long b = 2L;
    }

    @Test
    public void case8_validate() {
        //非法配置只记 warn 日志并忽略该项，不抛异常
        //冲突：以 features 开启为准
        Assertions.assertEquals("{\"a\":\"1\"}", ONode.serialize(new ConflictBean()));
        Assertions.assertEquals("{\"a\":\"1\"}", ONode.serialize(new ConflictBean(), Feature.Write_LongAsString));

        //不支持：该项无效
        Assertions.assertEquals("{\"a\":\"1\",\"b\":2}", ONode.serialize(new UnsupportedBean(), Feature.Write_LongAsString));
    }

    @Test
    public void case9_detachAndReattach() {
        ONode root = ONode.ofBean(new LongBean(), Options.of(Feature.Write_LongAsString));

        //单独取出属性节点
        Assertions.assertEquals("9007199254740993", root.get("version").toJson());
        Assertions.assertEquals("\"1234567890123456789\"", root.get("id").toJson());

        //放进另一棵树，用另一套选项写出
        ONode other = new ONode(Options.of(Feature.Write_LongAsString)).asObject();
        other.set("v", root.get("version"));
        other.set("x", 5L);
        Assertions.assertEquals("{\"v\":9007199254740993,\"x\":\"5\"}", other.toJson());
    }

    public static class NodeBean {
        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public ONode raw = new ONode(9L);
    }

    @Test
    public void case10_onodeValue() {
        NodeBean bean = new NodeBean();
        Options before = bean.raw.options();

        //用户给的 ONode 不修改（已知限制：排除不生效）
        String json = ONode.serialize(bean, Feature.Write_LongAsString);
        Assertions.assertEquals("{\"raw\":\"9\"}", json);
        Assertions.assertSame(before, bean.raw.options());
    }

    @Test
    public void case11_options() {
        Options base = Options.of(Feature.Write_LongAsString, Feature.Write_PrettyFormat);
        long ex = Feature.Write_LongAsString.mask();

        Assertions.assertSame(base, base.deriveWithout(0L));

        Options derived = base.deriveWithout(ex);
        Assertions.assertSame(derived, base.deriveWithout(ex));
        Assertions.assertEquals(ex, derived.getWriteExcludes());
        Assertions.assertFalse(derived.hasFeature(Feature.Write_LongAsString));
        Assertions.assertTrue(derived.hasFeature(Feature.Write_PrettyFormat));
        Assertions.assertEquals(0L, base.getWriteExcludes());

        //派生选项只读
        Assertions.assertThrows(UnsupportedOperationException.class, () -> derived.addFeatures(Feature.Write_Nulls));

        //派生选项共享编解码仓库（后加的编码器也可见）
        base.addEncoder(StringBuilder.class, (ctx, value, target) -> target.setValue("sb"));
        Assertions.assertNotNull(derived.getEncoder(new StringBuilder()));

        //原选项被修改后，缓存失效
        base.addFeatures(Feature.Write_Nulls);
        Options derived2 = base.deriveWithout(ex);
        Assertions.assertNotSame(derived, derived2);
        Assertions.assertTrue(derived2.hasFeature(Feature.Write_Nulls));

        //派生的派生，排除累积
        Options derived3 = derived2.deriveWithout(Feature.Write_PrettyFormat.mask());
        Assertions.assertEquals(ex | Feature.Write_PrettyFormat.mask(), derived3.getWriteExcludes());
    }

    @Test
    public void case11_nestedDerive() {
        Options base = Options.of(Feature.Write_LongAsString, Feature.Write_BooleanAsNumber);
        Options derived = base.deriveWithout(Feature.Write_LongAsString.mask());

        //已排除的不再派生
        Assertions.assertSame(derived, derived.deriveWithout(Feature.Write_LongAsString.mask()));

        //派生选项 copy 后为普通可写选项
        Options copied = derived.copy();
        Assertions.assertEquals(0L, copied.getWriteExcludes());
        Assertions.assertFalse(copied.hasFeature(Feature.Write_LongAsString));
        copied.addFeatures(Feature.Write_Nulls);
    }

    public static class FlatMapBean {
        @ONodeAttr(flat = true, excludeFeatures = Feature.Write_LongAsString)
        public Map<String, Object> ext = new LinkedHashMap<>();
    }

    @Test
    public void case13_flatUserNode() {
        Options userOpts = Options.of(Feature.Write_LongAsString);
        ONode userNode = new ONode(userOpts, 8L);

        FlatMapBean bean = new FlatMapBean();
        bean.ext.put("a", 1L);
        bean.ext.put("u", userNode);

        String json = ONode.serialize(bean, Feature.Write_LongAsString);
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1,\"u\":\"8\"}", json);

        //用户的 ONode 不被修改
        Assertions.assertSame(userOpts, userNode.options());
        Assertions.assertEquals("\"8\"", userNode.toJson());
        userNode.options().addFeatures(Feature.Write_Nulls);
    }

    public static class SharedNodeEncoder implements ObjectEncoder<Long> {
        static final ONode SHARED = new ONode(Options.of(Feature.Write_LongAsString), 6L);

        @Override
        public ONode encode(EncodeContext ctx, Long value, ONode target) {
            return SHARED;
        }
    }

    public static class SharedBean {
        @ONodeAttr(encoder = SharedNodeEncoder.class, excludeFeatures = Feature.Write_LongAsString)
        public Long a = 1L;
    }

    @Test
    public void case14_sharedNode() {
        Options before = SharedNodeEncoder.SHARED.options();

        //编码器返回共享节点：不享受排除，但也不被修改
        String json = ONode.serialize(new SharedBean(), Feature.Write_LongAsString);
        Assertions.assertEquals("{\"a\":\"6\"}", json);
        Assertions.assertSame(before, SharedNodeEncoder.SHARED.options());
        Assertions.assertEquals("\"6\"", SharedNodeEncoder.SHARED.toJson());
    }

    public static class TargetEncoder implements ObjectEncoder<Long> {
        @Override
        public ONode encode(EncodeContext ctx, Long value, ONode target) {
            return target.setValue(value + 1);
        }
    }

    public static class TargetBean {
        @ONodeAttr(encoder = TargetEncoder.class, excludeFeatures = Feature.Write_LongAsString)
        public Long a = 1L;
    }

    @Test
    public void case15_encoderTarget() {
        //编码器使用框架给的 target：排除生效
        Assertions.assertEquals("{\"a\":2}", ONode.serialize(new TargetBean(), Feature.Write_LongAsString));
    }

    public static class MapUserNodeBean {
        @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
        public Map<String, Object> ext = new LinkedHashMap<>();
    }

    @Test
    public void case16_mapUserNode() {
        ONode userNode = new ONode(Options.of(Feature.Write_LongAsString), 8L);

        MapUserNodeBean bean = new MapUserNodeBean();
        bean.ext.put("a", 1L);
        bean.ext.put("u", userNode);

        //用户的 ONode 不享受排除（排除不会向下渗透到非框架节点）；不管从整棵树还是单独写出，结果一致
        ONode root = ONode.ofBean(bean, Options.of(Feature.Write_LongAsString));
        Assertions.assertEquals("{\"ext\":{\"a\":1,\"u\":\"8\"}}", root.toJson());
        Assertions.assertEquals("\"8\"", root.get("ext").get("u").toJson());
        Assertions.assertEquals("1", root.get("ext").get("a").toJson());

        //从中间节点写出时，以起点节点的 Options 为全局选项（与原有 toJson 规则一致）
        Assertions.assertEquals("{\"a\":1,\"u\":8}", root.get("ext").toJson());
    }

    @Test
    public void case12_repeatAndConcurrent() throws Exception {
        String expected = "{\"id\":\"1234567890123456789\",\"version\":9007199254740993,\"boxed\":7}";

        for (int i = 0; i < 100; i++) {
            Assertions.assertEquals(expected, ONode.serialize(new LongBean(), Feature.Write_LongAsString));
        }

        Options shared = Options.of(Feature.Write_LongAsString);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                futures.add(executor.submit(() -> ONode.serialize(new LongBean(), shared)));
            }

            for (Future<String> f : futures) {
                Assertions.assertEquals(expected, f.get());
            }
        } finally {
            executor.shutdown();
        }
    }
}
