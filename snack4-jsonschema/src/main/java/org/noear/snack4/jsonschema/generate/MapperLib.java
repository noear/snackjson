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
package org.noear.snack4.jsonschema.generate;

import org.noear.eggg.TypeEggg;
import org.noear.snack4.jsonschema.generate.impl.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 定义库
 *
 * @author noear 2025/11/16 created
 * @since 4.0
 */
public class MapperLib {
    private static final MapperLib DEFAULT = new MapperLib(null).loadDefault();

    private final List<SchemaPatternMapper> schemaPatternMappers = new ArrayList<>();
    private final Map<Class<?>, SchemaMapper> schemaMapperMap = new HashMap<>();
    private final List<TypePatternMapper> typePatternMappers = new ArrayList<>();
    private final Map<Class<?>, TypeMapper> typeMapperMap = new HashMap<>();


    private final MapperLib parent;

    public MapperLib(MapperLib parent) {
        this.parent = parent;
    }

    public static MapperLib newInstance() {
        return new MapperLib(DEFAULT);
    }

    /**
     * 添加架构映射器
     */
    public <T> void addSchemaMapper(SchemaPatternMapper<T> generator) {
        schemaPatternMappers.add(generator);
    }

    /**
     * 添加架构映射器
     */
    public <T> void addSchemaMapper(Class<T> type, SchemaMapper<T> generator) {
        if (generator instanceof SchemaPatternMapper) {
            addSchemaMapper((SchemaPatternMapper<T>) generator);
        }

        schemaMapperMap.put(type, generator);
    }


    /**
     * 获取架构映射器
     */
    public SchemaMapper getSchemaMapper(TypeEggg typeEggg) {
        SchemaMapper tmp = schemaMapperMap.get(typeEggg.getType());

        if (tmp == null) {
            for (SchemaPatternMapper b1 : schemaPatternMappers) {
                if (b1.supports(typeEggg)) {
                    return b1;
                }
            }

            if (parent != null) {
                return parent.getSchemaMapper(typeEggg);
            }
        }

        return tmp;
    }

    /**
     * 添加类型映射
     */
    public <T> void addTypeMapper(TypePatternMapper<T> mapper) {
        typePatternMappers.add(mapper);
    }

    /**
     * 添加类型映射
     */
    public <T> void addTypeMapper(Class<T> type, TypeMapper<T> mapper) {
        if (mapper instanceof TypePatternMapper) {
            addTypeMapper((TypePatternMapper<T>) mapper);
        }

        typeMapperMap.put(type, mapper);
    }

    /**
     * 获取类型映射
     */
    public TypeMapper getTypeMapper(TypeEggg typeEggg) {
        if (typeEggg.isParameterizedType()) {
            TypeMapper tmp = typeMapperMap.get(typeEggg.getType());

            if (tmp == null) {
                for (TypePatternMapper b1 : typePatternMappers) {
                    if (b1.supports(typeEggg)) {
                        return b1;
                    }
                }

                if (parent != null) {
                    return parent.getTypeMapper(typeEggg);
                }
            }

            return tmp;
        }

        return null;
    }

    private MapperLib loadDefault() {
        schemaPatternMappers.add(new _DatePatternMapper());
        schemaPatternMappers.add(new _EnumPatternMapper());
        schemaPatternMappers.add(new _NumberPatternMapper());

        schemaMapperMap.put(Boolean.class, BooleanMapper.getInstance());
        schemaMapperMap.put(boolean.class, BooleanMapper.getInstance());
        schemaMapperMap.put(Character.class, CharMapper.getInstance());
        schemaMapperMap.put(char.class, CharMapper.getInstance());
        schemaMapperMap.put(Byte.class, ByteMapper.getInstance());
        schemaMapperMap.put(byte.class, ByteMapper.getInstance());
        schemaMapperMap.put(Byte[].class, ByteArrayMapper.getInstance());
        schemaMapperMap.put(byte[].class, ByteArrayMapper.getInstance());

        schemaMapperMap.put(String.class, new StringMapper());
        schemaMapperMap.put(URI.class, new URIMapper());

        schemaMapperMap.put(LocalDate.class, new LocalDateMapper());
        schemaMapperMap.put(LocalTime.class, new LocalTimeMapper());
        schemaMapperMap.put(LocalDateTime.class, new LocalDateTimeMapper());

        /// //////////

        typePatternMappers.add(new _FuturePatternMapper());

        typeMapperMap.put(Optional.class, new OptionalMapper());

        return this;
    }
}