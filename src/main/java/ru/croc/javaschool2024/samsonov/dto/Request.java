package ru.croc.javaschool2024.samsonov.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.croc.javaschool2024.samsonov.check_result.CheckResult;

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
    private static final List<String> selectedParties = readSelectedParties();
    
    private final Integer id;
    private final PersonData personData;
    private final boolean selfNominated;
    private final String party;
    private final Map<String, Integer> signs;
//    private final LocalDate creatingDate;
    private final String status;
//    private LocalDate updatingDate;

    private static List<String> readSelectedParties() {
        Path selectedPartiesPath = Paths.get(SELECTED_PARTIES_FILE_PATH);
        try {
            return new ArrayList<>(Files.readAllLines(selectedPartiesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CheckResult checkRequestData() {
        CheckResult personDataCheckResult = personData.checkPersonData();
        if (!personDataCheckResult.result()) {
            return personDataCheckResult;
        }
        if (!selfNominated) {
            if (!selectedParties.contains(party)) {
                if (signs.values().stream().anyMatch(number -> number > 2500)) {
                    return new CheckResult(false, "Больше 2500 подписей в районе");
                }
                else if (signs.values().stream().mapToInt(Integer::intValue).sum() < 100000) {
                    return new CheckResult(false, "Меньше 100000 подписей в сумме");
                }
            }
        }
        else if (signs.values().stream().anyMatch(number -> number > 2500)) {
            return new CheckResult(false, "Больше 2500 подписей в районе");
        }
        else if (signs.values().stream().mapToInt(Integer::intValue).sum() < 300000) {
            return new CheckResult(false, "Меньше 300000 подписей в сумме");
        }
        return new CheckResult(true, null);
    }
}
