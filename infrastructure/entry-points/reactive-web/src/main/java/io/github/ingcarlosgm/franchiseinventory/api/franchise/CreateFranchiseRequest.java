package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFranchiseRequest {
    private String name;
    private String contactEmail;
    private String website;
}