package ru.croc.javaschool2024.samsonov.dto;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@RequiredArgsConstructor
@ToString
public class Candidate {
    private final int id;
    private final int requestId;
    private final int personId;
    private final boolean selfNominated;
    private final String party;
    private final Map<String, Integer> signs;
}
