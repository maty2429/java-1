package com.a.demo.service;

import com.a.demo.exception.ValidationException;
import com.a.demo.model.PersonalInfo;
import com.a.demo.repository.IPersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicio: Aquí reside la Lógica de Negocio.
 * El Service actúa como intermediario entre el Controlador (Web) y el Repositorio (Base de Datos).
 */
@Service
@RequiredArgsConstructor // Crea automáticamente el constructor para inyección de dependencias (campos final)
@Slf4j // Habilita el objeto 'log' para imprimir mensajes profesionales en la consola
public class PersonalInfoServiceImpl implements IPersonalInfoService {

    // La inyección por constructor es la forma recomendada en Spring
    private final IPersonalInfoRepository personalInfoRepository;
    private final Validator validator;

    @Override
    public PersonalInfo save(PersonalInfo personalInfo) {
        // Creamos un BindingResult manualmente para capturar errores en la capa de servicio
        BindingResult result = new BeanPropertyBindingResult(personalInfo, "personalInfo");
        
        // Ejecutamos la validación manualmente usando el validador de Spring
        validator.validate(personalInfo, result);
        
        // Si hay errores, lanzamos nuestra excepción personalizada
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        
        log.info("Saving personal info for: {} {}", personalInfo.getFirstName(), personalInfo.getLastName());
        return personalInfoRepository.save(personalInfo);
    }

    @Override
    public Optional<PersonalInfo> findById(Long id) {
        log.info("Fetching personal info with ID: {}", id);
        return personalInfoRepository.findById(id);
    }

    @Override
    public List<PersonalInfo> findAll() {
        log.info("Retrieving all personal info records");
        return personalInfoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting personal info with ID: {}", id);
        personalInfoRepository.deleteById(id);
    }
}
