package com.example.kjh.shakeit;

import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.friend.contract.ShakeContract;
import com.example.kjh.shakeit.main.friend.presenter.ShakePresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShakePresenterTest {

    @Mock
    private ShakeContract.View view;

    @Mock
    private ShakeContract.Model model;

    private ShakePresenter presenter;

    private InOrder inOrder;

    @Before
    public void setUp() {
        presenter = new ShakePresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_nonShaking_onSensorChaged_Success() {
        // 2.7F 이상일 때 흔들렸다고 판단
        float gForce = 2.8F;

        User user = mock(User.class);
        user.setUserId(1);

        when(view.getIsShaking()).thenReturn(false);

        presenter.onSensorChanged(gForce);

        verify(view).hideUserInfo();

        verify(view).executeVibrate();
        verify(view).setIsShaking(true);
        verify(view).executeThread();
    }

    @Test
    public void test_isShaking_onSensorChanged_Failure() {
        // 2.7F 이상일 때 흔들렸다고 판단
        float gForce = 2.8F;

        when(view.getIsShaking()).thenReturn(true);

        presenter.onSensorChanged(gForce);

        assertTrue(view.getIsShaking() == true);
    }

}
