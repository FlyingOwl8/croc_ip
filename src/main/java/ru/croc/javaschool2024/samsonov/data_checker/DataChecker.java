package ru.croc.javaschool2024.samsonov.data_checker;

import ru.croc.javaschool2024.samsonov.data_checker.check_result.CheckResult;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.SELECTED_PARTIES_FILE_PATH;

//класс проверки данных для регистрации
public class DataChecker {
    private static final Set<String> selectedParties = readSelectedParties();

    private DataChecker() {}

    private static Set<String> readSelectedParties() {
        Path selectedPartiesPath = Paths.get(SELECTED_PARTIES_FILE_PATH);
        try {
            return new HashSet<>(Files.readAllLines(selectedPartiesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //проверить личные данные
    public static CheckResult checkPersonData(PersonData personData) {
        if (personData.getAge() < 35) {
            return new CheckResult(false, "Возраст меньше 35 лет");
        }
        if (personData.isForeignCitizenship()) {
            return new CheckResult(false, "Имеет гражданство другой страны");
        }
        if (personData.isConvicted()) {
            return new CheckResult(false, "Имеет судимость");
        }
        return new CheckResult(true, null);
    }

    //проверить данные заявки
    public static CheckResult checkRequest(Request request) {
        CheckResult personDataCheckResult = checkPersonData(request.getPersonData());
        if (!personDataCheckResult.result()) {
            return personDataCheckResult;
        }
        if (!request.isSelfNominated()) {
            if (!selectedParties.contains(request.getParty())) {
                if (request.getSigns().values().stream().anyMatch(number -> number > 2500)) {
                    return new CheckResult(false, "Больше 2500 подписей в районе");
                }
                else if (request.getSigns().values().stream().mapToInt(Integer::intValue).sum() < 100000) {
                    return new CheckResult(false, "Меньше 100000 подписей в сумме");
                }
            }
        }
        else if (request.getSigns().values().stream().anyMatch(number -> number > 2500)) {
            return new CheckResult(false, "Больше 2500 подписей в районе");
        }
        else if (request.getSigns().values().stream().mapToInt(Integer::intValue).sum() < 300000) {
            return new CheckResult(false, "Меньше 300000 подписей в сумме");
        }
        return new CheckResult(true, null);
    }
}
