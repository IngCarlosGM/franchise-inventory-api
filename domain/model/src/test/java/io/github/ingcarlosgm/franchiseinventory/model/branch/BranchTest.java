package io.github.ingcarlosgm.franchiseinventory.model.branch;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BranchTest {

    @Test
    void shouldCreateBranchWithValidName() {
        Branch branch = Branch.builder().name("Centro").build();

        assertEquals("Centro", branch.getName());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        Branch.BranchBuilder builder = Branch.builder();

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Branch.BranchBuilder builder = Branch.builder().name("");

        assertThrows(InvalidDataException.class, builder::build);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Branch.BranchBuilder builder = Branch.builder().name("   ");

        assertThrows(InvalidDataException.class, builder::build);
    }
}