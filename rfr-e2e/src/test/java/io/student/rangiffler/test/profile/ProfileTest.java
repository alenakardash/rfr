package io.student.rangiffler.test.profile;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.student.rangiffler.data.TestUser;
import io.student.rangiffler.jupiter.annotation.User;
import io.student.rangiffler.ui.page.LandingPage;
import io.student.rangiffler.utils.RandomDataUtils;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright
@DisplayName("Профиль пользователя")
class ProfileTest {
  private static final String DENMARK = "Denmark";
  private static final String FRANCE = "France";
  private static final Path AVATAR = Path.of("src/test/resources/img/avatar.png");
  private static final String PROFILE_UPDATED = "Your profile is successfully updated";

  @Test
  @DisplayName("Имя, фамилия, страна и аватар сохраняются после обновления страницы")
  void profileChangesPersistAfterReload(Page page, @User TestUser user) {
    String firstName = RandomDataUtils.randomName();
    String surname = RandomDataUtils.randomName();
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(user.username(), user.password())
        .clickSignIn()
        .clickProfile()
        .fillProfileForm(firstName, surname, DENMARK)
        .uploadAvatar(AVATAR)
        .clickSave()
        .checkThatAlertMessage(PROFILE_UPDATED)
        .reload()
        .checkThatUsername(user.username())
        .checkThatProfile(firstName, surname, DENMARK)
        .checkThatAvatar(AVATAR);
  }

  @Test
  @DisplayName("Reset возвращает сохранённые имя, фамилию, страну и аватар")
  void resetRestoresSavedProfile(Page page, @User TestUser user) {
    String savedFirstName = RandomDataUtils.randomName();
    String savedSurname = RandomDataUtils.randomName();
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(user.username(), user.password())
        .clickSignIn()
        .clickProfile()
        .fillProfileForm(savedFirstName, savedSurname, DENMARK)
        .clickSave()
        .checkThatAlertMessage(PROFILE_UPDATED)
        .fillProfileForm(RandomDataUtils.randomName(), RandomDataUtils.randomName(), FRANCE)
        .uploadAvatar(AVATAR)
        .checkThatAvatar(AVATAR)
        .clickReset()
        .checkThatProfile(savedFirstName, savedSurname, DENMARK)
        .checkThatAvatarIsEmpty();
  }
}
