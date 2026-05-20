package com.a.demo.rest;

import com.a.demo.dto.mapper.PersonalInfoMapper;
import com.a.demo.dto.request.PersonalInfoRequest;
import com.a.demo.dto.response.PersonalInfoResponse;
import com.a.demo.model.PersonalInfo;
import com.a.demo.service.IEducationService;
import com.a.demo.service.IExperienceService;
import com.a.demo.service.IPersonalInfoService;
import com.a.demo.service.ISkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST: Es el punto de entrada a tu aplicacion desde el exterior.
 * Aqui definimos las rutas (URLs) que los usuarios o aplicaciones externas pueden llamar.
 *
 * Anotaciones importantes:
 *   @RestController         -> @Controller + @ResponseBody. Todo lo que devuelven los metodos
 *                              se serializa a JSON automaticamente.
 *   @RequestMapping("...")  -> prefijo comun para todas las rutas de esta clase.
 *   @RequiredArgsConstructor-> Lombok genera el constructor con los campos final (inyeccion).
 *
 * Convenciones REST que aplicamos:
 *   GET    /recurso        -> lista
 *   GET    /recurso/{id}   -> uno por id    (200 OK / 404 Not Found)
 *   POST   /recurso        -> crear         (201 Created + Location header)
 *   PUT    /recurso/{id}   -> actualizar    (200 OK / 404 Not Found)
 *   DELETE /recurso/{id}   -> borrar        (204 No Content / 404 Not Found)
 */
@RestController
@RequestMapping("/api/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {

    private final IPersonalInfoService personalInfoService;
    private final ISkillService skillService;
    private final IEducationService educationService;
    private final IExperienceService experienceService;
    private final PersonalInfoMapper mapper;

    /**
     * Listado de todas las personas. Devuelve respuestas "ligeras" sin las colecciones
     * (para no traer N+1 consultas a la BD por cada item).
     */
    @GetMapping
    public ResponseEntity<List<PersonalInfoResponse>> getAll() {
        List<PersonalInfo> list = personalInfoService.findAll();
        return ResponseEntity.ok(mapper.toResponseListWithoutCollections(list));
    }

    /**
     * Endpoint anterior: lo mantenemos por compatibilidad con front existente.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PersonalInfoResponse>> getAllPersonalInfo() {
        return getAll();
    }

    /**
     * @PostMapping se usa para CREAR nuevos registros.
     * @Valid le dice a Spring que revise las reglas que pusimos en el DTO (como @NotBlank).
     * Si los datos no son validos, Spring lanza MethodArgumentNotValidException,
     * el GlobalExceptionHandler la convierte en respuesta JSON 400.
     *
     * Devolvemos 201 Created + cabecera Location con la URI del nuevo recurso (buena practica REST).
     */
    @PostMapping
    public ResponseEntity<PersonalInfoResponse> createPersonalInfo(@Valid @RequestBody PersonalInfoRequest request) {
        PersonalInfo entity = mapper.toEntity(request);
        PersonalInfo saved = personalInfoService.save(entity);
        URI location = URI.create("/api/personal-info/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toResponseWithoutCollections(saved));
    }

    /**
     * {id} en el mapeo es una "variable de ruta" (path variable).
     * @PathVariable le dice a Spring que extraiga el valor de la URL y lo pase al parametro.
     *
     * Aqui SI armamos el response completo, cargando las colecciones desde sus repositorios.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalInfoResponse> getPersonalInfoById(@PathVariable Long id) {
        PersonalInfo p = personalInfoService.findByIdOrThrow(id);
        PersonalInfoResponse response = mapper.toResponse(
                p,
                skillService.findByPersonalInfoId(id),
                educationService.findByPersonalInfoId(id),
                experienceService.findByPersonalInfoId(id)
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Busqueda por email (query string: /api/personal-info/search?email=foo@bar.com).
     * Util en flujos reales: chequear duplicados antes de registrar, recuperar contrasena, etc.
     */
    @GetMapping("/search")
    public ResponseEntity<PersonalInfoResponse> getByEmail(@RequestParam String email) {
        return personalInfoService.findByEmail(email)
                .map(mapper::toResponseWithoutCollections)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar un PersonalInfo existente. Si no existe -> 404 (lo lanza el service).
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonalInfoResponse> updatePersonalInfo(@PathVariable Long id,
                                                                    @Valid @RequestBody PersonalInfoRequest request) {
        PersonalInfo entity = mapper.toEntity(request);
        PersonalInfo updated = personalInfoService.update(id, entity);
        return ResponseEntity.ok(mapper.toResponseWithoutCollections(updated));
    }

    /**
     * DELETE -> 204 No Content (sin body) cuando se borro correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalInfo(@PathVariable Long id) {
        personalInfoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
