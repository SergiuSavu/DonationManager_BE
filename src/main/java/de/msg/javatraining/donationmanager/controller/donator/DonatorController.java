package de.msg.javatraining.donationmanager.controller.donator;

import de.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
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

    @GetMapping("/camp/{campaignId}")
    public List<Donator> getDonatorsByCampaignId(@PathVariable("campaignId") Long campaignId){
        return donatorService.getDonatorsByCampaignId(campaignId);
    }

    @GetMapping("/{donatorId}")
    public ResponseEntity<?> getDonator(@PathVariable("donatorId") Long donatorId) {
        try {
            Donator donator = donatorService.getDonatorById(donatorId);
            return ResponseEntity.ok(donator);
        } catch (DonatorNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
        // return donatorService.getDonatorById(donatorId); -- asta era singura linie de cod inainte
    }

//    @PostMapping("/{userId}")
//    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donator donator) {
//        ResponseEntity<?> don = donatorService.createDonator(userId, donator);
//        if (don.getStatusCode() == HttpStatus.OK) {
//            return ResponseEntity.ok("Donator created successfully!");
//        } else {
//            return ResponseEntity.ok("Donator has not been created!");
//        }
//    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donator donator) {
        try {
            Donator don = donatorService.createDonator(userId, donator);
            if (don != null) {
                return ResponseEntity.ok("Donator created successfully!");
            }
            return ResponseEntity.ok("Donator has not been created!");
        } catch (UserPermissionException | DonatorRequirementsException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }

//    @PutMapping("/{donatorId}/{userId}")
//    public ResponseEntity<?> updateDonator(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId, @RequestBody Donator newDonator) {
//        ResponseEntity<?> don = donatorService.updateDonator(userId, donatorId, newDonator);
//        if (don.getStatusCode() == HttpStatus.OK) {
//            return ResponseEntity.ok("Donator updated successfully");
//        } else {
//            return ResponseEntity.ok("Donator has not been updated!");
//        }
//    }

    @PutMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> updateDonator(@PathVariable("userId") Long userId,
                                           @PathVariable("donatorId") Long donatorId,
                                           @RequestBody Donator newDonator) {
        try {
            Donator don = donatorService.updateDonator(userId, donatorId, newDonator);
            if (don != null) {
                return ResponseEntity.ok("Donator updated successfully!");
            } else {
                return ResponseEntity.ok("Donator has not been updated!");
            }
        } catch (DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException
                 | DonatorNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }

//    @DeleteMapping("/{donatorId}/{userId}")
//    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
//        ResponseEntity<?> donator = donatorService.getDonatorById(donatorId);
//        if (donator.getStatusCode() == HttpStatus.OK) {
//            if (donationService.findDonationsByDonatorId(donatorId)) {
//                Donator updateValues = new Donator("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
//                donatorService.updateDonator(userId, donatorId, updateValues); // daca are donatii nu se sterge din sistem, ii se schimba valorile in UNKNOWN
//                return ResponseEntity.ok("Donator values have been set to UNKNOWN!");
//            } else {
//                donatorService.deleteDonatorById(userId, donatorId);
//                return ResponseEntity.ok("Donator values have been deleted!");
//            }
//        }
//        return ResponseEntity.ok("Donator with given id does not exist!");
//    }

    @DeleteMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
        try {
            Donator don = donatorService.deleteDonatorById(userId, donatorId);
            if (don != null) {
                if (donationService.findDonationsByDonatorId(donatorId)) {
                    Donator updateValues = new Donator("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
                    donatorService.updateDonator(userId, donatorId, updateValues);
                    return ResponseEntity.ok("Donator values have been set to UNKNOWN!");
                } else {
                    donatorService.deleteDonatorById(userId, donatorId);
                    return ResponseEntity.ok("Donator values have been deleted!");
                }
            }
            return ResponseEntity.ok("Donator with given id does not exist!");
        } catch (DonatorNotFoundException
                 | DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }
}
