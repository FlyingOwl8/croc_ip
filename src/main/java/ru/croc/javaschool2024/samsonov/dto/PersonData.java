package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.croc.javaschool2024.samsonov.data_checker.check_result.CheckResult;

@RequiredArgsConstructor
@Getter
@ToString
public class PersonData {
    private final Integer id;
    private final String fullName;
    private final String passport;
    private final int age;
    private final boolean foreignCitizenship;
    private final String educationDocument;
    private final boolean convicted;
}
