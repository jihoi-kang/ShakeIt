package com.example.kjh.shakeit;

import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.more.contract.UpdateProfileContract;
import com.example.kjh.shakeit.main.more.presenter.UpdateProfilePresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProfilePresenterTest {

    @Mock
    private UpdateProfileContract.View view;

    @Mock
    private UpdateProfileContract.Model model;

    private UpdateProfilePresenter presenter;

    private InOrder inOrder;

    @Before
    public void setUp() {
        presenter = new UpdateProfilePresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_enterIncorrectName_hideUpdateText() {
        when(view.getInputName()).thenReturn("강");

        presenter.onChangedInput();

        verify(view).hideUpdateText();
    }

    @Test
    public void test_enterCorrectName_hideUpdateText_BecauseOfSameInputName() {
        User mockUserInfo = mock(User.class);

        when(view.getUser()).thenReturn(mockUserInfo);

        when(view.getInputName()).thenReturn("강지회");
        when(mockUserInfo.getName()).thenReturn("강지회");

        when(view.getInputStatusMessage()).thenReturn("abc");
        when(mockUserInfo.getStatusMessage()).thenReturn("abc");

        presenter.onChangedInput();

        verify(view).hideUpdateText();
    }

    @Test
    public void test_enterCorrectName_showUpdateText() {
        User mockUserInfo = mock(User.class);

        when(view.getUser()).thenReturn(mockUserInfo);

        when(view.getInputName()).thenReturn("강지회");
        when(mockUserInfo.getName()).thenReturn("강지");

        presenter.onChangedInput();

        verify(view).showUpdateText();
    }

    @Test
    public void test_updateStatusMessage_showUpdateText() {
        User mockUserInfo = mock(User.class);

        when(view.getUser()).thenReturn(mockUserInfo);

        when(view.getInputStatusMessage()).thenReturn("aaa");
        when(mockUserInfo.getStatusMessage()).thenReturn("bbb");

        when(view.getInputName()).thenReturn("강지회");
        when(mockUserInfo.getName()).thenReturn("강지회");

        presenter.onChangedInput();

        verify(view).showUpdateText();
    }

}
