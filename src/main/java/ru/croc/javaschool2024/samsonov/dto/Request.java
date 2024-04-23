package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Request {
    private final Integer id;
    private final PersonData personData;
    private final boolean selfNominated;
    private final String party;
    private final Map<String, Integer> signs;
    private final String status;

    @Override
    public String toString() {
        return String.format("ID=%d, личные данные={%s}, самовыдвиженец=%b, партия=%s, " +
                        "подписи=%s, статус заявки='%s'",
                id, personData, selfNominated, party, signs, status);
    }
}
