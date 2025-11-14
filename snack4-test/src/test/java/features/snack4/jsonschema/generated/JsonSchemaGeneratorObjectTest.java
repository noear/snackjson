package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 对象类型测试
 */
@DisplayName("JsonSchemaGenerator 对象类型测试")
class JsonSchemaGeneratorObjectTest {

    static class TestPerson {
        private String name;
        private int age;
        private boolean active;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    @Test
    @DisplayName("生成简单对象模式")
    void testSimpleObject() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(TestPerson.class);
        ONode schema = generator.generate();

        assertEquals("object", schema.get("type").getString());
        assertTrue(schema.get("properties").isObject());

        ONode properties = schema.get("properties");
        assertEquals("string", properties.get("name").get("type").getString());
        assertEquals("integer", properties.get("age").get("type").getString());
        assertEquals("boolean", properties.get("active").get("type").getString());
    }

    static class Address {
        private String street;
        private String city;
        private List<String> phones;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public List<String> getPhones() { return phones; }
        public void setPhones(List<String> phones) { this.phones = phones; }
    }

    static class Company {
        private String name;
        private Map<String, Object> properties;
        private Address address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    @Test
    @DisplayName("生成复杂嵌套对象模式")
    void testComplexNestedObject() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Company.class)
                .withEnableDefinitions(true);
        ONode schema = generator.generate();

        assertEquals("object", schema.get("type").getString());

        ONode properties = schema.get("properties");
        assertEquals("string", properties.get("name").get("type").getString());
        assertEquals("object", properties.get("properties").get("type").getString());
        assertTrue(properties.get("properties").get("additionalProperties").getBoolean());

        // 检查地址属性
        ONode addressSchema = properties.get("address");
        assertTrue(addressSchema.hasKey("$ref") || addressSchema.get("type").getString().equals("object"));
    }
}