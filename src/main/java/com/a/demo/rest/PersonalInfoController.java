package com.a.demo.rest;

import com.a.demo.model.PersonalInfo;
import com.a.demo.service.IPersonalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST: Es el punto de entrada a tu aplicación desde el exterior.
 * Aquí definimos las rutas (URLs) que los usuarios o aplicaciones externas pueden llamar.
 */
@RestController
@RequestMapping("/api/personal-info")
@RequiredArgsConstructor // Genera automáticamente el constructor para inyectar dependencias
public class PersonalInfoController {

    private final IPersonalInfoService personalInfoService;

    /**
     * @GetMapping es la forma moderna de definir una ruta GET.
     * ResponseEntity permite devolver tanto los datos como códigos de estado HTTP (ej. 200 OK).
     */
    @GetMapping("/all")
    public ResponseEntity<List<PersonalInfo>> getAllPersonalInfo() {
        List<PersonalInfo> list = personalInfoService.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * @PostMapping se usa para CREAR nuevos registros.
     * @Valid le dice a Spring que revise las reglas que pusimos en el modelo (como @NotBlank).
     * Si los datos no son válidos, Spring devolverá un error automáticamente.
     */
    @PostMapping
    public ResponseEntity<PersonalInfo> createPersonalInfo(@jakarta.validation.Valid @RequestBody PersonalInfo personalInfo) {
        return ResponseEntity.ok(personalInfoService.save(personalInfo));
    }

    /**
     * {id} en el mapeo es una "variable de ruta" (path variable).
     * @PathVariable le dice a Spring que extraiga el valor de la URL y lo pase al parámetro.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalInfo> getPersonalInfoById(@PathVariable Long id) {
        return personalInfoService.findById(id)
                .map(ResponseEntity::ok) // Si se encuentra, devuelve 200 OK con los datos
                .orElse(ResponseEntity.notFound().build()); // Si no, devuelve 404 Not Found
    }

}
