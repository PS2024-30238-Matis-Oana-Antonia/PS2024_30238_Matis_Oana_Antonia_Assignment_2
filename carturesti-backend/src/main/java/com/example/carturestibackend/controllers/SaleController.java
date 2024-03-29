package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.SaleLogger;
import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.services.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        LOGGER.info(SaleLogger.ALL_SALES_RETRIEVED);
        List<SaleDTO> dtos = saleService.findAllSales();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id_sale}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable("id_sale") String id_sale) {
        LOGGER.info(SaleLogger.SALE_RETRIEVED_BY_ID, id_sale);
        SaleDTO dto = saleService.findSaleById(id_sale);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> insertSale(@RequestBody SaleDTO saleDTO) {
        String saleID = saleService.insertSale(saleDTO);
        LOGGER.debug(SaleLogger.SALE_INSERTED, saleID);
        return new ResponseEntity<>(saleID, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id_sale}")
    public ResponseEntity<String> deleteSale(@PathVariable("id_sale") String id_sale) {
        saleService.deleteSaleById(id_sale);
        LOGGER.debug(SaleLogger.SALE_DELETED, id_sale);
        return new ResponseEntity<>("Sale with ID " + id_sale + " deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{id_sale}")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable("id_sale") String id_sale, @RequestBody SaleDTO saleDTO) {
        LOGGER.debug(SaleLogger.SALE_UPDATED, id_sale);
        SaleDTO updatedSale = saleService.updateSale(id_sale, saleDTO);
        return new ResponseEntity<>(updatedSale, HttpStatus.OK);
    }
}
