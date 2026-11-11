package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.entity.Contract;
import com.example.demo.model.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
    properties = {
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.show-sql=false",
      "debug=false"
    })
@ActiveProfiles("integration")
@Testcontainers
class PostgresPersistenceIT {

  @Container
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17.6-alpine");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
  }

  @Autowired EntityManager entityManager;

  @Test
  @Transactional
  void persistsAndReloadsExistingTenantContractRelationship() {
    User tenant = new User();
    ReflectionTestUtils.setField(tenant, "email", "smoke@example.invalid");
    ReflectionTestUtils.setField(tenant, "password", "test-only-not-a-real-password");
    entityManager.persist(tenant);

    Contract contract = new Contract();
    ReflectionTestUtils.setField(contract, "name", "PostgreSQL smoke test");
    ReflectionTestUtils.setField(contract, "tenant", tenant);
    entityManager.persist(contract);
    entityManager.flush();
    Object contractId =
        entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(contract);
    Object tenantId =
        entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(tenant);
    entityManager.clear();

    Contract reloaded = entityManager.find(Contract.class, contractId);
    assertThat(ReflectionTestUtils.getField(reloaded, "name")).isEqualTo("PostgreSQL smoke test");
    User reloadedTenant = (User) ReflectionTestUtils.getField(reloaded, "tenant");
    assertThat(
            entityManager
                .getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(reloadedTenant))
        .isEqualTo(tenantId);
    assertThat(entityManager.createQuery("select u from User u", User.class).getResultList())
        .hasSize(1);
  }
}
