package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.croc.javaschool2024.samsonov.data_checker.check_result.CheckResult;

@RequiredArgsConstructor
@Getter
public class PersonData {
    private final Integer id;
    private final String fullName;
    private final String passport;
    private final int age;
    private final boolean foreignCitizenship;
    private final String educationDocument;
    private final boolean convicted;

    @Override
    public String toString() {
        return String.format("ID=%d, ФИО='%s', паспортные данные='%s', возраст=%d, " +
                "имеет гражданство другой страны=%b, документ об образовании='%s', имеет судимость=%b",
                id, fullName, passport, age, foreignCitizenship, educationDocument, convicted);
    }
}
