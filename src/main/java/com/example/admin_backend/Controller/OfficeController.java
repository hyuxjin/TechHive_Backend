package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.OfficeEntity;
import com.example.admin_backend.Service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/office")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @GetMapping("/getAllOffices")
    public List<OfficeEntity> getAllOffices() {
        return officeService.getAllOffices();
    }

    @GetMapping("/getOfficesByStatus")
    public List<OfficeEntity> getOfficesByStatus(@RequestParam(required = false) Boolean status) {
        return officeService.getOfficesByStatus(status);
    }

    @PostMapping("/addOffice")
    public OfficeEntity addOffice(@RequestBody OfficeEntity office) {
        return officeService.addOffice(office);
    }

    @PutMapping("/updateStatus/{id}")
public ResponseEntity<OfficeEntity> updateOfficeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
    Optional<OfficeEntity> officeOptional = officeService.findById(id);
    if (!officeOptional.isPresent()) {
        return ResponseEntity.notFound().build();
    }
    OfficeEntity office = officeOptional.get();
    office.setStatus(status.get("status"));  // Update the status
    officeService.save(office);  // Save the updated office in the database
    return ResponseEntity.ok(office);
}

}