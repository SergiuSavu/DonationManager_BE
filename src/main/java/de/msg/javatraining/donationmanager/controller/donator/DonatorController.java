package de.msg.javatraining.donationmanager.controller.donator;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public Optional<Donator> getDonator(@PathVariable("donatorId") Long donatorId) {
        return donatorService.getDonatorById(donatorId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donator donator) {
        Donator don = donatorService.createDonator(userId, donator);
        if (don != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> updateDonator(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId, @RequestBody Donator newDonator) {
        Donator don = donatorService.updateDonator(userId, donatorId, newDonator);
        if (don != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
        if (donatorService.getDonatorById(donatorId).isPresent()) {
            if (donationService.findDonationsByDonatorId(donatorId)) {
                Donator updateValues = new Donator("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
                donatorService.updateDonator(userId, donatorId, updateValues); // daca are donatii nu se sterge din sistem, ii se schimba valorile in UNKNOWN
                return new ResponseEntity<>("Donator values set to UNKNOWN", HttpStatus.OK);
            } else {
                donatorService.deleteDonatorById(userId, donatorId);
                return new ResponseEntity<>("Donator has been deleted", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Donator with given id does not exist", HttpStatus.FORBIDDEN);
    }
}
