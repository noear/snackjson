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
package org.noear.snack4.annotation;

import org.noear.eggg.AnnotatedEggg;
import org.noear.snack4.Feature;
import org.noear.snack4.codec.ObjectCreator;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.ClassUtil;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * 节点属性元
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class ONodeAttrHolder {
    private static final Logger LOG = LoggerFactory.getLogger(ONodeAttrHolder.class);

    /**
     * 可排除的写入特性（值格式类）
     */
    private static final long EXCLUDABLE_FEATURES = Feature.addFeatures(0L,
            Feature.Write_NumbersAsString,
            Feature.Write_LongAsString,
            Feature.Write_DoubleAsString,
            Feature.Write_BigDecimalAsPlain,
            Feature.Write_NumberTypeSuffix,
            Feature.Write_BooleanAsNumber,
            Feature.Write_UseDateFormat);

    private String alias;
    private String title;
    private String description;
    private boolean required;
    private String defaultValue; //@since 4.0.35

    private String format;
    private ZoneId zoneId;
    private TimeZone timezone;

    private boolean flat;
    private final boolean empty;

    private boolean encode = true;
    private boolean decode = true;
    private ObjectEncoder encoder;
    private ObjectDecoder decoder;
    private ObjectCreator creator;
    private long featuresValue;
    private long excludeFeaturesValue; //@since 4.0.60
    private AnnotatedEggg eggg;

    @Deprecated
    public ONodeAttrHolder(String alias, String title, String description, boolean required) {
        this(alias, title, description, required, null, null);
    }

    /**
     * @deprecated 4.0.35
     */
    @Deprecated
    public ONodeAttrHolder(String alias, String title, String description, boolean required, AnnotatedEggg eggg) {
        this(alias, title, description, required, null, eggg);
    }

    /**
     * @deprecated 4.0.42
     */
    @Deprecated
    public ONodeAttrHolder(String alias, String title, String description, boolean required, String defaultValue, AnnotatedEggg eggg) {
        this(alias, title, description, required, null, null, eggg);
    }

    public ONodeAttrHolder(String alias, String title, String description, boolean required, String defaultValue, String format, AnnotatedEggg eggg) {
        this.eggg = eggg;

        this.alias = alias;
        this.title = title;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.format = format;
        this.empty = true;
    }

    @Deprecated
    public ONodeAttrHolder(ONodeAttr attrAnno, String realName) {
        this(attrAnno, realName, null);
    }

    public ONodeAttrHolder(ONodeAttr attrAnno, String realName, AnnotatedEggg eggg) {
        this.eggg = eggg;

        if (attrAnno != null) {
            alias = attrAnno.name();
            title = attrAnno.title();
            description = attrAnno.description();
            required = attrAnno.required();
            defaultValue = attrAnno.defaultValue();

            format = attrAnno.format();
            if (Asserts.isNotEmpty(attrAnno.timezone())) {
                zoneId = ZoneId.of(attrAnno.timezone());
                timezone = TimeZone.getTimeZone(zoneId);
            }

            flat = attrAnno.flat();


            if (attrAnno.ignore()) {
                encode = false;
                decode = false;
            } else {
                encode = attrAnno.encode();
                decode = attrAnno.decode();
            }

            if (attrAnno.encoder().isInterface() == false) {
                encoder = ClassUtil.newInstance(attrAnno.encoder());
            }

            if (attrAnno.decoder().isInterface() == false) {
                decoder = ClassUtil.newInstance(attrAnno.decoder());
            }

            if (attrAnno.creator().isInterface() == false) {
                creator = ClassUtil.newInstance(attrAnno.creator());
            }

            featuresValue = Feature.addFeatures(0L, attrAnno.features());
            excludeFeaturesValue = resolveExcludeFeatures(attrAnno, realName, featuresValue);

            //为空
            empty = false;
        } else {
            //非空（即没有注解）
            empty = true;
        }

        if (Asserts.isEmpty(alias)) {
            alias = realName;
        }
    }

    public String getAlias() {
        return alias;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getFormat() {
        return format;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * @deprecated 4.0
     */
    @Deprecated
    public TimeZone getTimezone() {
        return timezone;
    }

    public String formatDate(Date value) {
        if (getZoneId() != null) {
            return DateUtil.format(value, getFormat(), getZoneId());
        } else {
            return DateUtil.format(value, getFormat());
        }
    }

    public boolean isFlat() {
        return flat;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean hasFeature(Feature feature) {
        return Feature.hasFeature(featuresValue, feature);
    }

    /**
     * 获取排除的全局写入特性掩码
     *
     * @since 4.0.60
     */
    public long getExcludeFeatures() {
        return excludeFeaturesValue;
    }

    private static long resolveExcludeFeatures(ONodeAttr attrAnno, String realName, long featuresValue) {
        long excludes = 0L;

        for (Feature f : attrAnno.excludeFeatures()) {
            if ((EXCLUDABLE_FEATURES & f.mask()) == 0L) {
                //不支持的特性：忽略该项（排除无效）
                LOG.warn("The feature is not supported by @ONodeAttr.excludeFeatures and will be ignored: {} ({})", f, realName);
                continue;
            }

            if ((featuresValue & f.mask()) != 0L) {
                //与 features 冲突：忽略该项（以 features 开启为准）
                LOG.warn("The feature is in both @ONodeAttr.features and excludeFeatures, excludeFeatures will be ignored: {} ({})", f, realName);
                continue;
            }

            excludes |= f.mask();
        }

        return excludes;
    }

    public boolean isEncode() {
        return encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public ObjectEncoder getEncoder() {
        return encoder;
    }

    public ObjectDecoder getDecoder() {
        return decoder;
    }

    public ObjectCreator getCreator() {
        return creator;
    }

    public AnnotatedEggg getEggg() {
        return eggg;
    }
}