package henrotaym.env.test.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import henrotaym.env.entities.Sale;
import henrotaym.env.entities.Vegetable;
import henrotaym.env.exceptions.InsufficientStockException;
import henrotaym.env.http.requests.SaleRequest;
import henrotaym.env.http.requests.SaleVegetableRequest;
import henrotaym.env.http.requests.relationships.VegetableRelationshipRequest;
import henrotaym.env.repositories.SaleRepository;
import henrotaym.env.repositories.SaleVegetableRepository;
import henrotaym.env.repositories.VegetableRepository;
import henrotaym.env.services.SaleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.List;

public class SaleServiceTest {

    @Mock private VegetableRepository vegetableRepository;
    @Mock private SaleRepository saleRepository;
    @Mock private SaleVegetableRepository saleVegetableRepository;

    @InjectMocks private SaleService saleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkout_success_whenStockSufficient() {
        // Given
        Vegetable veg1 = new Vegetable();
        veg1.setId(1L);
        veg1.setStock(BigInteger.valueOf(10));
        veg1.setPrice(BigInteger.valueOf(5));

        // Créer le VegetableRelationshipRequest avec l'id du légume
        VegetableRelationshipRequest vegRelReq = new VegetableRelationshipRequest(veg1.getId());

        // Créer SaleVegetableRequest avec quantité et légume (relationship)
        SaleVegetableRequest svReq = new SaleVegetableRequest(BigInteger.valueOf(5), vegRelReq);

        SaleRequest saleRequest = new SaleRequest();
        saleRequest.setVegetables(List.of(svReq));

        when(vegetableRepository.findAllById(List.of(1L))).thenReturn(List.of(veg1));

        // When
        Sale sale = saleService.checkout(saleRequest);

        // Then
        assertNotNull(sale);
        assertEquals(BigInteger.valueOf(25), sale.getAmount()); // 5 * 5

        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(vegetableRepository, times(1)).saveAll(anyList());
        verify(saleVegetableRepository, times(1)).saveAll(anyList());

        // Stock doit être décrémenté
        assertEquals(BigInteger.valueOf(5), veg1.getStock());
    }

    @Test
    void checkout_fails_whenStockInsufficient() {
        // Given
        Vegetable veg1 = new Vegetable();
        veg1.setId(1L);
        veg1.setStock(BigInteger.valueOf(3));
        veg1.setPrice(BigInteger.valueOf(5));

        VegetableRelationshipRequest vegRelReq = new VegetableRelationshipRequest(veg1.getId());

        SaleVegetableRequest svReq =
                new SaleVegetableRequest(
                        BigInteger.valueOf(5), vegRelReq); // demande plus que stock

        SaleRequest saleRequest = new SaleRequest();
        saleRequest.setVegetables(List.of(svReq));

        when(vegetableRepository.findAllById(List.of(1L))).thenReturn(List.of(veg1));

        // When / Then
        assertThrows(InsufficientStockException.class, () -> saleService.checkout(saleRequest));

        verifyNoInteractions(saleRepository);
        verify(vegetableRepository, never()).saveAll(any());
        verify(saleVegetableRepository, never()).saveAll(any());
    }
}
