package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.OfficeEntity;
import com.example.admin_backend.Repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    public List<OfficeEntity> getAllOffices() {
        return officeRepository.findAll();
    }

    public List<OfficeEntity> getOfficesByStatus(Boolean status) {
        if (status == null) {
            return getAllOffices();
        } else {
            return officeRepository.findAllByStatus(status);
        }
    }

    public OfficeEntity addOffice(OfficeEntity office) {
        return officeRepository.save(office);
    }

    public OfficeEntity updateOfficeStatus(Long officeId, boolean status) {
        OfficeEntity office = officeRepository.findById(officeId)
                .orElseThrow(() -> new RuntimeException("Office not found"));
        office.setStatus(status);
        return officeRepository.save(office);
    }

     public Optional<OfficeEntity> findById(Long id) {
        return officeRepository.findById(id);
    }

    // Method to save an office entity
    public OfficeEntity save(OfficeEntity office) {
        return officeRepository.save(office);
    }
}
