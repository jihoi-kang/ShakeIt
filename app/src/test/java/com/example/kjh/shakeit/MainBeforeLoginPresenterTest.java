package com.example.kjh.shakeit;

import com.example.kjh.shakeit.beforelogin.contract.MainContract;
import com.example.kjh.shakeit.beforelogin.presenter.MainPresenter;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class MainBeforeLoginPresenterTest {

    @Mock
    private MainContract.View view;

    @Mock
    private MainContract.Model model;

    private MainPresenter presenter;

    private InOrder inOrder;

    @Before
    public void setUp() {
        presenter = new MainPresenter(view, model);
        inOrder = inOrder(view, model);
    }



}
