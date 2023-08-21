package de.msg.javatraining.donationmanager.controller.donator;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donator")
public class DonatorController {
    @Autowired
    private DonatorService donatorService;

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donator> getAllDonators() {
        return donatorService.getAllDonators();
    }

    @GetMapping("/{donatorId}")
    public ResponseEntity<?> getDonator(@PathVariable("donatorId") Long donatorId) {
        return donatorService.getDonatorById(donatorId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donator donator) {
        ResponseEntity<?> don = donatorService.createDonator(userId, donator);
        if (don.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("Donator created successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donator has not been created!");
        }
    }

    @PutMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> updateDonator(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId, @RequestBody Donator newDonator) {
        ResponseEntity<?> don = donatorService.updateDonator(userId, donatorId, newDonator);
        if (don.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("Donator updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donator has not been updated!");
        }
    }

    @DeleteMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
        ResponseEntity<?> donator = donatorService.getDonatorById(donatorId);
        if (donator.getStatusCode() == HttpStatus.OK) {
            if (donationService.findDonationsByDonatorId(donatorId)) {
                Donator updateValues = new Donator("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
                donatorService.updateDonator(userId, donatorId, updateValues); // daca are donatii nu se sterge din sistem, ii se schimba valorile in UNKNOWN
                return ResponseEntity.ok("Donator values have been set to UNKNOWN!");
            } else {
                donatorService.deleteDonatorById(userId, donatorId);
                return ResponseEntity.ok("Donator values have been deleted!");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Donator with given id does not exist!");
    }
}
