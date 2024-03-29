package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.SaleLogger;
import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.dtos.mappers.SaleMapper;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Sale;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    @Autowired
    public SaleService(SaleRepository saleRepository,  ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    public List<SaleDTO> findAllSales() {
        LOGGER.info(SaleLogger.ALL_SALES_RETRIEVED);
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(SaleMapper::toSaleDTO)
                .collect(Collectors.toList());
    }

    public SaleDTO findSaleById(String id_sale) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            return SaleMapper.toSaleDTO(saleOptional.get());
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }

    public String insertSale(SaleDTO saleDTO) {
        Sale sale = SaleMapper.fromSaleDTO(saleDTO);
        sale = saleRepository.save(sale);
        LOGGER.debug(SaleLogger.SALE_INSERTED, sale.getId_sale());
        return sale.getId_sale();
    }

    public void deleteSaleById(String id_sale) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            saleRepository.delete(saleOptional.get());
            LOGGER.debug(SaleLogger.SALE_DELETED, id_sale);
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }

    public SaleDTO updateSale(String id_sale, SaleDTO saleDTO) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            Sale existingSale = saleOptional.get();
            existingSale.setDiscount_percentage(saleDTO.getDiscount_percentage());
            existingSale.setPrice_after_discount(saleDTO.getPrice_after_discount());

            Sale updatedSale = saleRepository.save(existingSale);
            LOGGER.debug(SaleLogger.SALE_UPDATED, id_sale);

            return SaleMapper.toSaleDTO(updatedSale);
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }
    public void applyDiscount(Product product, double discountPercentage) {
        double discountedPrice = product.getPrice() * (1 - discountPercentage / 100);
        product.setPrice(discountedPrice);
        productRepository.save(product);
    }
}
