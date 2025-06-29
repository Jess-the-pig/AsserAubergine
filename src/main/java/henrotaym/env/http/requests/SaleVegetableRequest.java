import henrotaym.env.http.requests.relationships.VegetableRelationshipRequest;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class SaleVegetableRequest {
    private BigInteger quantity;
    private VegetableRelationshipRequest vegetable;
}
