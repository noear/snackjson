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
package org.noear.snack4.codec;

import org.noear.eggg.TypeEggg;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.EgggUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 *
 * @author noear 2025/10/7 created
 * @since 4.0
 */
public class EncodeContext {
    private final Options options;
    private final ONodeAttrHolder attr;
    private final AnnotatedElement element;

    public EncodeContext(Options options, ONodeAttrHolder attr, Object value) {
        this.options = options;

        if (attr == null || attr.isEmpty()) {
            TypeEggg typeEggg = EgggUtil.getTypeEggg(value.getClass());
            if (typeEggg.isJdkType() == false) {
                attr = typeEggg.getClassEggg().getDigest();
            }
        }

        this.attr = attr;

        if (attr == null || attr.getEggg() == null) {
            this.element = null;
        } else {
            this.element = attr.getEggg().getElement();
        }
    }

    public Options getOptions() {
        return options;
    }

    public ONodeAttrHolder getAttr() {
        return attr;
    }

    public AnnotatedElement getElement() {
        return element;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        if (element == null) {
            return null;
        } else {
            return element.getAnnotation(annotationClass);
        }
    }

    public boolean hasFeature(Feature feature) {
        if (attr != null && attr.isEmpty() == false) {
            if (attr.hasFeature(feature)) {
                return true;
            }
        }

        return options.hasFeature(feature);
    }
}
