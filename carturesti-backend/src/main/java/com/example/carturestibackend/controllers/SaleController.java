package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/sale")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<SaleDTO> dtos = saleService.findAllSales();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id_sale}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable("id_sale") String id_sale) {
        SaleDTO dto = saleService.findSaleById(id_sale);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> insertSale(@RequestBody SaleDTO saleDTO) {
        String saleID = saleService.insertSale(saleDTO);
        return new ResponseEntity<>(saleID, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id_sale}")
    public ResponseEntity<String> deleteSale(@PathVariable("id_sale") String id_sale) {
        saleService.deleteSaleById(id_sale);
        return new ResponseEntity<>("Sale with ID " + id_sale + " deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{id_sale}")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable("id_sale") String id_sale, @RequestBody SaleDTO saleDTO) {
        SaleDTO updatedSale = saleService.updateSale(id_sale, saleDTO);
        return new ResponseEntity<>(updatedSale, HttpStatus.OK);
    }
}
