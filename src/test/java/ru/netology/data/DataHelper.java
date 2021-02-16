package ru.netology.data;

import lombok.Value;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String login;
        private String code;
    }

    public static VerificationCode getVerificationCode(AuthInfo authInfo)  {
        val codeSQL = "SELECT code FROM auth_codes WHERE created = (SELECT max(created) FROM auth_codes);";
        val runner = new QueryRunner();
        String verificationCode="";
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            verificationCode = (String) runner.query(conn, codeSQL, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new VerificationCode(authInfo.getLogin(), verificationCode);
    }

    @Value
    public static class Transaction {
        private String from;
        private String to;
        private int amount;
    }

    public static Transaction getTransaction(String from, String to, int amount) {
        return new Transaction(from, to, amount);
    }

    public static void clearData() {
        val deleteCardTransactionSQL = "DELETE FROM card_transactions";
        val deleteAuthCodesSQL = "DELETE FROM auth_codes";
        val dataSQL = "UPDATE cards SET balance_in_kopecks=1000000";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.update(conn, deleteCardTransactionSQL);
            runner.update(conn, deleteAuthCodesSQL);
            runner.update(conn, dataSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
