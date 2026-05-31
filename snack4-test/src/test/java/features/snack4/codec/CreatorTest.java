package features.snack4.codec;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectCreator;

/**
 * @ONodeAttr(creator) 单测
 *
 * @author noear 2026/5/31 created
 * @since 4.0
 */
public class CreatorTest {

    // ============================================================
    // 字段级 creator（属性注解方式）
    // ============================================================

    @Test
    public void creator_field_level() {
        // 字段上标注 creator，解码时使用自定义 creator 创建实例
        String json = "{\"child\":{\"name\":\"Alice\"}}";
        ParentModel parent = ONode.ofJson(json).toBean(ParentModel.class);
        System.out.println(parent.getChild().getName());
        System.out.println(parent.getChild().getCreatedBy());

        Assertions.assertEquals("Alice", parent.getChild().getName());
        Assertions.assertEquals("FieldChildCreator", parent.getChild().getCreatedBy());
    }

    @Test
    public void creator_field_level_null_json() {
        // JSON 中属性为 null，creator 不应被调用
        String json = "{\"child\":null}";
        ParentModel parent = ONode.ofJson(json).toBean(ParentModel.class);
        Assertions.assertNull(parent.getChild());
    }

    @Test
    public void creator_field_level_missing_json() {
        // JSON 中没有该属性，creator 不应被调用
        String json = "{}";
        ParentModel parent = ONode.ofJson(json).toBean(ParentModel.class);
        Assertions.assertNull(parent.getChild());
    }

    @Test
    public void creator_field_level_encode_decode_roundtrip() {
        ParentModel parent = new ParentModel();
        ChildModel child = new ChildModel();
        child.setName("Bob");
        parent.setChild(child);

        String json = ONode.ofBean(parent).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"child\":{\"name\":\"Bob\"}}", json);

        ParentModel restored = ONode.ofJson(json).toBean(ParentModel.class);
        Assertions.assertEquals("Bob", restored.getChild().getName());
        Assertions.assertEquals("FieldChildCreator", restored.getChild().getCreatedBy());
    }

    // ============================================================
    // getter 级 creator
    // ============================================================

    @Test
    public void creator_getter_level() {
        // getter 方法上标注 creator
        String json = "{\"item\":{\"text\":\"hello\"}}";
        GetterModel obj = ONode.ofJson(json).toBean(GetterModel.class);
        System.out.println(obj.getItem().getText());
        System.out.println(obj.getItem().getCreatedBy());

        Assertions.assertEquals("hello", obj.getItem().getText());
        Assertions.assertEquals("GetterItemCreator", obj.getItem().getCreatedBy());
    }

    // ============================================================
    // setter 级 creator
    // ============================================================

    @Test
    public void creator_setter_level() {
        // setter 方法上标注 creator
        String json = "{\"item\":{\"text\":\"world\"}}";
        SetterModel obj = ONode.ofJson(json, Feature.Decode_AllowUseSetter).toBean(SetterModel.class);
        System.out.println(obj.getItem().getText());
        System.out.println(obj.getItem().getCreatedBy());

        Assertions.assertEquals("world", obj.getItem().getText());
        Assertions.assertEquals("SetterItemCreator", obj.getItem().getCreatedBy());
    }

    // ============================================================
    // Options 级 creator 与 @ONodeAttr(creator) 优先级
    // ============================================================

    @Test
    public void creator_attr_overrides_options() {
        // 同时有 Options 级和属性级 creator，属性级优先
        Options options = Options.of()
                .addCreator(ChildModel.class, (opts, node, clazz) -> {
                    ChildModel c = new ChildModel();
                    c.setCreatedBy("OptionsChildCreator");
                    return c;
                });

        String json = "{\"child\":{\"name\":\"test\"}}";
        ParentModel parent = ONode.ofJson(json, options).toBean(ParentModel.class);

        System.out.println(parent.getChild().getCreatedBy());
        // 属性级 creator 应优先
        Assertions.assertEquals("FieldChildCreator", parent.getChild().getCreatedBy());
    }

    @Test
    public void creator_options_level_no_attr() {
        // 没有 @ONodeAttr(creator)，使用 Options 级 creator
        Options options = Options.of()
                .addCreator(ChildModel.class, (opts, node, clazz) -> {
                    ChildModel c = new ChildModel();
                    c.setCreatedBy("OptionsChildCreator");
                    return c;
                });

        String json = "{\"child\":{\"name\":\"test\"}}";
        ParentModelNoCreator parent = ONode.ofJson(json, options).toBean(ParentModelNoCreator.class);

        System.out.println(parent.getChild().getCreatedBy());
        Assertions.assertEquals("OptionsChildCreator", parent.getChild().getCreatedBy());
    }

    @Test
    public void creator_options_level_fallback() {
        // 无属性级 creator 也无 Options 级 creator，走默认构造
        String json = "{\"child\":{\"name\":\"default\"}}";
        ParentModelNoCreator parent = ONode.ofJson(json).toBean(ParentModelNoCreator.class);

        Assertions.assertEquals("default", parent.getChild().getName());
        Assertions.assertNull(parent.getChild().getCreatedBy());
    }

    // ============================================================
    // 多字段各自独立 creator
    // ============================================================

    @Test
    public void creator_multiple_fields_independent() {
        // 一个 Bean 有多个字段，各字段各自指定不同的 creator
        String json = "{\"a\":{\"text\":\"aaa\"},\"b\":{\"text\":\"bbb\"}}";
        MultiFieldModel obj = ONode.ofJson(json).toBean(MultiFieldModel.class);

        Assertions.assertEquals("aaa", obj.getA().getText());
        Assertions.assertEquals("CreatorA", obj.getA().getCreatedBy());
        Assertions.assertEquals("bbb", obj.getB().getText());
        Assertions.assertEquals("CreatorB", obj.getB().getCreatedBy());
    }

    // ============================================================
    // creator 结合 decoder 组合使用
    // ============================================================

    @Test
    public void creator_with_decoder() {
        // creator 创建实例 + decoder 做额外解码逻辑
        String json = "{\"data\":{\"value\":\"v1\"}}";
        CreatorAndDecoderModel obj = ONode.ofJson(json).toBean(CreatorAndDecoderModel.class);

        System.out.println(obj.getData().getValue());
        System.out.println(obj.getData().getCreatedBy());

        Assertions.assertEquals("v1", obj.getData().getValue());
        Assertions.assertEquals("MyCreator", obj.getData().getCreatedBy());
    }

    // ============================================================
    // creator 创建的对象作为嵌套 bean 继续解码
    // ============================================================

    @Test
    public void creator_nested_deep_decode() {
        // creator 创建的对象，其内部属性仍能正常解码填充
        String json = "{\"child\":{\"name\":\"deep\",\"createdBy\":\"should_be_overwritten\"}}";
        ParentModel parent = ONode.ofJson(json).toBean(ParentModel.class);

        System.out.println(parent.getChild().getName());
        System.out.println(parent.getChild().getCreatedBy());

        Assertions.assertEquals("deep", parent.getChild().getName());
        // createdBy 应该是 creator 设置的值（creator 先创建对象，后续字段解码会覆盖有 setter 的字段）
        Assertions.assertEquals("should_be_overwritten", parent.getChild().getCreatedBy());
    }

    // ============================================================
    // 内部数据类
    // ============================================================

    @Setter
    @Getter
    public static class ParentModel {
        @ONodeAttr(creator = FieldChildCreator.class)
        private ChildModel child;
    }

    @Setter
    @Getter
    public static class ParentModelNoCreator {
        private ChildModel child;
    }

    @Setter
    @Getter
    public static class ChildModel {
        private String name;
        private String createdBy;
    }

    @Setter
    @Getter
    public static class SimpleItem {
        private String text;
        private String createdBy;
    }

    @Getter
    public static class GetterModel {
        private SimpleItem item;

        @ONodeAttr(creator = GetterItemCreator.class)
        public SimpleItem getItem() {
            return item;
        }

        public void setItem(SimpleItem item) {
            this.item = item;
        }
    }

    public static class SetterModel {
        private SimpleItem item;

        public SimpleItem getItem() {
            return item;
        }

        @ONodeAttr(creator = SetterItemCreator.class)
        public void setItem(SimpleItem item) {
            this.item = item;
        }
    }

    @Setter
    @Getter
    public static class MultiFieldModel {
        @ONodeAttr(creator = CreatorA.class)
        private SimpleItem a;

        @ONodeAttr(creator = CreatorB.class)
        private SimpleItem b;
    }

    @Setter
    @Getter
    public static class CreatorAndDecoderModel {
        @ONodeAttr(creator = MyCreator.class)
        private DataItem data;
    }

    @Setter
    @Getter
    public static class DataItem {
        private String value;
        private String createdBy;
    }

    // ============================================================
    // 自定义 Creator 实现
    // ============================================================

    public static class FieldChildCreator implements ObjectCreator<ChildModel> {
        @Override
        public ChildModel create(Options opts, ONode node, Class<?> clazz) {
            ChildModel c = new ChildModel();
            c.setCreatedBy("FieldChildCreator");
            return c;
        }
    }

    public static class GetterItemCreator implements ObjectCreator<SimpleItem> {
        @Override
        public SimpleItem create(Options opts, ONode node, Class<?> clazz) {
            SimpleItem item = new SimpleItem();
            item.setCreatedBy("GetterItemCreator");
            return item;
        }
    }

    public static class SetterItemCreator implements ObjectCreator<SimpleItem> {
        @Override
        public SimpleItem create(Options opts, ONode node, Class<?> clazz) {
            SimpleItem item = new SimpleItem();
            item.setCreatedBy("SetterItemCreator");
            return item;
        }
    }

    public static class CreatorA implements ObjectCreator<SimpleItem> {
        @Override
        public SimpleItem create(Options opts, ONode node, Class<?> clazz) {
            SimpleItem item = new SimpleItem();
            item.setCreatedBy("CreatorA");
            return item;
        }
    }

    public static class CreatorB implements ObjectCreator<SimpleItem> {
        @Override
        public SimpleItem create(Options opts, ONode node, Class<?> clazz) {
            SimpleItem item = new SimpleItem();
            item.setCreatedBy("CreatorB");
            return item;
        }
    }

    public static class MyCreator implements ObjectCreator<DataItem> {
        @Override
        public DataItem create(Options opts, ONode node, Class<?> clazz) {
            DataItem item = new DataItem();
            item.setCreatedBy("MyCreator");
            return item;
        }
    }
}
