package com.teoresi.staff.libs.data.models;

import com.teoresi.staff.libs.data.components.SpecificationFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.Optional;

public class SimpleFilter<T> implements Filter<T> {

    protected String field;
    protected SimpleFilterOperator operator;
    protected String value;

    public Specification<T> toSpecification(SpecificationFactory<T> specificationFactory) {
        return specificationFactory.createSpecification(this.field, this.operator, this.value);
    }

    @Override
    public boolean containsField(String field) {
        return this.field.equals(field);
    }

    SimpleFilter(final String field, final SimpleFilterOperator operator, final String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public static <T> SimpleFilterBuilder<T> builder() {
        return new SimpleFilterBuilder();
    }

    public String getField() {
        return this.field;
    }

    public SimpleFilterOperator getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public void setField(final String field) {
        this.field = field;
    }

    public void setOperator(final SimpleFilterOperator operator) {
        this.operator = operator;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SimpleFilter)) {
            return false;
        } else {
            SimpleFilter<?> other = (SimpleFilter)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$field = this.getField();
                    Object other$field = other.getField();
                    if (this$field == null) {
                        if (other$field == null) {
                            break label47;
                        }
                    } else if (this$field.equals(other$field)) {
                        break label47;
                    }

                    return false;
                }

                Object this$operator = this.getOperator();
                Object other$operator = other.getOperator();
                if (this$operator == null) {
                    if (other$operator != null) {
                        return false;
                    }
                } else if (!this$operator.equals(other$operator)) {
                    return false;
                }

                Object this$value = this.getValue();
                Object other$value = other.getValue();
                if (this$value == null) {
                    if (other$value != null) {
                        return false;
                    }
                } else if (!this$value.equals(other$value)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SimpleFilter;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $field = this.getField();
        result = result * 59 + ($field == null ? 43 : $field.hashCode());
        Object $operator = this.getOperator();
        result = result * 59 + ($operator == null ? 43 : $operator.hashCode());
        Object $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getField();
        return "SimpleFilter(field=" + var10000 + ", operator=" + this.getOperator() + ", value=" + this.getValue() + ")";
    }

    public static class SimpleFilterBuilder<T> {
        private String field;
        private SimpleFilterOperator operator;
        private String value;

        SimpleFilterBuilder() {
        }

        public SimpleFilterBuilder<T> field(final String field) {
            this.field = field;
            return this;
        }

        public SimpleFilterBuilder<T> operator(final SimpleFilterOperator operator) {
            this.operator = operator;
            return this;
        }

        public SimpleFilter.SimpleFilterBuilder<T> value(final String value) {
            this.value = value;
            return this;
        }

        public SimpleFilter<T> build() {
            return new SimpleFilter(this.field, this.operator, this.value);
        }

        public String toString() {
            return "SimpleFilter.SimpleFilterBuilder(field=" + this.field + ", operator=" + this.operator + ", value=" + this.value + ")";
        }
    }
}
