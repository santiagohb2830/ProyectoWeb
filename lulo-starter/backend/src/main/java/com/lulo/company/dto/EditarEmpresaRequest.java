package com.lulo.company.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarEmpresaRequest {
    private String nombreEmpresa;
    private String dominio;
    @Email(message = "El correo de contacto no es válido")
    private String emailContacto;

    /** Si se envía, reemplaza al admin de la empresa por este. */
    @Email(message = "El correo del nuevo admin no es válido")
    private String nuevoEmailAdmin;
    /** Si se envía junto a nuevoEmailAdmin, sirve para crear el usuario admin. */
    private String nuevoPasswordAdmin;
}
