import henrotaym.env.http.requests.relationships.VegetableRelationshipRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public class SaleVegetableRequest {
    private final BigInteger quantity;
    private final VegetableRelationshipRequest vegetable;
}
