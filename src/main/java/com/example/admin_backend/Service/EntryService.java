package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.EntryEntity;
import com.example.admin_backend.Repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    public EntryEntity saveEntry(EntryEntity entryEntity) {
        return entryRepository.save(entryEntity);
    }

    public Optional<EntryEntity> getEntryById(Long entryId) {
        return entryRepository.findById(entryId);
    }

    public List<EntryEntity> getAllEntries() {
        return entryRepository.findAll();
    }

    public void deleteEntry(Long entryId) {
        if (entryRepository.existsById(entryId)) {
            entryRepository.deleteById(entryId);
        } else {
            throw new RuntimeException("Entry not found with ID: " + entryId);
        }
    }
}