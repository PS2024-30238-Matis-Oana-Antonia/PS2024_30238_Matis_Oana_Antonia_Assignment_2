package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.SaleLogger;
import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.SaleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping(value = "/sale")
public class SaleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleController.class);

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ModelAndView getAllSales() {
        LOGGER.info(SaleLogger.ALL_SALES_RETRIEVED);
        List<SaleDTO> dtos = saleService.findAllSales();
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("sales", dtos);
        return modelAndView;
    }

    @GetMapping("/{id_sale}")
    public ModelAndView getSaleById(@PathVariable("id_sale") String id_sale) {
        LOGGER.info(SaleLogger.SALE_RETRIEVED_BY_ID, id_sale);
        SaleDTO dto = saleService.findSaleById(id_sale);
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("sale", dto);
        return modelAndView;
    }

    @PostMapping("/insertSale")
    public ModelAndView insertSale(@ModelAttribute @Valid SaleDTO saleDTO) {
        String saleID = saleService.insertSale(saleDTO);
        LOGGER.debug(SaleLogger.SALE_INSERTED, saleID);
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("saleID", saleID);
        return new ModelAndView("redirect:/sale");
    }

    @PostMapping(value = "/deleteSale")
    public ModelAndView deleteSale(@RequestParam("id_sale") String saleId, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/sale"); // Redirecting back to the sales page
        try {
            saleService.deleteSaleById(saleId);
            LOGGER.debug(SaleLogger.SALE_DELETED, saleId);
            redirectAttributes.addFlashAttribute("successMessage", "Sale with ID " + saleId + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete sale with ID " + saleId + ". Please try again.");
        }
        return mav;
    }

    @PostMapping("/saleUpdate")
    public ModelAndView updateSale(@RequestParam("id_sale") String id_sale, @Valid @ModelAttribute SaleDTO saleDTO) {
        ModelAndView mav = new ModelAndView("redirect:/sale");
        try {
            SaleDTO updatedSale = saleService.updateSale(id_sale, saleDTO);
            mav.addObject("successMessage", "Sale updated successfully!");
        } catch (Exception e) {
            mav.addObject("errorMessage", "Failed to update sale. Please try again.");
        }
        return mav;
    }

}
