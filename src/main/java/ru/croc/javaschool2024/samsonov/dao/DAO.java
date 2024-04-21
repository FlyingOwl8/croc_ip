package ru.croc.javaschool2024.samsonov.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import ru.croc.javaschool2024.samsonov.check_result.CheckResult;
import ru.croc.javaschool2024.samsonov.dto.Candidate;
import ru.croc.javaschool2024.samsonov.dto.PersonData;
import ru.croc.javaschool2024.samsonov.dto.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.croc.javaschool2024.samsonov.db_consts.DBConstants.*;

@RequiredArgsConstructor
public class DAO {
    private final Connection connection;

    public void createRequest(Request request) {
        String sqlInsertPersonData = "INSERT INTO " + PERSON_DATA_TABLE_NAME + " (full_name, passport, age, " +
                "foreign_citizenship, education, convicted) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlInsertRequest = "INSERT INTO " + REQUESTS_TABLE_NAME + " (person_id, self_nominated, party, " +
                "signs, status) VALUES (?, ?, ?, ?, ?)";
        String sqlCheckIfPersonExists = "SELECT id FROM " + PERSON_DATA_TABLE_NAME + " WHERE passport = ?";

        PersonData personData = request.getPersonData();
        int personId = -5;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfPersonExists)) {
            preparedStatement.setString(1, personData.getPassport());
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    personId = result.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (personId < 0) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertPersonData,
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, personData.getFullName());
                preparedStatement.setString(2, personData.getPassport());
                preparedStatement.setInt(3, personData.getAge());
                preparedStatement.setBoolean(4, personData.isForeignCitizenship());
                preparedStatement.setString(5, personData.getEducationDocument());
                preparedStatement.setBoolean(6, personData.isConvicted());
                preparedStatement.executeUpdate();
                try (ResultSet result = preparedStatement.getGeneratedKeys()) {
                    result.next();
                    personId = result.getInt(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        String sqlCheckIfRequestExists = "SELECT id FROM " + REQUESTS_TABLE_NAME + " WHERE person_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfRequestExists)) {
            preparedStatement.setInt(1, personId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    throw new IllegalStateException("request already");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sqlCheckIfPartyExists = "SELECT id FROM " + REQUESTS_TABLE_NAME + " WHERE party = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfPartyExists)) {
            preparedStatement.setString(1, request.getParty());
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    throw new IllegalStateException("party already");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertRequest)) {
            preparedStatement.setInt(1, personId);
            preparedStatement.setBoolean(2, request.isSelfNominated());
            if (!request.isSelfNominated()) {
                preparedStatement.setString(3, request.getParty());
            }
            else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                preparedStatement.setString(4, ow.writeValueAsString(request.getSigns()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            preparedStatement.setString(5, "CREATED");
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Request getRequestByPassport(String passport) {
        String sqlSelectDataByPassport =
                "SELECT " + REQUESTS_TABLE_NAME + ".id, person_id, self_nominated, party, signs, status, " +
                        "full_name, passport, age, foreign_citizenship, education, convicted " +
                        "FROM " + REQUESTS_TABLE_NAME + " JOIN " + PERSON_DATA_TABLE_NAME +
                        " ON " + REQUESTS_TABLE_NAME + ".person_id = " + PERSON_DATA_TABLE_NAME + ".id " +
                        "WHERE passport = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectDataByPassport)) {
            preparedStatement.setString(1, passport);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return new Request(
                            result.getInt("id"),
                            new PersonData(
                                    result.getInt("person_id"),
                                    result.getString("full_name"),
                                    result.getString("passport"),
                                    result.getInt("age"),
                                    result.getBoolean("foreign_citizenship"),
                                    result.getString("education"),
                                    result.getBoolean("convicted")
                            ),
                            result.getBoolean("self_nominated"),
                            result.getString("party"),
                            objectMapper.readValue(result.getString("signs"), new TypeReference<>() {}),
                            result.getString("status")
                    );
                }
                else {
                    throw new IllegalStateException("no");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PersonData getPersonDataById(int id) {
        String sqlSelectDataById =
                "SELECT full_name, passport, age, foreign_citizenship, education, convicted " +
                        "FROM " + PERSON_DATA_TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectDataById)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    return new PersonData(
                            id,
                            result.getString("full_name"),
                            result.getString("passport"),
                            result.getInt("age"),
                            result.getBoolean("foreign_citizenship"),
                            result.getString("education"),
                            result.getBoolean("convicted")
                    );
                }
                else {
                    throw new IllegalStateException(String.format("Выдвиженец с ID %d не найден", id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRequest(Integer id) {
        String sqlDeleteRequest = "DELETE FROM " + REQUESTS_TABLE_NAME + " WHERE id = ?";
        String sqlCheckIfExists = "SELECT * FROM " + REQUESTS_TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfExists)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalStateException(String.format("Заявка с ID %d не найдена", id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteRequest)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Request> getAllRequests() {
        String sqlSelectDataByPassport =
                "SELECT id, person_id, self_nominated, party, signs, status, full_name, passport, age, " +
                        "foreign_citizenship, education, convicted FROM " + REQUESTS_TABLE_NAME +
                        " JOIN " + PERSON_DATA_TABLE_NAME +
                        " ON " + REQUESTS_TABLE_NAME + ".person_id = " + PERSON_DATA_TABLE_NAME + ".id ";
        List<Request> requestList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectDataByPassport)) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                ObjectMapper objectMapper = new ObjectMapper();
                while (result.next()) {
                    Request request = new Request(
                            result.getInt("id"),
                            new PersonData(
                                    result.getInt("person_id"),
                                    result.getString("full_name"),
                                    result.getString("passport"),
                                    result.getInt("age"),
                                    result.getBoolean("foreign_citizenship"),
                                    result.getString("education"),
                                    result.getBoolean("convicted")
                            ),
                            result.getBoolean("self_nominated"),
                            result.getString("party"),
                            objectMapper.readValue(result.getString("signs"), new TypeReference<>() {}),
                            result.getString("status")
                    );
                    requestList.add(request);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return requestList;
    }

    public void checkRequestAndUpdateStatus(Integer id) {
        String sqlSelectDataById =
                "SELECT person_id, self_nominated, party, signs, status, full_name, passport, age, " +
                        "foreign_citizenship, education, convicted FROM " + REQUESTS_TABLE_NAME +
                        " JOIN " + PERSON_DATA_TABLE_NAME +
                        " ON " + REQUESTS_TABLE_NAME + ".person_id = " + PERSON_DATA_TABLE_NAME + ".id " +
                        "WHERE " + REQUESTS_TABLE_NAME + ".id = ?";
        Request request = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectDataById)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                ObjectMapper objectMapper = new ObjectMapper();
                while (result.next()) {
                    request = new Request(
                            id,
                            new PersonData(
                                    result.getInt("person_id"),
                                    result.getString("full_name"),
                                    result.getString("passport"),
                                    result.getInt("age"),
                                    result.getBoolean("foreign_citizenship"),
                                    result.getString("education"),
                                    result.getBoolean("convicted")
                            ),
                            result.getBoolean("self_nominated"),
                            result.getString("party"),
                            objectMapper.readValue(result.getString("signs"), new TypeReference<>() {}),
                            result.getString("status")
                    );
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (request == null) {
            throw new IllegalStateException(String.format("Заявка с ID %d не найдена", id));
        }
        else if (!Objects.equals(request.getStatus(), "CREATED")) {
            throw new IllegalStateException(String.format("Заявка с ID %d уже проверена", id));
        }

        String sqlUpdateRequest = "UPDATE " + REQUESTS_TABLE_NAME + " SET status = ? WHERE id = ?";
        String status;
        CheckResult checkResult = request.checkRequestData();
        if (checkResult.result()) {
            status = "ACCEPTED";
            addRegisteredCandidate(id);
        }
        else {
            status = "REFUSED";
            addDeclinedRequest(id, checkResult.message());
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateRequest)) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRegisteredCandidate(int requestId) {
        String sqlInsertClient = "INSERT INTO " + CANDIDATES_TABLE_NAME + " (request_id) VALUES (?)";
        String sqlCheckIfExists = "SELECT * FROM " + CANDIDATES_TABLE_NAME + " WHERE request_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfExists)) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    throw new IllegalStateException(
                            String.format("Кандидат по заявке %d уже зарегистрирован", requestId));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertClient)) {
            preparedStatement.setInt(1, requestId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDeclinedRequest(int requestId, String refusalReason) {
        String sqlInsertClient = "INSERT INTO " + REFUSALS_TABLE_NAME + " VALUES (?, ?)";
        String sqlCheckIfExists = "SELECT * FROM " + REFUSALS_TABLE_NAME + " WHERE request_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfExists)) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    throw new IllegalStateException(
                            String.format("Причина отказа по заявке %d уже записана", requestId));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertClient)) {
            preparedStatement.setInt(1, requestId);
            preparedStatement.setString(2, refusalReason);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRefusalReasonByRequestId(int requestId) {
        String sqlSelectDataById =
                "SELECT refusal_reason FROM " + REFUSALS_TABLE_NAME + " WHERE request_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectDataById)) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    return result.getString("refusal_reason");
                }
                else {
                    throw new IllegalStateException(
                            String.format("Причина отказа по заявке с ID %d не найдена", requestId));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkAllUncheckedRequestsAndUpdateStatus() {
        String sqlSelectData =
                "SELECT " + REQUESTS_TABLE_NAME + ".id FROM " + REQUESTS_TABLE_NAME +
                        " JOIN " + PERSON_DATA_TABLE_NAME +
                        " ON " + REQUESTS_TABLE_NAME + ".person_id = " + PERSON_DATA_TABLE_NAME + ".id " +
                        "WHERE " + REQUESTS_TABLE_NAME + ".status = 'CREATED'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectData)) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    checkRequestAndUpdateStatus(result.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Candidate> getRegisteredCandidates() {
        String sqlSelectData =
                "SELECT " + REQUESTS_TABLE_NAME + ".id, request_id, person_id, self_nominated, party, signs " +
                        "FROM " + CANDIDATES_TABLE_NAME +
                        " JOIN " + REQUESTS_TABLE_NAME +
                        " ON " + CANDIDATES_TABLE_NAME + ".request_id = " + REQUESTS_TABLE_NAME + ".id ";
        List<Candidate> candidateList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectData)) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                ObjectMapper objectMapper = new ObjectMapper();
                while (result.next()) {
                     Candidate candidate = new Candidate(
                             result.getInt("id"),
                             result.getInt("request_id"),
                             result.getInt("person_id"),
                             result.getBoolean("self_nominated"),
                             result.getString("party"),
                             objectMapper.readValue(result.getString("signs"), new TypeReference<>() {})
                    );
                    candidateList.add(candidate);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return candidateList;
    }

    public int getCandidateIdByPassport(String passport) {
        Request request = getRequestByPassport(passport);

        String sqlSelectData =
                "SELECT " + CANDIDATES_TABLE_NAME + ".candidate_id FROM " + CANDIDATES_TABLE_NAME +
                        " JOIN " + REQUESTS_TABLE_NAME +
                        " ON " + CANDIDATES_TABLE_NAME + ".request_id = " + REQUESTS_TABLE_NAME + ".id " +
                        "WHERE " + REQUESTS_TABLE_NAME + ".id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectData)) {
            preparedStatement.setInt(1, request.getId());
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("candidate_id");
                }
                else {
                    throw new IllegalStateException("no");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCandidate(Integer id) {
        String sqlDeleteRequest = "DELETE FROM " + CANDIDATES_TABLE_NAME + " WHERE id = ?";
        String sqlCheckIfExists = "SELECT * FROM " + CANDIDATES_TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheckIfExists)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalStateException(String.format("Кандидат с ID %d не найден", id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteRequest)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}