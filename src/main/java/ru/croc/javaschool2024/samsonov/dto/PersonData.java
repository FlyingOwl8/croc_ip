package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.croc.javaschool2024.samsonov.check_result.CheckResult;

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

    public CheckResult checkPersonData() {
        if (age < 35) {
            return new CheckResult(false, "Возраст меньше 35 лет");
        }
        if (foreignCitizenship) {
            return new CheckResult(false, "Имеет гражданство другой страны");
        }
        if (convicted) {
            return new CheckResult(false, "Имеет судимость");
        }
        return new CheckResult(true, null);
    }
}
