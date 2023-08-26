package de.msg.javatraining.donationmanager.controller.donor;

import de.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import de.msg.javatraining.donationmanager.service.donorService.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donator")
public class DonorController {
    @Autowired
    private DonorService donorService;

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donor> getAllDonators() {
        return donorService.getAllDonators();
    }

    @GetMapping("/camp/{campaignId}")
    public List<Donor> getDonatorsByCampaignId(@PathVariable("campaignId") Long campaignId){
        return donorService.getDonatorsByCampaignId(campaignId);
    }

    @GetMapping("/{donatorId}")
    public ResponseEntity<?> getDonator(@PathVariable("donatorId") Long donatorId) {
        try {
            Donor donor = donorService.getDonatorById(donatorId);
            return ResponseEntity.ok(donor);
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
    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donor donor) {
        ResponseEntity<?> response;
        try {
            Donor don = donorService.createDonator(userId, donor);
//            if (don != null) {
//                return ResponseEntity.ok("Donator created successfully!");
//            }
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
        } catch (UserPermissionException | DonatorRequirementsException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
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
                                           @RequestBody Donor newDonor) {
        ResponseEntity<?> response;
        try {
            Donor don = donorService.updateDonator(userId, donatorId, newDonor);
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));

        } catch (DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException
                 | DonatorNotFoundException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
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
        ResponseEntity<?> response;
        try {
            Donor don = donorService.deleteDonatorById(userId, donatorId);
            if (donationService.findDonationsByDonatorId(donatorId)) {
                Donor updateValues = new Donor("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
                donorService.updateDonator(userId, donatorId, updateValues);
                response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
            } else {
                donorService.deleteDonatorById(userId, donatorId);
                response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
            }
        } catch (DonatorNotFoundException
                 | DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

//    @DeleteMapping("/{donatorId}/{userId}")
//    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
//        try {
//            Donor don = donorService.deleteDonatorById(userId, donatorId);
//            if (don != null) {
//                if (donationService.findDonationsByDonatorId(donatorId)) {
//                    Donor updateValues = new Donor("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
//                    donorService.updateDonator(userId, donatorId, updateValues);
//                    return ResponseEntity.ok("Donator values have been set to UNKNOWN!");
//                } else {
//                    donorService.deleteDonatorById(userId, donatorId);
//                    return ResponseEntity.ok("");
//                }
//            }
//            return ResponseEntity.ok("Donator with given id does not exist!");
//        } catch (DonatorNotFoundException
//                 | DonatorIdException
//                 | UserPermissionException
//                 | DonatorRequirementsException exception) {
//            return ResponseEntity.ok(exception.getMessage());
//        }
//    }
}
