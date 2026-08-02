package io.github.ingcarlosgm.franchiseinventory.model.franchise;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FranchiseTest {

    @Test
    void shouldCreateFranchiseWithValidName() {
        Franchise franchise = Franchise.builder().name("Mi Franquicia").build();

        assertEquals("Mi Franquicia", franchise.getName());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        Franchise.FranchiseBuilder builder = Franchise.builder();

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Franchise.FranchiseBuilder builder = Franchise.builder().name("");

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Franchise.FranchiseBuilder builder = Franchise.builder().name("   ");

        assertThrows(InvalidDataException.class, builder::build);
    }
}