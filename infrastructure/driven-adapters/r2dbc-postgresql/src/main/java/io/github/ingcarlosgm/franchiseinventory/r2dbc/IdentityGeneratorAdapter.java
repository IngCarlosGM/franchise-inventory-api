package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityGeneratorAdapter implements IdentityGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}