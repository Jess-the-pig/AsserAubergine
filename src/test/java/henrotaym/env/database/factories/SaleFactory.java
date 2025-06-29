package henrotaym.env.database.factories;

import henrotaym.env.entities.Sale;

import net.datafaker.Faker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class SaleFactory extends EntityFactory<Sale> {

    public SaleFactory(Faker faker, JpaRepository<Sale, Long> repository) {
        this.faker = faker;
        this.repository = repository;
    }

    @Override
    protected Sale entity() {
        return new Sale();
    }

    @Override
    protected void attributes(Sale entity) {
        entity.setAmount(BigInteger.valueOf(this.faker.number().numberBetween(1000, 10000)));
    }
}
