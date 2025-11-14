/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonschema.codec;

import org.noear.eggg.ClassEggg;
import org.noear.eggg.Property;
import org.noear.eggg.PropertyEggg;
import org.noear.eggg.TypeEggg;
import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.CodecException;
import org.noear.snack4.codec.util.EgggUtil;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.codec.encode.*;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * 对象编码器
 *
 * @author noear 2025/11/14 created
 * @since 4.0
 */
public class JsonSchemaBuilder {
    static final List<TypePatternSchemaBuilder> typePatternEncoders = new  ArrayList<>();
    static final Map<Class<?>, TypeSchemaBuilder> typeEncoders = new  HashMap<>();
    static {
        typePatternEncoders.add(new _DatePatternSchemaBuilder());
        typePatternEncoders.add(new _EnumPatternSchemaBuilder());
        typePatternEncoders.add(new _NumberPatternSchemaBuilder());

        typeEncoders.put(Boolean.class,  BooleanSchemaBuilder.getInstance());
        typeEncoders.put(boolean.class, BooleanSchemaBuilder.getInstance());
        typeEncoders.put(char.class,  new CharSchemaBuilder());

        typeEncoders.put(String.class, new StringSchemaBuilder());
        typeEncoders.put(URI.class, new URISchemaBuilder());
    }

    /**
     * Java Object 编码为 ONode
     */
    public static ONode encode(Type type) {
        if (type == null) {
            return new ONode(null);
        }

        if(type == void.class){
            throw new JsonSchemaException("Not support the void type");
        }

        return new JsonSchemaBuilder(EgggUtil.getTypeEggg(type)).encode();
    }

    private final TypeEggg source0;

    private final Map<Object, Object> visited;


    private JsonSchemaBuilder(TypeEggg typeEggg) {
        this.source0 = typeEggg;
        this.visited = new IdentityHashMap<>();
    }

    /**
     * Java Object 编码为 ONode
     */
    public ONode encode() {
        try {
            return encodeValueToNode(source0, null);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new CodecException("Failed to encode bean to ONode", e);
        }
    }

    private TypeSchemaBuilder getEncoder(TypeEggg typeEggg) {
        TypeSchemaBuilder tmp = typeEncoders.get(typeEggg.getType());

        if (tmp == null) {
            for (TypePatternSchemaBuilder b1 : typePatternEncoders) {
                if(b1.canEncode(typeEggg)){
                    return b1;
                }
            }
        }

        return tmp;
    }

    // 值转ONode处理
    private ONode encodeValueToNode(TypeEggg typeEggg, ONodeAttrHolder attr) throws Throwable {
        // 优先使用自定义编解码器
        TypeSchemaBuilder codec = getEncoder(typeEggg);
        if (codec != null) {
            return codec.encode(attr, typeEggg, new ONode());
        }

        if (typeEggg.isCollection()) {
            return encodeCollectionToNode(typeEggg);
        } else if (typeEggg.isMap()) {
            return encodeMapToNode(typeEggg);
        } else {
            if (typeEggg.isArray()) {
                return encodeArrayToNode(typeEggg);
            } else {
                return encodeBeanToNode(typeEggg);
            }
        }
    }

    // 对象转ONode核心逻辑
    private ONode encodeBeanToNode(TypeEggg typeEggg) throws Throwable {
        // 循环引用检测
        if (visited.containsKey(typeEggg)) {
            return null;
        } else {
            visited.put(typeEggg, null);
        }

        ONode target = new ONode().asObject();

        target.set(SchemaUtil.NAME_TYPE, SchemaUtil.TYPE_OBJECT);
        ONode oProperties = target.getOrNew(SchemaUtil.NAME_PROPERTIES).asObject();
        ONode oRequired = target.getOrNew(SchemaUtil.NAME_REQUIRED).asArray();

        try {
            ClassEggg classEggg = typeEggg.getClassEggg();

            for (PropertyEggg pw : classEggg.getPropertyEgggs()) {
                final Property property;
                if (pw.getGetterEggg() != null) {
                    property = pw.getGetterEggg();
                } else {
                    property = pw.getFieldEggg();
                }

                if (property == null) {
                    continue;
                }

                ONodeAttrHolder attr = property.getDigest();
                if (property.isTransient() || attr.isEncode() == false) {
                    continue;
                }

                ONode propertyNode = encodeValueToNode(property.getTypeEggg(), attr);

                if (propertyNode != null) {

                    if(Asserts.isNotEmpty(attr.getDescription())){
                        propertyNode.set(SchemaUtil.NAME_DESCRIPTION, attr.getDescription());
                    }

                    if (attr.isFlat()) {
                        if (propertyNode.isObject()) {
                            oProperties.setAll(propertyNode.getObject());

                            if (attr.isRequired()) {
                                oRequired.addAll(propertyNode.getObject().keySet());
                            }
                        }
                    } else {
                        oProperties.set(property.getAlias(), propertyNode);

                        if (attr.isRequired()) {
                            oRequired.add(property.getAlias());
                        }
                    }
                }
            }
        } finally {
            visited.remove(typeEggg);
        }

        return target;
    }

    // 处理数组类型
    private ONode encodeArrayToNode(TypeEggg typeEggg) throws Throwable {
        ONode tmp = new ONode();
        tmp.set("type", SchemaUtil.TYPE_ARRAY);

        ONode itemsType = encodeValueToNode(EgggUtil.getTypeEggg(typeEggg.getType().getComponentType()), null);
        tmp.set("items", itemsType);

        return tmp;
    }

    // 处理集合类型
    private ONode encodeCollectionToNode(TypeEggg typeEggg) throws Throwable {
        ONode tmp = new ONode();
        tmp.set("type", SchemaUtil.TYPE_ARRAY);

        if (typeEggg.isParameterizedType()) {
            ONode itemsType = encodeValueToNode(EgggUtil.getTypeEggg(typeEggg.getActualTypeArguments()[0]), null);
            tmp.set("items", itemsType);
        }

        return tmp;
    }

    // 处理Map类型
    private ONode encodeMapToNode(TypeEggg typeEggg) throws Throwable {
        ONode tmp = new ONode();
        tmp.set("type", SchemaUtil.TYPE_OBJECT);
        return tmp;
    }
}