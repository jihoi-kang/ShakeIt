package com.example.kjh.shakeit;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.main.friend.contract.AddFriendContract;
import com.example.kjh.shakeit.main.friend.presenter.AddFriendPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddFriendPresenterTest {

    @Mock
    private AddFriendContract.View view;

    @Mock
    private AddFriendContract.Model model;

    private AddFriendPresenter presenter;

    private InOrder inOrder;

    @Captor
    ArgumentCaptor<ResultCallback> callback;

    @Before
    public void setUp() {
        presenter = new AddFriendPresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_enterIncorrectEmail_onSearch_failure() {
        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail");

        presenter.onClickSearch();

        verify(view).showMessageForIncorrectEmail();
    }

//    model에서 NullPointException
//    @Test
//    public void test_enterCorrectEmail_onSearch_NoResult() {
//        User userInfo = mock(User.class);
//
//        when(view.getInputEmail()).thenReturn("jihoi.kang@gmail.cm");
//        when(userInfo.get_id()).thenReturn(2);
//
//        presenter.onClickSearch();
//
//        inOrder.verify(view).hideSoftKeyboard();
//
//        String email = view.getInputEmail();
//
//        verify(model).getUserByEmail(userInfo.get_id(), eq(email), callback.capture());
//        callback.getValue().onSuccess(null);
//
//        inOrder.verify(view).showMessageForNoResult();
//    }
//
//    @Test
//    public void test_enterCorrectEmail_onSearch_Success() {
//        User userInfo = mock(User.class);
//
//        when(view.getInputEmail()).thenReturn("jihoi@hoi.com");
//        when(userInfo.get_id()).thenReturn(2);
//
//        presenter.onClickSearch();
//
//        inOrder.verify(view).hideSoftKeyboard();
//
//        String email = view.getInputEmail();
//
//        verify(model).getUserByEmail(userInfo.get_id(), eq(email), callback.capture());
//        callback.getValue().onSuccess("");
//
//        inOrder.verify(view).showFriendInfo("");
//    }



}
