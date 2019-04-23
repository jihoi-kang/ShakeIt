package com.example.kjh.shakeit;

import android.support.test.rule.ActivityTestRule;

import com.example.kjh.shakeit.beforelogin.activity.EmailLoginActivity;
import com.example.kjh.shakeit.utils.ToastMatcher;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class EmailLoginActivityTest {

    @Rule
    public ActivityTestRule<EmailLoginActivity> emailLoginActivityRule = new ActivityTestRule(EmailLoginActivity.class, true, true);

    @Test
    public void test_inputIncorrectEmail() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail"));

        onView(withId(R.id.emailLoginButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_email)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_inputIncorrectPassword() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("1234"));

        onView(withId(R.id.emailLoginButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_SuccessLogin() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("@Abcd1234"));

        onView(withId(R.id.emailLoginButton)).perform(click());

//        onView(withText(R.string.msg_for_success_login)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_SuccessFailure() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("@Abcd123"));

        onView(withId(R.id.emailLoginButton)).perform(click());

        onView(withText(R.string.msg_for_failure_login)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

}
