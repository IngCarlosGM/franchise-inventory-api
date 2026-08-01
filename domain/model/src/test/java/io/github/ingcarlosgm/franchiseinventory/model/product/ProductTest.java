package io.github.ingcarlosgm.franchiseinventory.model.product;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void shouldCreateProductWithValidNameAndStock() {
        Product product = Product.builder().name("Café").stock(40).build();

        assertEquals("Café", product.getName());
        assertEquals(40, product.getStock());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        Product.ProductBuilder builder = Product.builder().stock(10);

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Product.ProductBuilder builder = Product.builder().name("").stock(10);

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Product.ProductBuilder builder = Product.builder().name("   ").stock(10);

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenStockIsNull() {
        Product.ProductBuilder builder = Product.builder().name("Café");

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        Product.ProductBuilder builder = Product.builder().name("Café").stock(-1);

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldAcceptZeroStock() {
        Product product = Product.builder().name("Café").stock(0).build();

        assertEquals(0, product.getStock());
    }

    @Test
    void shouldFailWhenPriceIsNegative() {
        Product.ProductBuilder builder = Product.builder()
                .name("Café").stock(10).price(new BigDecimal("-1.00"));

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldAcceptNullPrice() {
        Product product = Product.builder().name("Café").stock(10).build();

        assertNull(product.getPrice());
    }

    @Test
    void shouldPreserveValidationWhenCopyingWithToBuilder() {
        Product product = Product.builder().name("Café").stock(40).build();

        Product.ProductBuilder copy = product.toBuilder().stock(-1);

        assertThrows(InvalidDataException.class, copy::build);
    }
}