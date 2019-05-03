package com.example.kjh.shakeit;

import com.example.kjh.shakeit.callback.ResultCallback;
import com.example.kjh.shakeit.login.contract.EmailLoginContract;
import com.example.kjh.shakeit.login.presenter.EmailLoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailLoginPresenterTest {

    @Mock
    private EmailLoginContract.View view;

    @Mock
    private EmailLoginContract.Model model;

    private EmailLoginPresenter presenter;

    private InOrder inOrder;

    @Captor
    ArgumentCaptor<ResultCallback> loginResultCallback;

    @Before
    public void setUp() {
        presenter = new EmailLoginPresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_enterIncorrectEmail_onClickLogin_failureLogin() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail");

        presenter.onClickEmailLogin();

        verify(view).showMessageForIncorrectEmail();
    }

    @Test
    public void test_enterIncorrectPassword_onClickLogin_failureLogin() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("123");

        presenter.onClickEmailLogin();

        verify(view).showMessageForIncorrectPassword();
    }

    @Test
    public void test_enterCorrectEmailAndPassword_onClickLogin_SuccessLogin() {
//        User mockUserInfo = mock(User.class);
        String UserInfo = "";

        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("@Abcd1234");

        presenter.onClickEmailLogin();

        inOrder.verify(view).hideSoftKeyboard();
        inOrder.verify(view).showLoadingDialog();

        String email = view.getInputEmail();
        String password = view.getInputPassword();

        verify(model).login(eq(email), eq(password), loginResultCallback.capture());
        loginResultCallback.getValue().onSuccess(UserInfo);

        inOrder.verify(view).hideLoadingDialog();
        inOrder.verify(view).showMessageForSuccessLoginAndFinishActivity(UserInfo);

    }

    @Test
    public void test_enterCorrectEmailAndPassword_onClickLogin_FailureLogin_BecauseOfServerError() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("@Abcd1234");

        String errorMsg = "Server Error Message";

        presenter.onClickEmailLogin();

        inOrder.verify(view).hideSoftKeyboard();
        inOrder.verify(view).showLoadingDialog();

        String email = view.getInputEmail();
        String password = view.getInputPassword();

        verify(model).login(eq(email), eq(password), loginResultCallback.capture());
        loginResultCallback.getValue().onFailure(errorMsg);

        inOrder.verify(view).hideLoadingDialog();
        inOrder.verify(view).showMessageForFailureLogin(errorMsg);
    }
}
