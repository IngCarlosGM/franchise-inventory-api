package io.github.ingcarlosgm.franchiseinventory.api.branch;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BranchResponse {
    private final String id;
    private final String franchiseId;
    private final String name;
    private final String city;
    private final String phone;
    private final Instant createdAt;
    private final Instant updatedAt;
}