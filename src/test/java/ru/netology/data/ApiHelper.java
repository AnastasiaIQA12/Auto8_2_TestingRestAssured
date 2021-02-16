package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiHelper {
    private ApiHelper() {
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void postQueryUser(DataHelper.AuthInfo authInfo) {
        given()
                .spec(requestSpec)
                .body(authInfo)// передаём в теле объект, который будет преобразован в JSON
                .when() //когда
                .post("/api/auth") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static String getToken(DataHelper.VerificationCode verificationCode){
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
    return token;
    }

    public static Card[] getCard(String token) {
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
        return cards;
    }

    public static void madeTransfer(String token, String from, String to, int sum) {
        given() // "дано"
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(DataHelper.getTransaction(from, to, sum)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/transfer") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }
}
