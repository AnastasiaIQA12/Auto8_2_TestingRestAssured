package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.ApiHelper;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;
import lombok.val;

import static org.junit.jupiter.api.Assertions.*;


public class TransferTest {
    int balance1;
    int balance2;

    @BeforeEach
    void setUp()  {
        DataHelper.clearData();
    }

    @Test
    void shouldTransferMiddleOnFirstCard()  {
        int sum = 5000;
        val authInfo = DataHelper.getAuthInfo();
        ApiHelper.postQueryUser(authInfo);
        val verificationCode = DataHelper.getVerificationCode(authInfo);
        String token = ApiHelper.getToken(verificationCode);
        Card[] cards= ApiHelper.getCard(token);
        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());
        ApiHelper.madeTransfer(token, "5559 0000 0000 0002", "5559 0000 0000 0001", sum);
        Card[] cardsAfterTransfer =ApiHelper.getCard(token);
        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());
        assertEquals(15000, endBalance1);
        assertEquals(5000, endBalance2);
    }

    @Test
    void shouldTransferMiddleOnSecondCard()  {
        int sum = 5000;
        val authInfo = DataHelper.getAuthInfo();
        ApiHelper.postQueryUser(authInfo);
        val verificationCode = DataHelper.getVerificationCode(authInfo);
        String token = ApiHelper.getToken(verificationCode);
        Card[] cards= ApiHelper.getCard(token);
        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());
        ApiHelper.madeTransfer(token, "5559 0000 0000 0001", "5559 0000 0000 0002", sum);
        Card[] cardsAfterTransfer =ApiHelper.getCard(token);
        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());
        assertEquals(5000, endBalance1);
        assertEquals(15000, endBalance2);
    }

}
