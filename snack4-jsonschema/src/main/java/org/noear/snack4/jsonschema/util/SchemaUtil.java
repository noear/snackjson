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
package org.noear.snack4.jsonschema.util;

import org.noear.eggg.*;
import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.util.EgggUtil;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 架构工具
 *
 * @author noear
 * @since 4.0
 */
public class SchemaUtil {
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_ARRAY = "array";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_NULL = "null";

    private static String nameOr(String name1, String name2) {
        if(Asserts.isEmpty(name1)) {
            return name2;
        } else{
            return name1;
        }
    }

    /**
     * 属性申明
     * */
    public static PropertyDesc propertyOf(AnnotatedElement ae, TypeEggg typeEggg) {
        ONodeAttr p1Anno = ae.getAnnotation(ONodeAttr.class);

        if (p1Anno == null) {
            if (ae instanceof Parameter) {
                Parameter p1 = (Parameter) ae;
                String name = p1.getName();
                return new PropertyDesc(name, typeEggg.getGenericType(), false, "");
            } else {
                Field p1 = (Field) ae;
                String name = p1.getName();
                return new PropertyDesc(name, typeEggg.getGenericType(), false, "");
            }
        } else {
            if (ae instanceof Parameter) {
                Parameter p1 = (Parameter) ae;
                String name = nameOr(p1Anno.name(), p1.getName());
                return new PropertyDesc(name, typeEggg.getGenericType(), p1Anno.required(), p1Anno.description());
            } else {
                Field p1 = (Field) ae;
                String name = nameOr(p1Anno.name(), p1.getName());
                return new PropertyDesc(name, typeEggg.getGenericType(), p1Anno.required(), p1Anno.description());
            }
        }
    }

    /**
     * 构建工具输入架构
     *
     * @param toolParams       工具参数
     */
    public static String buildInputSchema(List<PropertyDesc> toolParams) {
        return buildToolParametersNode(toolParams, new ONode()).toJson();
    }

    /**
     * 构建类型的架构节点
     * */
    public static String buildOutputSchema(Type type) {
        return buildTypeSchemaNode(type, "", new ONode()).toJson();
    }

    /**
     * 构建工具参数节点
     *
     * @param toolParams       工具参数
     * @param schemaParentNode 架构父节点（待构建）
     */
    public static ONode buildToolParametersNode(List<PropertyDesc> toolParams, ONode schemaParentNode) {
        schemaParentNode.asObject();

        ONode requiredNode = new ONode(schemaParentNode.options()).asArray();

        schemaParentNode.set("type", TYPE_OBJECT);
        schemaParentNode.getOrNew("properties").then(propertiesNode -> {
            propertiesNode.asObject();

            for (PropertyDesc fp : toolParams) {
                propertiesNode.getOrNew(fp.name()).then(paramNode -> {
                    buildTypeSchemaNode(fp.type(), fp.description(), paramNode);
                });

                if (fp.required()) {
                    requiredNode.add(fp.name());
                }
            }
        });

        schemaParentNode.set("required", requiredNode);

        return schemaParentNode;
    }


    /**
     * 构建类型的架构节点
     *
     * @since 3.1
     * @since 3.3
     * @since 3.5
     */
    public static ONode buildTypeSchemaNode(Type type, String description, ONode schemaNode) {
        handleType(EgggUtil.getTypeEggg(type), description, schemaNode);

        if (Asserts.isNotEmpty(description)) {
            schemaNode.set("description", description);
        }

        return schemaNode;
    }

    /**
     * 乎略输出架构
     * */
    public static boolean isIgnoreOutputSchema(Type type) {
        if (type == void.class) {
            return true;
        } else if (type == String.class) {
            return true;
        } else if (type == Boolean.class) {
            return true;
        } else if (type instanceof Class) {
            Class clz = ((Class) type);

            if (Number.class.isAssignableFrom(clz)) {
                return true;
            } else if (Date.class.isAssignableFrom(clz)) {
                return true;
            }

            return clz.isPrimitive() || clz.isEnum();
        }

        return false;
    }


    /**
     * 处理普通 Class 类型：数组、枚举、POJO 等
     */
    private static void handleType(TypeEggg typeEggg, String description, ONode schemaNode) {

        // Array
        if (typeEggg.isArray()) {
            schemaNode.set("type", TYPE_ARRAY);
            buildTypeSchemaNode(typeEggg.getType().getComponentType(), null, schemaNode.getOrNew("items"));
            return;
        }

        // Collection
        if (Collection.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_ARRAY);

            if(typeEggg.isParameterizedType()){
                Type[] actualTypeArguments = typeEggg.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    buildTypeSchemaNode(actualTypeArguments[0], null, schemaNode.getOrNew("items"));
                }
            }
            return;
        }

        // Map
        if (Map.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_OBJECT);
            return;
        }

        // Enum
        if (typeEggg.isEnum()) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.getOrNew("enum").then(n -> {
                for (Object e : typeEggg.getType().getEnumConstants()) {
                    n.add(e.toString());
                }
            });
            return;
        }

        //Optional
        if (Optional.class == typeEggg.getType() && typeEggg.isParameterizedType()) {
            buildTypeSchemaNode(typeEggg.getActualTypeArguments()[0], description, schemaNode);
            return;
        }

        // Date
        if (Date.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.set("format", "date-time");
            return;
        }

        if (LocalDateTime.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.set("format", "date-time");
            return;
        }

        if (LocalDate.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.set("format", "date");
            return;
        }

        if (LocalTime.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.set("format", "time");
            return;
        }

        // Uri
        if (URI.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_STRING);
            schemaNode.set("format", "uri");
            return;
        }

        // 特殊类型处理: 大整型、大数字
        if(BigInteger.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_INTEGER);
            return;
        }

        if(BigDecimal.class.isAssignableFrom(typeEggg.getType())) {
            schemaNode.set("type", TYPE_NUMBER);
            return;
        }

        if(Void.class == typeEggg.getType() || void.class == typeEggg.getType()) {
            schemaNode.set("type", TYPE_NULL);
            return;
        }

        if(Object.class == typeEggg.getType()){
            schemaNode.set("type", TYPE_OBJECT);
        }


        // 处理普通对象类型（POJO）
        handleBeanType(typeEggg, schemaNode);
    }

    /**
     * 处理 POJO 类型（含字段映射）
     *
     * @since 3.3
     */
    private static void handleBeanType(TypeEggg typeEggg, ONode schemaNode) {
        String typeStr = jsonTypeOfJavaType(typeEggg.getType());
        schemaNode.set("type", typeStr);

        if (!TYPE_OBJECT.equals(typeStr)) {
            return;
        }

        ONode requiredNode = new ONode(schemaNode.options()).asArray();

        schemaNode.getOrNew("properties").then(propertiesNode -> {
            propertiesNode.asObject();

            for (FieldEggg fw : typeEggg.getClassEggg().getAllFieldEgggs()) {
                if(fw.isStatic() || fw.isTransient()){
                    continue;
                }

                PropertyDesc fp = propertyOf(fw.getField(), fw.getTypeEggg());

                if (fp != null) {
                    propertiesNode.getOrNew(fp.name()).then(paramNode -> {
                        buildTypeSchemaNode(fp.type(), fp.description(), paramNode);
                    });

                    if (fp.required()) {
                        requiredNode.add(fp.name());
                    }
                }
            }
        });

        schemaNode.set("required", requiredNode);
    }

    /**
     * json 类型转换
     */
    public static String jsonTypeOfJavaType(Class<?> type) {
        if (type.equals(String.class) || type.equals(Date.class) || type.equals(BigDecimal.class) || type.equals(BigInteger.class)) {
            return TYPE_STRING;
        } else if (type.equals(Byte.class) || type.equals(byte.class) || type.equals(char.class) ||
                type.equals(Short.class) || type.equals(short.class) ||
                type.equals(Integer.class) || type.equals(int.class) || type.equals(Long.class) || type.equals(long.class)) {
            return TYPE_INTEGER;
        } else if (type.equals(Double.class) || type.equals(double.class) || type.equals(Float.class) || type.equals(float.class) || type.equals(Number.class)) {
            return TYPE_NUMBER;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return TYPE_BOOLEAN;
        } else {
            return TYPE_OBJECT;
        }
    }

    /// ////

    /**
     * 主入口方法：构建 Schema 节点（递归处理）
     *
     * @since 3.1
     * @since 3.3
     * @deprecated 3.5
     */
    @Deprecated
    public static void buildToolParamNode(Type type, String description, ONode schemaNode) {
        buildTypeSchemaNode(type, description, schemaNode);
    }

    public static String getSchemaTypeName(ONode node) {
        switch (node.type()) {
            case Undefined:
                return "undefined";
            case Null:
                return "null";
            case Boolean:
                return "boolean";
            case Number:
                if(node.getValue() instanceof Float || node.getValue() instanceof Double || node.getValue() instanceof BigDecimal) {
                    return "number";
                } else  {
                    return "integer";
                }
            case String:
                return "string";
            case Date:
                return "date";
            case Array:
                return "array";
            case Object:
                return "object";
            default:
                return "unknown";
        }
    }

}