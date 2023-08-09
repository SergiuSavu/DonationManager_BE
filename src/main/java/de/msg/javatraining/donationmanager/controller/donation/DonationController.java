package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.persistence.DonationModel.Donation;
import de.msg.javatraining.donationmanager.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @PostMapping()
    public void createDonation(@RequestBody Donation donation) {
        donationService.createDonation(donation);
    }

    @PutMapping("/{id}")
    public void updateDonation(@PathVariable("id") Long id, @RequestBody Donation newDonation) {
        donationService.updateDonation(id, newDonation);
    }

    @DeleteMapping("/{id}")
    public void deleteDonationById(@PathVariable("id") Long id) {
        donationService.deleteDonationById(id);
    }
}
