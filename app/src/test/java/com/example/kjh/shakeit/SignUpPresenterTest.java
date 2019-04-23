package com.example.kjh.shakeit;

import com.example.kjh.shakeit.beforelogin.callback.ResultCallback;
import com.example.kjh.shakeit.beforelogin.contract.SignUpContract;
import com.example.kjh.shakeit.beforelogin.presenter.SignUpPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignUpPresenterTest {

    @Mock
    private SignUpContract.View view;

    @Mock
    private SignUpContract.Model model;

    private SignUpPresenter presenter;

    private InOrder inOrder;

    @Captor
    ArgumentCaptor<ResultCallback> callback;

    @Before
    public void setUp() {
        presenter = new SignUpPresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_enterIncorrectEmail_onClickSignUp_failure() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail");

        presenter.onClickSignUp();

        verify(view).showMessageForIncorrectEmail();
    }

    @Test
    public void test_enterIncorrectPassword_onClickSignUp_failure() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("123");

        presenter.onClickSignUp();

        verify(view).showMessageForIncorrectPassword();
    }

    @Test
    public void test_enterIncorrectPasswordAgain_onClickSignUp_failure() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("12345678");
        when(view.getInputPasswordAgain()).thenReturn("1234567");

        presenter.onClickSignUp();

        verify(view).showMessageForIncorrectPasswordAgain();
    }

    @Test
    public void test_enterIncorrectName_onClickSignUp_failure() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("12345678");
        when(view.getInputPasswordAgain()).thenReturn("12345678");
        when(view.getInputName()).thenReturn("강");

        presenter.onClickSignUp();

        verify(view).showMessageForIncorrectName();
    }

    @Test
    public void test_enterCorrectInput_onClickSignUp_Success() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("12345678");
        when(view.getInputPasswordAgain()).thenReturn("12345678");
        when(view.getInputName()).thenReturn("강지회");

        presenter.onClickSignUp();

        inOrder.verify(view).hideSoftKeyboard();
        inOrder.verify(view).showLoadingDialog();

        String email = view.getInputEmail();
        String password = view.getInputPassword();
        String name = view.getInputName();
        String successMsg = "success";

        verify(model).signUp(eq(email), eq(password), eq(name), callback.capture());
        callback.getValue().onSuccess(successMsg);

        inOrder.verify(view).hideLoadingDialog();
        inOrder.verify(view).showMessageForSuccessSignUp();
        inOrder.verify(view).finishActivity();
    }

    @Test
    public void test_enterCorrectInput_onClickSignUp_Failure_BecauseOfServerError() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.com");
        when(view.getInputPassword()).thenReturn("12345678");
        when(view.getInputPasswordAgain()).thenReturn("12345678");
        when(view.getInputName()).thenReturn("강지회");

        presenter.onClickSignUp();

        inOrder.verify(view).hideSoftKeyboard();
        inOrder.verify(view).showLoadingDialog();

        String email = view.getInputEmail();
        String password = view.getInputPassword();
        String name = view.getInputName();
        String errorMsg = "Server error";

        verify(model).signUp(eq(email), eq(password), eq(name), callback.capture());
        callback.getValue().onFailure(errorMsg);

        inOrder.verify(view).hideLoadingDialog();
        inOrder.verify(view).showMessageForFailureSignUp(errorMsg);
    }

}
