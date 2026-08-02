package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FranchiseResponse {
    private final String id;
    private final String name;
    private final String contactEmail;
    private final String website;
    private final Instant createdAt;
    private final Instant updatedAt;
}