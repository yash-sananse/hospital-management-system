package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Bill;
import com.example.demo.service.BillService;
import com.example.demo.service.AppointmentService;

@RequestMapping("/reception")
@Controller
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private AppointmentService appointmentService;


    // OPEN GENERATE BILL PAGE

    @GetMapping("/generate-bill")
    public String generateBillPage(
            @RequestParam Long appointmentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Find appointment
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Appointment not found."
            );

            return "redirect:/reception/appointments";
        }

        // Check whether bill already exists
        Bill existingBill =
                billService.getBillByAppointment(appointmentId);

        if (existingBill != null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "A bill already exists for this appointment."
            );

            return "redirect:/reception/appointments";
        }

        // Send appointment to HTML
        model.addAttribute("appointment", appointment);
        model.addAttribute("active", "appointments");

        // Automatically get doctor's consultation fee
        model.addAttribute(
                "consultationFee",
                appointment.getDoctor().getConsultationFee()
        );

        return "AllReception/generate-bill";
    }


    // SAVE BILL

    @PostMapping("/generate-bill")
    public String saveBill(

            @RequestParam Long appointmentId,

            @RequestParam(required = false)
            Double medicineCharge,

            @RequestParam(required = false)
            Double labCharge,

            @RequestParam(required = false)
            Double roomCharge,

            @RequestParam(required = false)
            Double otherCharge,

            @RequestParam(required = false)
            Double discount,

            @RequestParam(required = false)
            Double paidAmount,

            RedirectAttributes redirectAttributes) {

        try {

            // Find appointment
            Appointment appointment =
                    appointmentService.getAppointmentById(appointmentId);

            if (appointment == null) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Appointment not found."
                );

                return "redirect:/reception/appointments";
            }

            // Check duplicate bill
            Bill existingBill =
                    billService.getBillByAppointment(appointmentId);

            if (existingBill != null) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Bill already exists for this appointment."
                );

                return "redirect:/reception/appointments";
            }

            // Create bill
            Bill bill = billService.createBill(
                    appointment,
                    medicineCharge,
                    labCharge,
                    roomCharge,
                    otherCharge,
                    discount,
                    paidAmount
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Bill generated successfully."
            );

            return "redirect:/reception/bill/" + bill.getId();

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to generate bill: " + e.getMessage()
            );

            return "redirect:/reception/appointments";
        }
    }





    // VIEW BILL
    @GetMapping("/bill/{appointmentId}")
    public String viewBill(
            @org.springframework.web.bind.annotation.PathVariable Long appointmentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Bill bill = billService.getBillByAppointment(appointmentId);

        if (bill == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Bill not found."
            );

            return "redirect:/reception/appointments";
        }

        model.addAttribute("bill", bill);
        model.addAttribute("active", "appointments");

        return "AllReception/view-bill";
    }

    @GetMapping("/bill/edit/{id}")
    public String editBill(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Bill bill = billService.getBillById(id);

            model.addAttribute("bill", bill);
            model.addAttribute("active", "appointments");

            return "AllReception/edit-bill";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Bill not found."
            );

            return "redirect:/reception/appointments";
        }
    }

    @PostMapping("/bill/edit/{id}")
    public String updateBill(
            @PathVariable Long id,
            @ModelAttribute Bill bill,
            RedirectAttributes redirectAttributes) {

        try {

            billService.updateBill(id, bill);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Bill updated successfully."
            );

            return "redirect:/reception/bill/" + id;

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to update bill: " + e.getMessage()
            );

            return "redirect:/reception/bill/" + id;
        }
    }

    @PostMapping("/bill/{id}/paid")
    public String markBillAsPaid(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            billService.markAsPaid(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Bill marked as paid successfully."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to mark bill as paid: " + e.getMessage()
            );
        }

        return "redirect:/reception/bill/" + id;
    }

}