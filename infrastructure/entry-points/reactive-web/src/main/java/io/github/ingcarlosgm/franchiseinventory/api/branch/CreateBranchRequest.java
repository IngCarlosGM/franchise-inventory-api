package io.github.ingcarlosgm.franchiseinventory.api.branch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBranchRequest {
    private String name;
    private String city;
    private String phone;
}