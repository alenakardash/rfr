package io.student.rangiffler.ui.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import io.student.rangiffler.config.TestConfig;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class ProfilePage {
  public static final String URL = TestConfig.frontUrl() + "/profile";
  private final Page page;
  private final Locator avatarInput;
  private final Locator emptyAvatar;
  private final Locator avatar;
  private final Locator usernameInput;
  private final Locator firstNameInput;
  private final Locator surnameInput;
  private final Locator location;
  private final Locator locationOptions;
  private final Locator save;
  private final Locator reset;
  private final Locator alert;

  public ProfilePage(Page page) {
    this.page = page;
    this.avatarInput = page.getByLabel("Update avatar", new Page.GetByLabelOptions().setExact(true));
    this.emptyAvatar = page.getByRole(AriaRole.MAIN).getByTestId("PersonIcon");
    this.avatar = page.getByRole(AriaRole.MAIN).getByRole(AriaRole.IMG,
        new Locator.GetByRoleOptions().setName(Pattern.compile("^$")));
    this.usernameInput = page.getByLabel("Username", new Page.GetByLabelOptions().setExact(true));
    this.firstNameInput = page.getByLabel("First Name", new Page.GetByLabelOptions().setExact(true));
    this.surnameInput = page.getByLabel("Surname", new Page.GetByLabelOptions().setExact(true));
    this.location = page.getByRole(AriaRole.COMBOBOX,
        new Page.GetByRoleOptions().setName("Location").setExact(true));
    this.locationOptions = page.getByRole(AriaRole.LISTBOX,
        new Page.GetByRoleOptions().setName("Location").setExact(true)).getByRole(AriaRole.OPTION);
    this.save = page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Save").setExact(true));
    this.reset = page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Reset").setExact(true));
    this.alert = page.getByRole(AriaRole.ALERT);
  }

  @Step("Открыть профиль")
  public ProfilePage open() {
    page.navigate(URL);
    return this;
  }

  @Step("Перезагрузить страницу профиля")
  public ProfilePage reload() {
    page.reload();
    return new ProfilePage(page);
  }

  @Step("Загрузить аватар: {file}")
  public ProfilePage uploadAvatar(Path file) {
    avatarInput.setInputFiles(file);
    return this;
  }

  @Step("Заполнить форму профиля: {firstName} / {surname} / {country}")
  public ProfilePage fillProfileForm(String firstName, String surname, String country) {
    firstNameInput.fill(firstName);
    surnameInput.fill(surname);
    location.click();
    locationOptions.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.IMG,
        new Page.GetByRoleOptions().setName(country).setExact(true)))).click();
    return this;
  }

  @Step("Нажать Save")
  public ProfilePage clickSave() {
    page.waitForResponse(ProfilePage::isUpdateUser, save::click);
    return this;
  }

  @Step("Нажать Reset")
  public ProfilePage clickReset() {
    reset.click();
    return this;
  }

  @Step("Проверить сообщение о сохранении профиля: {message}")
  public ProfilePage checkThatAlertMessage(String message) {
    assertThat(alert).hasText(message);
    return this;
  }

  @Step("Проверить имя пользователя в профиле: {username}")
  public ProfilePage checkThatUsername(String username) {
    assertThat(usernameInput).hasValue(username);
    return this;
  }

  @Step("Проверить данные профиля: {firstName} / {surname} / {country}")
  public ProfilePage checkThatProfile(String firstName, String surname, String country) {
    assertThat(firstNameInput).hasValue(firstName);
    assertThat(surnameInput).hasValue(surname);
    assertThat(location).hasText(country);
    return this;
  }

  @Step("Проверить, что аватар не загружен")
  public ProfilePage checkThatAvatarIsEmpty() {
    assertThat(emptyAvatar).isVisible();
    return this;
  }

  @Step("Проверить аватар: {file}")
  public ProfilePage checkThatAvatar(Path file) {
    assertThat(avatar).hasAttribute("src", "data:image/png;base64," + base64(file));
    return this;
  }

  private static String base64(Path file) {
    try {
      return Base64.getEncoder().encodeToString(Files.readAllBytes(file));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static boolean isUpdateUser(Response response) {
    String body = response.request().postData();
    return response.url().endsWith("/graphql") && body != null && body.contains("\"operationName\":\"UpdateUser\"");
  }
}
