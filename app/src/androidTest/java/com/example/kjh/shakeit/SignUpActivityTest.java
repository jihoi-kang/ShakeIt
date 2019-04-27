package com.example.kjh.shakeit;

import android.support.test.rule.ActivityTestRule;

import com.example.kjh.shakeit.beforelogin.view.SignUpActivity;
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

public class SignUpActivityTest {

    @Rule
    public ActivityTestRule<SignUpActivity> signUpActivityRule = new ActivityTestRule(SignUpActivity.class, true, true);

    @Test
    public void test_inputIncorrectEmail() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
        onView(withId(R.id.inputName)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail"));

        onView(withId(R.id.signUpButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_email)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_inputIncorrectPassword() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
        onView(withId(R.id.inputName)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("1234"));

        onView(withId(R.id.signUpButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_inputIncorrectPasswordAgain() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
        onView(withId(R.id.inputName)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("@Abcd1234"));
        onView(withId(R.id.inputPasswordAgain)).perform(typeText("@Abcd123"));

        onView(withId(R.id.signUpButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_password_again)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_inputIncorrectName() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
        onView(withId(R.id.inputName)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi.kang@gmail.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("@Abcd1234"));
        onView(withId(R.id.inputPasswordAgain)).perform(typeText("@Abcd1234"));
        onView(withId(R.id.inputName)).perform(typeText("ka"));

        onView(withId(R.id.signUpButton)).perform(click());

        onView(withText(R.string.msg_for_incorrect_name)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    /** 계속해서 사용하려면 FakeModel을 추가해야함 */
//    @Test
//    public void test_SuccessSignUp() {
//        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
//        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
//        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
//        onView(withId(R.id.inputName)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.inputEmail)).perform(typeText("test28@gmail.com"));
//        onView(withId(R.id.inputPassword)).perform(typeText("@Abcd1234"));
//        onView(withId(R.id.inputPasswordAgain)).perform(typeText("@Abcd1234"));
//        onView(withId(R.id.inputName)).perform(typeText("KangJiHoi"));
//
//        onView(withId(R.id.signUpButton)).perform(click());
//
////        onView(withText(R.string.msg_for_success_signUp)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
//    }

    @Test
    public void test_FailureSignUp() {
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPasswordAgain)).check(matches(isDisplayed()));
        onView(withId(R.id.inputName)).check(matches(isDisplayed()));

        onView(withId(R.id.inputEmail)).perform(typeText("jihoi@hoi.com"));
        onView(withId(R.id.inputPassword)).perform(typeText("password"));
        onView(withId(R.id.inputPasswordAgain)).perform(typeText("password"));
        onView(withId(R.id.inputName)).perform(typeText("jihoi"));

        onView(withId(R.id.signUpButton)).perform(click());

        onView(withText(R.string.msg_for_failure_signUp_becauseOfOverlapEmail)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}
