package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDTO> patientResponseDTOS =
                patients.stream().map(patient-> PatientMapper.toDTO(patient)).toList();
        return patientResponseDTOS;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient already exists with this emailId " + patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id,PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id " + id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)) {
            throw new EmailAlreadyExistsException("A patient already exists with this emailId " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setBirthDate(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patientRepository.save(patient);
        return PatientMapper.toDTO(patient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
