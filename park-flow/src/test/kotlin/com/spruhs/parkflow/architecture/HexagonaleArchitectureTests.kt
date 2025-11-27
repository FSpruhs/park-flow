package com.spruhs.parkflow.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class HexagonaleArchitectureTests {
    private val basePackage = "com.spruhs.parkflow"
    private val importedClasses = ClassFileImporter().importPackages(basePackage)

    @Test
    fun `domain should not depend on application or adapter`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..")
            .check(importedClasses)
    }

    @Test
    fun `application should not depend on adapter`() {
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter..")
            .check(importedClasses)
    }

    @ParameterizedTest(name = "{index}: {0} should reside in {1} package")
    @MethodSource("allowedNaming")
    fun `allowed naming rules`(
        naming: String,
        packageName: String,
    ) {
        classes()
            .that().haveSimpleNameEndingWith(naming)
            .should().resideInAPackage(packageName)
            .check(importedClasses)
    }

    companion object {
        @JvmStatic
        fun allowedNaming(): Stream<Arguments> =
            Stream.of(
                Arguments.of("Aggregate", "..domain.."),
                Arguments.of("Repository", "..adapter.secondary.."),
                Arguments.of("Adapter", "..adapter.."),
                Arguments.of("Port", "..application.."),
                Arguments.of("Command", "..application.."),
                Arguments.of("Message", "..adapter.primary.."),
                Arguments.of("Request", "..adapter.primary.."),
                Arguments.of("Projection", "..domain.."),
            )
    }

    @Test
    fun `event should reside in api package`() {
        classes()
            .that().haveSimpleNameEndingWith("Event")
            .should().resideInAnyPackage("..api..", "..common..")
            .check(importedClasses)
    }
}
