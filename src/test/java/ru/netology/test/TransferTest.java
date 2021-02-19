package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.ApiHelper;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;
import lombok.val;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DataHelper.*;

public class TransferTest {
    int balance1;
    int balance2;
    String numberCard1;
    String numberCard2;

    @BeforeEach
    void setUp()  {
        clearData();
    }

    @Test
    void shouldTransferMiddleOnFirstCard()  {
        int sum = 5000;
        val authInfo = getAuthInfo();
        ApiHelper.postQueryUser(authInfo);
        val verificationCode = getVerificationCode(authInfo);
        String token = ApiHelper.getToken(verificationCode);
        Card[] cards= ApiHelper.getCard(token);
        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());
        numberCard1= DataHelper.getFirstCard().getNumber();
        numberCard2= DataHelper.getSecondCard().getNumber();
        ApiHelper.madeTransfer(token, numberCard2, numberCard1, sum);
        Card[] cardsAfterTransfer =ApiHelper.getCard(token);
        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());
        assertEquals(balance1+sum, endBalance1);
        assertEquals(balance2-sum, endBalance2);
    }

    @Test
    void shouldTransferMiddleOnSecondCard()  {
        int sum = 5000;
        val authInfo = getAuthInfo();
        ApiHelper.postQueryUser(authInfo);
        val verificationCode = getVerificationCode(authInfo);
        String token = ApiHelper.getToken(verificationCode);
        Card[] cards= ApiHelper.getCard(token);
        balance1 = Integer.parseInt(cards[0].getBalance());
        balance2 = Integer.parseInt(cards[1].getBalance());
        numberCard1= DataHelper.getFirstCard().getNumber();
        numberCard2= DataHelper.getSecondCard().getNumber();
        ApiHelper.madeTransfer(token, numberCard1, numberCard2, sum);
        Card[] cardsAfterTransfer =ApiHelper.getCard(token);
        int endBalance1 = Integer.parseInt(cardsAfterTransfer[0].getBalance());
        int endBalance2 = Integer.parseInt(cardsAfterTransfer[1].getBalance());
        assertEquals(balance1-sum, endBalance1);
        assertEquals(balance2+sum, endBalance2);
    }

}
