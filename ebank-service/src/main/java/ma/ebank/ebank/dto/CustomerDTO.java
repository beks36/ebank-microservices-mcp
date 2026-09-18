package ma.ebank.ebank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Représente le Customer tel que renvoyé par customer-service.
// On duplique volontairement la structure (pas de module partagé) pour rester simple.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
