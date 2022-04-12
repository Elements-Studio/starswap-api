package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;


@Embeddable
public class Quotient {

    @Column(nullable = false)
    private Long numerator;

    @Column(nullable = false)
    private Long denominator;

    public Quotient() {
    }

    public Quotient(Long numerator, Long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Long getNumerator() {
        return numerator;
    }

    public void setNumerator(Long numerator) {
        this.numerator = numerator;
    }

    public Long getDenominator() {
        return denominator;
    }

    public void setDenominator(Long denominator) {
        this.denominator = denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quotient quotient = (Quotient) o;
        return Objects.equals(numerator, quotient.numerator) && Objects.equals(denominator, quotient.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return "Quotient{" +
                "numerator=" + numerator +
                ", denominator=" + denominator +
                '}';
    }
}
