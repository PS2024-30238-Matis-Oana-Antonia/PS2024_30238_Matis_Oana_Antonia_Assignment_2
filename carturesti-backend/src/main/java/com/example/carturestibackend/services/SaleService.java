package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.SaleLogger;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.dtos.mappers.ProductMapper;
import com.example.carturestibackend.dtos.mappers.SaleMapper;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Sale;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.SaleRepository;
import com.example.carturestibackend.validators.SaleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle operations related to sales.
 */
@Service
public class SaleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleValidator saleValidator;

    /**
     * Constructs a new SaleService with the specified repositories and validator.
     *
     * @param saleRepository    The repository for sales.
     * @param productRepository The repository for products.
     * @param saleValidator     The validator for sales.
     */
    @Autowired
    public SaleService(SaleRepository saleRepository, ProductRepository productRepository, SaleValidator saleValidator) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.saleValidator = saleValidator;
    }

    /**
     * Retrieves all sales.
     *
     * @return A list of SaleDTO representing all sales.
     */
    public List<SaleDTO> findAllSales() {
        LOGGER.info(SaleLogger.ALL_SALES_RETRIEVED);
        List<Sale> sales = saleRepository.findAll();
        List<SaleDTO> saleDTOs = new ArrayList<>();

        for (Sale sale : sales) {
            SaleDTO saleDTO = SaleMapper.toSaleDTO(sale);
            List<String> productIdsWithDiscount = new ArrayList<>();

            for (Product product : sale.getProducts()) {
                String productId = product.getId_product();
                double discountPercentage = sale.getDiscount_percentage();
                double originalPrice = product.getPrice();
                double discountedPrice = originalPrice;

                if (discountPercentage > 0) {
                    discountedPrice = originalPrice * (1 - discountPercentage / 100);
                }
                productIdsWithDiscount.add(productId);
            }

            // Set the list of product IDs with discounts in the SaleDTO
            saleDTO.setId_products(productIdsWithDiscount);
            saleDTOs.add(saleDTO);
        }

        return saleDTOs;
    }

    /**
     * Retrieves a sale by its ID.
     *
     * @param id_sale The ID of the sale to retrieve.
     * @return The SaleDTO representing the retrieved sale.
     * @throws ResourceNotFoundException if the sale is not found.
     */
    public SaleDTO findSaleById(String id_sale) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            return SaleMapper.toSaleDTO(saleOptional.get());
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }

    /**
     * Inserts a new sale.
     *
     * @param saleDTO The SaleDTO representing the sale to insert.
     * @return The ID of the newly inserted sale.
     * @throws IllegalArgumentException if the sale data is invalid.
     */
    public String insertSale(SaleDTO saleDTO) {
        Sale sale = SaleMapper.fromSaleDTO(saleDTO);
        if (saleValidator.isValid(sale)) {
            sale = saleRepository.save(sale);
            LOGGER.debug(SaleLogger.SALE_INSERTED, sale.getId_sale());
            return sale.getId_sale();
        } else {
            LOGGER.error(SaleLogger.SALE_INSERT_FAILED_INVALID_DATA);
            throw new IllegalArgumentException("Invalid sale data");
        }
    }

    /**
     * Deletes a sale by its ID.
     *
     * @param id_sale The ID of the sale to delete.
     * @throws ResourceNotFoundException if the sale is not found.
     */
    public void deleteSaleById(String id_sale) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            Sale sale = saleOptional.get();

            // Disassociate the sale from its associated products
            List<Product> products = sale.getProducts();
            for (Product product : products) {
                product.setSale(null);
                productRepository.save(product);
            }

            // Delete the sale
            saleRepository.delete(sale);

            LOGGER.debug(SaleLogger.SALE_DELETED, id_sale);
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }

    /**
     * Updates a sale.
     *
     * @param id_sale  The ID of the sale to update.
     * @param saleDTO The SaleDTO representing the new state of the sale.
     * @return The updated SaleDTO.
     * @throws ResourceNotFoundException if the sale is not found.
     */
    public SaleDTO updateSale(String id_sale, SaleDTO saleDTO) {
        Optional<Sale> saleOptional = saleRepository.findById(id_sale);
        if (saleOptional.isPresent()) {
            Sale existingSale = saleOptional.get();
            existingSale.setDiscount_percentage(saleDTO.getDiscount_percentage());
            Sale updatedSale = saleRepository.save(existingSale);
            LOGGER.debug(SaleLogger.SALE_UPDATED, id_sale);

            return SaleMapper.toSaleDTO(updatedSale);
        } else {
            LOGGER.error(SaleLogger.SALE_NOT_FOUND_BY_ID, id_sale);
            throw new ResourceNotFoundException(Sale.class.getSimpleName() + " with id: " + id_sale);
        }
    }

    /**
     * Applies a discount to a product based on the provided discount percentage.
     *
     * @param product            The product to apply the discount to.
     * @param discountPercentage The percentage of the discount.
     */
    public void applyDiscount(Product product, double discountPercentage) {
        double discountedPrice = product.getPrice() * (1 - discountPercentage / 100);
        product.setPrice(discountedPrice);
        productRepository.save(product);
        LOGGER.info("Discount of {}% applied to product {} (ID: {}). New price: ${}", discountPercentage,
                product.getName(), product.getId_product(), discountedPrice);
    }
}
