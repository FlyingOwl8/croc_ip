package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Candidate {
    private final int id;
    private final int requestId;
    private final int personId;
    private final boolean selfNominated;
    private final String party;
    private final Map<String, Integer> signs;

    @Override
    public String toString() {
        return String.format("ID=%d, ID заявки=%d, ID выдвиженца=%d, самовыдвиженец=%b, партия=%s, подписи=%s",
                id, requestId, personId, selfNominated, party, signs);
    }
}
