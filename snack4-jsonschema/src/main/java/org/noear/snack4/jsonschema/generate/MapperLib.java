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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义库
 *
 * @author noear 2025/11/16 created
 * @since 4.0
 */
public class MapperLib {
    private static final MapperLib DEFAULT = new MapperLib(null).loadDefault();

    private final List<SchemaPatternMapper> TYPE_PATTERN_GENERATORS = new ArrayList<>();
    private final Map<Class<?>, SchemaMapper> TYPE_GENERATOR_MAP = new ConcurrentHashMap<>();

    private final MapperLib parent;

    public MapperLib(MapperLib parent) {
        this.parent = parent;
    }

    public static MapperLib newInstance() {
        return new MapperLib(DEFAULT);
    }

    /**
     * 添加生成器
     */
    public <T> void addMapper(SchemaPatternMapper<T> generator) {
        TYPE_PATTERN_GENERATORS.add(generator);
    }

    /**
     * 添加生成器
     */
    public <T> void addMapper(Class<T> type, SchemaMapper<T> generator) {
        if (generator instanceof SchemaPatternMapper) {
            addMapper((SchemaPatternMapper<T>) generator);
        }

        TYPE_GENERATOR_MAP.put(type, generator);
    }


    /**
     * 获取生成器
     */
    public SchemaMapper getMapper(TypeEggg typeEggg) {
        SchemaMapper tmp = TYPE_GENERATOR_MAP.get(typeEggg.getType());

        if (tmp == null) {
            for (SchemaPatternMapper b1 : TYPE_PATTERN_GENERATORS) {
                if (b1.supports(typeEggg)) {
                    return b1;
                }
            }

            if (parent != null) {
                return parent.getMapper(typeEggg);
            }
        }

        return tmp;
    }

    private MapperLib loadDefault() {
        TYPE_PATTERN_GENERATORS.add(new _DatePatternMapper());
        TYPE_PATTERN_GENERATORS.add(new _EnumPatternMapper());
        TYPE_PATTERN_GENERATORS.add(new _NumberPatternMapper());

        TYPE_GENERATOR_MAP.put(Boolean.class, BooleanMapper.getInstance());
        TYPE_GENERATOR_MAP.put(boolean.class, BooleanMapper.getInstance());
        TYPE_GENERATOR_MAP.put(Character.class, CharMapper.getInstance());
        TYPE_GENERATOR_MAP.put(char.class, CharMapper.getInstance());
        TYPE_GENERATOR_MAP.put(Byte.class, ByteMapper.getInstance());
        TYPE_GENERATOR_MAP.put(byte.class, ByteMapper.getInstance());
        TYPE_GENERATOR_MAP.put(Byte[].class, ByteArrayMapper.getInstance());
        TYPE_GENERATOR_MAP.put(byte[].class, ByteArrayMapper.getInstance());

        TYPE_GENERATOR_MAP.put(String.class, new StringMapper());
        TYPE_GENERATOR_MAP.put(URI.class, new URIMapper());

        TYPE_GENERATOR_MAP.put(LocalDate.class, new LocalDateMapper());
        TYPE_GENERATOR_MAP.put(LocalTime.class, new LocalTimeMapper());
        TYPE_GENERATOR_MAP.put(LocalDateTime.class, new LocalDateTimeMapper());

        return this;
    }
}