/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import org.antlr.v4.runtime.Token;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class FieldAccessExpression extends BaseExpression {
    private static final Logger log = LoggerFactory.getLogger(FieldAccessExpression.class);

    private final Expression object;
    private final Expression field;

    public FieldAccessExpression(Token start, Expression object, Expression field) {
        super(start);
        this.object = object;
        this.field = field;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final Object bean = this.object.evaluateUnsafe(context);
        final Object fieldValue = field.evaluateUnsafe(context);
        if (bean == null || fieldValue == null) {
            return null;
        }
        final String fieldName = fieldValue.toString();

        // First try to access the field using the given field name
        final Object property = getProperty(bean, fieldName);
        if (property == null) {
            // If the given field name does not work, try to convert it to camel case to make JSON-like access
            // to fields possible. Example: "geo.location.metro_code" => "geo.getLocation().getMetroCode()"
            return getProperty(bean, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName));
        }
        return property;
    }

    private Object getProperty(Object bean, String fieldName) {
        try {
            Object property = PropertyUtils.getProperty(bean, fieldName);
            if (property == null) {
                // in case the bean is a Map, try again with a simple property, it might be masked by the Map
                property = PropertyUtils.getSimpleProperty(bean, fieldName);
            }
            log.debug("[field access] property {} of bean {}: {}", fieldName, bean.getClass().getTypeName(), property);
            return property;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.debug("Unable to read property {} from {}", fieldName, bean);
            return null;
        }
    }

    @Override
    public Class getType() {
        return Object.class;
    }

    @Override
    public String toString() {
        return object.toString() + "." + field.toString();
    }

    public Expression object() {
        return object;
    }

    public Expression field() {
        return field;
    }

    @Override
    public Iterable<Expression> children() {
        return ImmutableList.of(object, field);
    }
}
