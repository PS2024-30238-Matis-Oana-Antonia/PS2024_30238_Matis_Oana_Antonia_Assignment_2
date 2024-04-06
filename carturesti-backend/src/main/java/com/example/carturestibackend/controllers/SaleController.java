package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.SaleLogger;
import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.services.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @PostMapping
    public ModelAndView insertSale(@RequestBody SaleDTO saleDTO) {
        String saleID = saleService.insertSale(saleDTO);
        LOGGER.debug(SaleLogger.SALE_INSERTED, saleID);
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("saleID", saleID);
        return modelAndView;
    }

    @DeleteMapping("/{id_sale}")
    public ModelAndView deleteSale(@PathVariable("id_sale") String id_sale) {
        saleService.deleteSaleById(id_sale);
        LOGGER.debug(SaleLogger.SALE_DELETED, id_sale);
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("message", "Sale with ID " + id_sale + " deleted successfully");
        return modelAndView;
    }

    @PutMapping("/{id_sale}")
    public ModelAndView updateSale(@PathVariable("id_sale") String id_sale, @RequestBody SaleDTO saleDTO) {
        LOGGER.debug(SaleLogger.SALE_UPDATED, id_sale);
        SaleDTO updatedSale = saleService.updateSale(id_sale, saleDTO);
        ModelAndView modelAndView = new ModelAndView("/sale");
        modelAndView.addObject("sale", updatedSale);
        return modelAndView;
    }
}
