package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<?> getDonation(@PathVariable("donationId") Long donationId) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            return ResponseEntity.ok(donation);
        } catch (DonationNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
        // return donationService.getDonationById(donationId);
    }

    @PostMapping("/{donatorId}/{campaignId}/{userId}")
    public ResponseEntity<?> createDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donatorId") Long donatorId,
                                            @PathVariable("campaignId") Long campaignId,
                                            @RequestBody Donation donation) {
        ResponseEntity<?> response;
        try {
            Donation don = donationService.createDonation(userId, donatorId, campaignId, donation);
            //if (don != null) {
                //return ResponseEntity.ok("Donation created successfully!");
            //}
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
            //return ResponseEntity.ok("Donation has not been created!");
            //return response;
        } catch (UserPermissionException | DonationRequirementsException | DonationException exception) {
            //return ResponseEntity.ok(exception.getMessage());
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/{donationId}/{userId}")
    public ResponseEntity<?> updateDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donationId") Long donationId,
                                            @RequestBody Donation newDonation) {

        try {
            Donation don = donationService.updateDonation(userId, donationId, newDonation);
            if (don != null) {
                return ResponseEntity.ok("Donation updated successfully!");
            }
            return ResponseEntity.ok("Donation has not been updated!");
        } catch (DonationRequirementsException
                 | DonationIdException
                 | UserPermissionException
                 | DonationNotFoundException
                 | DonationApprovedException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

    @PatchMapping("/{donationId}/{userId}")
    public ResponseEntity<?> approveDonation(@PathVariable("donationId") Long donationId, @PathVariable("userId") Long userId) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            if (!Objects.equals(donation.getCreatedBy().getId(), userId)) {
                if (!donation.isApproved()) {
                    donationService.approveDonation(donationId, userId);
                }
            }
            return ResponseEntity.ok("Changes happened!");
        } catch (DonationNotFoundException | UserNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }

    @DeleteMapping("/{donationId}/{userId}")
    public ResponseEntity<?> deleteDonationById(@PathVariable("userId") Long userId,
                                                @PathVariable("donationId") Long donationId) {


        ResponseEntity<?> response;
        try {
            Donation donation = donationService.getDonationById(donationId);
            if (donation != null) {
                if (!donation.isApproved()) {
                    donationService.deleteDonationById(userId, donationId);
                    return ResponseEntity.ok("Donation has been deleted!");
                }
                return ResponseEntity.ok("Can't delete a donation which has been approved!");
            }
            return ResponseEntity.ok("Donation with given id does not exist!");
        } catch (DonationIdException
                 | DonationNotFoundException
                 | DonationApprovedException
                 | UserPermissionException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

}
