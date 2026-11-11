package com.example.demo;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchitectureTests {

  @Test
  void existingApplicationPackagesHaveNoCycles() {
    var classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.demo");

    assertThat(classes).isNotEmpty();
    assertThat(classes.stream().map(type -> type.getPackageName()).distinct())
        .contains("com.example.demo.model.entity", "com.example.demo.model.enums");

    slices().matching("com.example.demo.(**)").should().beFreeOfCycles().check(classes);
  }
}
