package ru.netology.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


public class TransferTest {
    private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    int balance1;
    int balance2;

    @BeforeEach
    void setUp() throws SQLException {
        val deleteCardTransactionSQL = "DELETE FROM card_transactions";
        val deleteAuthCodesSQL = "DELETE FROM auth_codes";
        val dataSQL = "UPDATE cards SET balance_in_kopecks=1000000";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.update(conn, deleteCardTransactionSQL);
            runner.update(conn,deleteAuthCodesSQL);
            runner.update(conn, dataSQL);
        }

    }

    @Test
    void shouldTransferMiddleOnFirstCard() throws SQLException {
        int sum = 5000;
        val authInfo = DataHelper.getAuthInfo();
        // Given - When - Then
        // Предусловия
        given()
                .spec(requestSpec)
                .body(authInfo)// передаём в теле объект, который будет преобразован в JSON
                .when() //когда
                .post("/api/auth") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        val verificationCode = DataHelper.getVerificationCode(authInfo);

        String token =
                given() // "дано"
                        .spec(requestSpec)
                        .body(verificationCode) // передаём в теле объект, который будет преобразован в JSON
                        .when() // "когда"
                        .post("/api/auth/verification") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .path("token");

        Card[] cards =
                given() // "дано"
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());

        given() // "дано"
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(DataHelper.getTransaction("5559 0000 0000 0002", "5559 0000 0000 0001", sum)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/transfer") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        Card[] cardsAfterTransfer =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());

        assertEquals(15000, endBalance1);
        assertEquals(5000, endBalance2);
    }

    @Test
    void shouldTransferMiddleOnSecondCard() throws SQLException {
        int sum = 5000;
        val authInfo = DataHelper.getAuthInfo();
        // Given - When - Then
        // Предусловия
        given()
                .spec(requestSpec)
                .body(authInfo)// передаём в теле объект, который будет преобразован в JSON
                .when() //когда
                .post("/api/auth") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        val verificationCode = DataHelper.getVerificationCode(authInfo);

        String token =
                given() // "дано"
                        .spec(requestSpec)
                        .body(verificationCode) // передаём в теле объект, который будет преобразован в JSON
                        .when() // "когда"
                        .post("/api/auth/verification") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .path("token");

        Card[] cards =
                given() // "дано"
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());

        given() // "дано"
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(DataHelper.getTransaction("5559 0000 0000 0001", "5559 0000 0000 0002", sum)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/transfer") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        Card[] cardsAfterTransfer =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());

        assertEquals(5000, endBalance1);
        assertEquals(15000, endBalance2);
    }

}
