package ma.ebank.ebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountWithCustomerDTO {
    private Long id;
    private String rib;
    private double balance;
    private CustomerDTO customer;
}
