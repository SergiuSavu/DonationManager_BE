package de.msg.javatraining.donationmanager.controller.donator;

import de.msg.javatraining.donationmanager.persistence.donatorModel.Donator;
import de.msg.javatraining.donationmanager.service.donatorService.DonatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/donator")
public class DonatorController {
    @Autowired
    private DonatorService donatorService;

    @GetMapping()
    public List<Donator> getAllDonators() {
        return donatorService.getAllDonators();
    }

    @GetMapping("/{id}")
    public Optional<Donator> getDonator(@PathVariable("id") Long id) {
        return donatorService.getDonatorById(id);
    }

    @PostMapping()
    public void createDonator(@RequestBody Donator donator) {
        donatorService.createDonator(donator);
    }

    @PutMapping("/{id}")
    public void updateDonator(@PathVariable("id") Long id, @RequestBody Donator newDonator) {
        donatorService.updateDonator(id, newDonator);
    }

    @DeleteMapping("/{id}")
    public void deleteDonationById(@PathVariable("id") Long id) {
        donatorService.deleteDonatorById(id);
    }
}
