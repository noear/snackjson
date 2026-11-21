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
package org.noear.snack4;

import org.noear.snack4.codec.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * JSON 处理选项（线程安全配置）
 *
 * @author noear 2019/2/12 created
 * @since 4.0
 */
public final class Options {
    //默认类型的key
    public static final String DEF_TYPE_PROPERTY_NAME = "@type";
    //默认时区
    public static final ZoneId DEF_ZONE = ZoneId.systemDefault();
    public static final TimeZone DEF_TIME_ZONE = TimeZone.getDefault();
    //默认偏移时区
    public static final ZoneOffset DEF_OFFSET = OffsetDateTime.now().getOffset();
    //默认地区
    public static final Locale DEF_LOCALE = Locale.getDefault();
    //默认时间格式器
    public static final String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //默认特性
    public static final int DEF_FEATURES = 0;

    //默认选项（私有）
    public static final Options DEF_OPTIONS = new Options(true);
    public static final String DEF_UNSUPPORTED_HINT = "Read-only mode does not support modification.";

    //编码仓库
    private final CodecLib codecLib;
    //特性开关（使用位掩码存储）
    private long featuresValue = DEF_FEATURES;
    //时间格式
    private String dateFormat = DEF_DATETIME_FORMAT;
    //书写缩进
    private String writeIndent = "  ";
    //类型属性名
    private String typePropertyName = DEF_TYPE_PROPERTY_NAME;
    //类加载器
    private ClassLoader classLoader;
    //允许安全类
    private Locale locale = DEF_LOCALE;
    //时区
    private ZoneId zoneId = DEF_ZONE;
    private TimeZone timeZone = DEF_TIME_ZONE;

    private Supplier<Map> mapFactory = LinkedHashMap::new;
    private Supplier<List> listFactory = ArrayList::new;

    //JSON 嵌套深度上限
    private int maxNestingDepth = 1000;


    private boolean readonly;

    //排除的写入特性（仅派生选项非零；用于属性级 excludeFeatures）
    private final long writeExcludes;
    //派生选项缓存（按排除掩码；用到时才创建）
    private volatile Map<Long, Options> derivedCache;

    private Options(boolean readonly) {
        this.readonly = readonly;
        this.codecLib = CodecLib.newInstance();
        this.writeExcludes = 0L;
    }

    /**
     * 派生构造（共享编解码仓库，只读）
     */
    private Options(Options base, long excludes) {
        this.readonly = true;
        this.codecLib = base.codecLib;
        this.writeExcludes = base.writeExcludes | excludes;

        copyFieldsFrom(base);
        this.featuresValue &= ~excludes;
    }

    /**
     * 复制基础属性（不含编解码仓库与只读状态）
     */
    private void copyFieldsFrom(Options src) {
        this.featuresValue = src.featuresValue;
        this.dateFormat = src.dateFormat;
        this.writeIndent = src.writeIndent;
        this.typePropertyName = src.typePropertyName;
        this.classLoader = src.classLoader;
        this.locale = src.locale;
        this.zoneId = src.zoneId;
        this.timeZone = src.timeZone;
        this.mapFactory = src.mapFactory;
        this.listFactory = src.listFactory;
        this.maxNestingDepth = src.maxNestingDepth;
    }

    /**
     * 确认可写（并清理派生缓存，避免过期）
     */
    private void assertWritable() {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        //仅在有缓存时清理（避免常规路径上的 volatile 写）
        if (derivedCache != null) {
            derivedCache = null;
        }
    }

    /**
     * 获取排除的写入特性掩码（内部使用）
     */
    public long getWriteExcludes() {
        return writeExcludes;
    }

    /**
     * 派生一个排除了指定写入特性的只读选项（内部使用；共享编解码仓库，按掩码缓存）
     *
     * @param excludes 排除的特性掩码
     */
    public Options deriveWithout(long excludes) {
        if ((excludes & ~writeExcludes) == 0L) {
            //无新增排除
            return this;
        }

        Map<Long, Options> cache = derivedCache;
        if (cache == null) {
            //并发下最多多建一份，无害
            derivedCache = cache = new ConcurrentHashMap<>();
        }

        Options derived = cache.get(excludes);
        if (derived == null) {
            derived = cache.computeIfAbsent(excludes, k -> new Options(this, k));
        }

        return derived;
    }

    /**
     * 转为只读
     */
    public Options readonly() {
        this.readonly = true;
        return this;
    }

    /**
     * 克隆一个新的选项（非只读）
     */
    public Options copy() {
        Options tmp = new Options(false); // 内部执行了 codecLib = CodecLib.newInstance()

        // 复制基础属性
        tmp.copyFieldsFrom(this);

        // 将旧 options 的特有编解码器填充到新 options 的 codecLib 中
        tmp.codecLib.fill(this.codecLib);

        return tmp;
    }

    /**
     * 用于链式构建
     */
    public Options then(Consumer<Options> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * 加载类
     */
    public Class<?> loadClass(String className) throws SnackException {
        return loadClass(className, true);
    }

    /**
     * 加载类
     */
    public Class<?> loadClass(String className, boolean throwOnError) {
        try {
            ClassLoader loader = classLoader;
            if (loader == null) {
                loader = Thread.currentThread().getContextClassLoader();
            }

            if (loader != null) {
                // 统一不触发静态初始化，行为一致
                return Class.forName(className, false, loader);
            } else {
                return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            if (throwOnError) {
                throw new SnackException("Failed to load class: " + className, e);
            } else {
                return null;
            }
        }
    }

    /**
     * 是否启用指定特性
     */
    public boolean hasFeature(Feature feature) {
        return Feature.hasFeature(this.featuresValue, feature);
    }

    public long getFeatures() {
        return featuresValue;
    }

    public Locale getLocale() {
        return locale;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * @deprecated 4.0
     *
     */
    @Deprecated
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 获取日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    public String getTypePropertyName() {
        return typePropertyName;
    }

    /**
     * 获取解码器
     */
    public ObjectDecoder<?> getDecoder(Class<?> clazz) {
        return codecLib.getDecoder(clazz);
    }

    /**
     * 获取编码器
     */
    public ObjectEncoder<?> getEncoder(Object value) {
        return codecLib.getEncoder(value);
    }

    /**
     * 获取创建器
     */
    public ObjectCreator<?> getCreator(Class<?> clazz) {
        return codecLib.getCreator(clazz);
    }

    /**
     * 获取缩进字符串
     */
    public String getWriteIndent() {
        return writeIndent;
    }

    /**
     * 获取 JSON 嵌套深度上限
     */
    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    public <T> Map<String, T> createMap() {
        return mapFactory.get();
    }

    public <T> List<T> createList() {
        return listFactory.get();
    }


    /// /////////////

    /**
     * 设置 JSON 嵌套深度上限
     */
    public Options maxNestingDepth(int maxNestingDepth) {
        assertWritable();

        this.maxNestingDepth = maxNestingDepth;
        return this;
    }

    /**
     * 设置日期格式
     */
    public Options dateFormat(String format) {
        assertWritable();

        this.dateFormat = format;
        return this;
    }

    /**
     * 设置地区
     */
    public Options locale(Locale locale) {
        assertWritable();

        this.locale = locale;
        return this;
    }

    /**
     * 设置时区
     */
    public Options timeZone(TimeZone timeZone) {
        assertWritable();

        this.zoneId = timeZone.toZoneId();
        this.timeZone = timeZone;
        return this;
    }

    /**
     * 设置时区
     */
    public Options timeZone(ZoneId zoneId) {
        assertWritable();

        this.zoneId = zoneId;
        this.timeZone = TimeZone.getTimeZone(zoneId);
        return this;
    }

    /**
     * 设置缩进字符串
     */
    public Options writeIndent(String indent) {
        assertWritable();

        this.writeIndent = indent;
        return this;
    }

    /**
     * 设置类加载器
     */
    public Options classLoader(ClassLoader classLoader) {
        assertWritable();

        this.classLoader = classLoader;
        return this;
    }

    /**
     * 添加特性
     */
    public Options addFeatures(Feature... features) {
        assertWritable();

        this.featuresValue = Feature.addFeatures(this.featuresValue, features);
        return this;
    }

    public Options setFeatures(Feature... features) {
        assertWritable();

        this.featuresValue = Feature.addFeatures(0L, features);
        return this;
    }

    /**
     * 移除特性
     */
    public Options removeFeatures(Feature... features) {
        assertWritable();

        this.featuresValue = Feature.removeFeatures(this.featuresValue, features);
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        assertWritable();

        codecLib.addDecoder(type, decoder);
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(ObjectPatternDecoder<T> decoder) {
        assertWritable();

        codecLib.addDecoder(decoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        assertWritable();

        codecLib.addEncoder(type, encoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(ObjectPatternEncoder<T> encoder) {
        assertWritable();

        codecLib.addEncoder(encoder);
        return this;
    }

    /**
     * 注册自定义创建器
     */
    public <T> Options addCreator(Class<T> type, ObjectCreator<T> creator) {
        assertWritable();

        codecLib.addCreator(type, creator);
        return this;
    }

    /**
     * 注册自定义创建器
     */
    public <T> Options addCreator(ObjectPatternCreator<T> creator) {
        assertWritable();

        codecLib.addCreator(creator);
        return this;
    }

    //////////////////
    // 类型安全检测（双层配置：全局 + 实例）
    //////////////////

    /**
     * 注册自定义检测器（类型安全检测器）
     */
    public Options addChecker(TypeChecker checker) {
        assertWritable();

        if (checker != null) {
            codecLib.addChecker(checker);
        }
        return this;
    }

    /**
     * 判断类名是否应被拦截（供解码器在动态类加载前调用）
     *
     * @param className 完整类名
     * @return true 表示拦截（拒绝加载），false 表示放行
     */
    public boolean isTypeBlocked(String className) {
        // 协议注入防护（含 ":" 或 "!" 的类名一律拒绝，不可关闭）
        if (className == null || className.contains(":") || className.contains("!")) {
            return true;
        }

        return codecLib.isTypeBlocked(className);
    }

    public Options mapFactory(Supplier<Map> mapFactory) {
        assertWritable();

        this.mapFactory = mapFactory;
        return this;
    }

    public Options listFactory(Supplier<List> listFactory) {
        assertWritable();

        this.listFactory = listFactory;
        return this;
    }

    public static Options of(Feature... features) {
        Options tmp = new Options(false);
        for (Feature f : features) {
            tmp.addFeatures(f);
        }
        return tmp;
    }
}