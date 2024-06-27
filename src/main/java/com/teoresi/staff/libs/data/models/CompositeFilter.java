package com.teoresi.staff.libs.data.models;

import com.teoresi.staff.libs.data.components.SpecificationFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Setter
@Getter
public class CompositeFilter<T> implements Filter<T> {
    protected Filter<T> operandOne;
    protected CompositeFilterOperator operator;
    protected Filter<T> operandTwo;

    public Specification<T> toSpecification(SpecificationFactory<T> specificationFactory) {
        return this.operator == CompositeFilterOperator.AND ? Specification.where(this.operandOne.toSpecification(specificationFactory)).and(this.operandTwo.toSpecification(specificationFactory)) : Specification.where(this.operandOne.toSpecification(specificationFactory)).or(this.operandTwo.toSpecification(specificationFactory));
    }

    CompositeFilter(final Filter<T> operandOne, final CompositeFilterOperator operator, final Filter<T> operandTwo) {
        this.operandOne = operandOne;
        this.operator = operator;
        this.operandTwo = operandTwo;
    }

    public boolean containsField (String field) {
        return (operandOne.containsField(field) || operandTwo.containsField(field));
    }

    public static <T> CompositeFilterBuilder<T> builder() {
        return new CompositeFilter.CompositeFilterBuilder<>();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CompositeFilter)) {
            return false;
        } else {
            CompositeFilter<?> other = (CompositeFilter)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$operandOne = this.getOperandOne();
                    Object other$operandOne = other.getOperandOne();
                    if (this$operandOne == null) {
                        if (other$operandOne == null) {
                            break label47;
                        }
                    } else if (this$operandOne.equals(other$operandOne)) {
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

                Object this$operandTwo = this.getOperandTwo();
                Object other$operandTwo = other.getOperandTwo();
                if (this$operandTwo == null) {
                    return other$operandTwo == null;
                } else return this$operandTwo.equals(other$operandTwo);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CompositeFilter;
    }

    public int hashCode() {
        int result = 1;
        Object $operandOne = this.getOperandOne();
        result = result * 59 + ($operandOne == null ? 43 : $operandOne.hashCode());
        Object $operator = this.getOperator();
        result = result * 59 + ($operator == null ? 43 : $operator.hashCode());
        Object $operandTwo = this.getOperandTwo();
        result = result * 59 + ($operandTwo == null ? 43 : $operandTwo.hashCode());
        return result;
    }

    public String toString() {
        Filter<T> var10000 = this.getOperandOne();
        return "CompositeFilter(operandOne=" + var10000 + ", operator=" + this.getOperator() + ", operandTwo=" + this.getOperandTwo() + ")";
    }

    public static class CompositeFilterBuilder<T> {
        private Filter<T> operandOne;
        private CompositeFilterOperator operator;
        private Filter<T> operandTwo;

        CompositeFilterBuilder() {
        }

        public CompositeFilterBuilder<T> operandOne(final Filter<T> operandOne) {
            this.operandOne = operandOne;
            return this;
        }

        public CompositeFilterBuilder<T> operator(final CompositeFilterOperator operator) {
            this.operator = operator;
            return this;
        }

        public CompositeFilterBuilder<T> operandTwo(final Filter<T> operandTwo) {
            this.operandTwo = operandTwo;
            return this;
        }

        public CompositeFilter<T> build() {
            return new CompositeFilter<>(this.operandOne, this.operator, this.operandTwo);
        }

        public String toString() {
            return "CompositeFilter.CompositeFilterBuilder(operandOne=" + this.operandOne + ", operator=" + this.operator + ", operandTwo=" + this.operandTwo + ")";
        }
    }
}