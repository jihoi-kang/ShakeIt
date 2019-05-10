package com.example.kjh.shakeit;

import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.main.chat.presenter.ChatPresenter;
import com.example.kjh.shakeit.netty.FutureListener;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.TimeManager;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatPresenterTest {

    @Mock
    private ChatContract.View view;

    @Mock
    private ChatContract.Model model;

    private ChatPresenter presenter;

    private InOrder inOrder;

    @Captor
    ArgumentCaptor<FutureListener> listener;

    @Before
    public void setUp() {
        presenter = new ChatPresenter(view, model);
        inOrder = inOrder(view, model);
    }

    @Test
    public void test_correctInputContent_onClickSend_Success() {
        User user = mock(User.class);
        ChatRoom room = mock(ChatRoom.class);
        when(view.getInputContent()).thenReturn("Hello World");
        when(view.getUser()).thenReturn(user);
        when(view.getChatRoom()).thenReturn(room);
        String time = TimeManager.nowTime();

        presenter.onClickSend();

        String body =
                Serializer.serialize(new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", view.getInputContent(), time));


        verify(model).sendMessage(eq(body), listener.capture());
        listener.getValue().success();

        inOrder.verify(view).clearInputContent();
    }

    @Test
    public void test_correctInputContent_onClickSend_Failure() {
        User user = mock(User.class);
        ChatRoom room = mock(ChatRoom.class);
        when(view.getInputContent()).thenReturn("Hello World");
        when(view.getUser()).thenReturn(user);
        when(view.getChatRoom()).thenReturn(room);
        String time = TimeManager.nowTime();

        presenter.onClickSend();

        String body =
                Serializer.serialize(new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", view.getInputContent(), time));


        verify(model).sendMessage(eq(body), listener.capture());
        listener.getValue().error();

        inOrder.verify(view).showMessageForFailure();
    }



}
