package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.croc.javaschool2024.samsonov.data_checker.check_result.CheckResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.*;

@RequiredArgsConstructor
@Getter
@ToString
public class Request {
    private final Integer id;
    private final PersonData personData;
    private final boolean selfNominated;
    private final String party;
    private final Map<String, Integer> signs;
    private final String status;
}
