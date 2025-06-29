package henrotaym.env.tests.feature;

import henrotaym.env.ApplicationTest;
import henrotaym.env.entities.Sale;
import henrotaym.env.entities.Vegetable;
import henrotaym.env.exceptions.InsufficientStockException;
import henrotaym.env.http.requests.SaleRequest;
import henrotaym.env.http.requests.SaleVegetableRequest;
import henrotaym.env.repositories.VegetableRepository;
import henrotaym.env.services.SaleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class SaleServiceFeatureTest extends ApplicationTest {

    @Autowired private SaleService saleService;
    @Autowired private VegetableRepository vegetableRepository;

    private Vegetable vegetable;

    @BeforeEach
    void setUp() {
        vegetableRepository.deleteAll();

        vegetable = new Vegetable();
        vegetable.setName("Tomate");
        vegetable.setPrice(BigInteger.valueOf(10));
        vegetable.setStock(BigInteger.valueOf(20));
        vegetableRepository.save(vegetable);
    }

    @Test
    void checkout_decrementsStock_whenStockSufficient() {
        // Arrange
        SaleVegetableRequest svReq = new SaleVegetableRequest();
        svReq.setVegetable(vegetable);
        svReq.setQuantity(BigInteger.valueOf(5));

        SaleRequest saleRequest = new SaleRequest();
        saleRequest.setVegetables(List.of(svReq));

        // Act
        Sale sale = saleService.checkout(saleRequest);

        // Assert
        Vegetable updatedVegetable = vegetableRepository.findById(vegetable.getId()).orElseThrow();
        assertThat(updatedVegetable.getStock()).isEqualTo(BigInteger.valueOf(15)); // 20 - 5

        assertThat(sale.getAmount()).isEqualTo(BigInteger.valueOf(50)); // 5 * 10
        assertThat(sale.getSaleVegetables()).hasSize(1);
    }

    @Test
    void checkout_throwsException_whenStockInsufficient() {
        // Arrange
        SaleVegetableRequest svReq = new SaleVegetableRequest();
        svReq.setVegetable(vegetable);
        svReq.setQuantity(BigInteger.valueOf(50)); // plus que le stock disponible

        SaleRequest saleRequest = new SaleRequest();
        saleRequest.setVegetables(List.of(svReq));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> saleService.checkout(saleRequest));

        // Le stock n'a pas changé
        Vegetable updatedVegetable = vegetableRepository.findById(vegetable.getId()).orElseThrow();
        assertThat(updatedVegetable.getStock()).isEqualTo(BigInteger.valueOf(20));
    }
}
