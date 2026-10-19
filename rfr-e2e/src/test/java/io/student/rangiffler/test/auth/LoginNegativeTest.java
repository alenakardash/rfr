package io.student.rangiffler.test.auth;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.student.rangiffler.data.TestUser;
import io.student.rangiffler.jupiter.annotation.User;
import io.student.rangiffler.ui.page.LandingPage;
import io.student.rangiffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright
@DisplayName("Вход с неверными учётными данными")
class LoginNegativeTest {
  private static final String BAD_CREDENTIALS = "Bad credentials";

  @Test
  @DisplayName("Существующий пользователь с неверным паролем видит Bad credentials")
  void wrongPasswordShowsBadCredentials(Page page, @User TestUser user) {
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(user.username(), RandomDataUtils.randomPassword())
        .clickSignInExpectingError()
        .checkThatErrorMessage(BAD_CREDENTIALS);
  }

  @Test
  @DisplayName("Несуществующий пользователь видит Bad credentials")
  void unknownUserShowsBadCredentials(Page page) {
    TestUser unknown = TestUser.unique();
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(unknown.username(), unknown.password())
        .clickSignInExpectingError()
        .checkThatErrorMessage(BAD_CREDENTIALS);
  }
}
