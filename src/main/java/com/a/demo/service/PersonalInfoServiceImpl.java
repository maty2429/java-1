package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.PersonalInfo;
import com.a.demo.repository.IPersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicio: Aqui reside la Logica de Negocio.
 * El Service actua como intermediario entre el Controlador (Web) y el Repositorio (Base de Datos).
 *
 * ============================================================================
 *  GUIA RAPIDA: que es @Transactional y por que la usamos
 * ============================================================================
 *
 *  Una "transaccion" en BD es un grupo de operaciones que se ejecutan como UNA SOLA UNIDAD.
 *  Regla: TODAS se aplican o NINGUNA. Si una falla a mitad de camino, las anteriores se
 *  deshacen automaticamente (rollback). Asi la BD nunca queda inconsistente.
 *
 *  Ejemplo concreto: imagina un metodo que primero guarda PersonalInfo y despues guarda Skills
 *  asociadas. Si guardar las skills falla, sin transaccion te queda el PersonalInfo grabado
 *  "huerfano" en la BD. Con @Transactional, el guardado del PersonalInfo se revierte tambien.
 *
 *  Que pasa si NO uso @Transactional?
 *    Spring abre y cierra una conexion por cada operacion (auto-commit). Cada INSERT/UPDATE
 *    es independiente. Si la segunda falla, la primera ya esta confirmada en la BD.
 *
 *  Que pasa si uso @Transactional?
 *    Spring abre UNA conexion al entrar al metodo, ejecuta TODO con esa conexion, y al final:
 *      - Si no hubo excepciones -> COMMIT (confirma todos los cambios)
 *      - Si hubo una RuntimeException -> ROLLBACK (deshace todos los cambios)
 *
 *  Y @Transactional(readOnly = true)?
 *    Le dice al motor "este metodo NO escribe, solo lee".
 *      - Hibernate/Spring puede optimizar: no marca entidades como sucias, no hace flush.
 *      - Algunos drivers (Postgres) la usan para hint del optimizador.
 *      - Documenta la intencion: cualquiera que lea el codigo sabe que es solo lectura.
 *    Si por error intentas escribir dentro de un metodo readOnly, suele tirar excepcion
 *    (segun el provider de persistencia).
 *
 *  Reglas practicas:
 *    @Transactional               -> en metodos que ESCRIBEN (save, update, delete).
 *    @Transactional(readOnly=true)-> en metodos que solo LEEN (findById, findAll...).
 *    Sin @Transactional           -> funciona, pero no hay garantia atomica si hay varios pasos.
 *
 *  IMPORTANTE: @Transactional solo funciona si se llama el metodo "desde afuera" del bean.
 *  Si dentro de la misma clase llamas this.metodoTransactional(), Spring NO intercepta.
 *
 * ============================================================================
 */
@Service
@RequiredArgsConstructor // Lombok: genera el constructor para inyectar las dependencias (campos final)
@Slf4j // Lombok: habilita el objeto 'log' para imprimir mensajes profesionales (info/warn/error)
public class PersonalInfoServiceImpl implements IPersonalInfoService {

    // La inyeccion por constructor es la forma recomendada en Spring (campos final = inmutables)
    private final IPersonalInfoRepository personalInfoRepository;
    private final Validator validator;

    @Override
    @Transactional // Escribe en la BD: si algo falla, rollback automatico.
    public PersonalInfo save(PersonalInfo personalInfo) {
        // Validacion manual usando el Validator de Spring.
        // (En la mayoria de los casos alcanza con @Valid en el controller; esto es por si
        //  llaman al service desde otro lado.)
        BindingResult result = new BeanPropertyBindingResult(personalInfo, "personalInfo");
        validator.validate(personalInfo, result);

        if (result.hasErrors()) {
            // Lanzamos nuestra excepcion personalizada: el GlobalExceptionHandler la convertira
            // en una respuesta 400 con JSON detallando los errores.
            throw new ValidationException(result);
        }

        log.info("Saving personal info for: {} {}", personalInfo.getFirstName(), personalInfo.getLastName());
        return personalInfoRepository.save(personalInfo);
    }

    @Override
    @Transactional
    public PersonalInfo update(Long id, PersonalInfo personalInfo) {
        // Primero chequeamos que exista; si no, lanzamos 404 (no creamos uno nuevo).
        if (!personalInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("PersonalInfo", id);
        }
        // Forzamos el id del path; ignoramos cualquier id que venga en el body por seguridad.
        personalInfo.setId(id);
        log.info("Updating personal info ID: {}", id);
        return personalInfoRepository.save(personalInfo);
    }

    @Override
    @Transactional(readOnly = true) // Solo lee: hint de optimizacion + intencion clara
    public Optional<PersonalInfo> findById(Long id) {
        log.info("Fetching personal info with ID: {}", id);
        return personalInfoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalInfo findByIdOrThrow(Long id) {
        return personalInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInfo", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonalInfo> findByEmail(String email) {
        log.info("Fetching personal info by email: {}", email);
        return personalInfoRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalInfo> findAll() {
        log.info("Retrieving all personal info records");
        return personalInfoRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!personalInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("PersonalInfo", id);
        }
        log.warn("Deleting personal info with ID: {}", id);
        personalInfoRepository.deleteById(id);
    }
}
