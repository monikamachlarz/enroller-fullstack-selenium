package com.company.enroller.e2e.meetingsManagement;

import com.company.enroller.e2e.BaseTests;
import com.company.enroller.e2e.Const;
import com.company.enroller.e2e.authentication.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MeetingsTests extends BaseTests {

    WebDriver driver;
    MeetingsPage page;
    LoginPage loginPage;

    @BeforeEach
    void setup() {
        this.dbInit();
        this.driver = WebDriverManager.chromedriver().create();
        this.page = new MeetingsPage(driver);
        this.loginPage = new LoginPage(driver);
        this.page.get(Const.HOME_PAGE);
    }


    @Test
    @DisplayName("[SPOTKANIA.1] The meeting should be added to your meeting list. It should contain a title and description.")
    void addNewMeeting() {
        this.loginPage.loginAs(Const.USER_I_NAME);
        this.page.addNewMeeting(Const.MEETING_III_TITLE, Const.MEETING_DESC);
        // Asserts
        assertThat(this.page.getMeetingByTitle(Const.MEETING_III_TITLE)).isNotNull();
        assertThat(this.page.getMeetingByTitle(Const.MEETING_DESC).getText()).contains(Const.MEETING_DESC);
        assertThat(this.page.getMeetings()).hasSize(3);
    }

    @Test
    @DisplayName("[SPOTKANIA.2] User should not be able to add a meeting without a title.")
    void cannotAddMeetingWithoutTitle() {
        this.loginPage.loginAs(Const.USER_I_NAME);
        this.page.click(this.page.getAddNewMeetingBtn());
        this.page.getMeetingDescInput().sendKeys(Const.MEETING_DESC);
        this.page.click(this.page.getConfirmMeetingBtn());
        boolean isEnabled = this.page.getConfirmMeetingBtn().isEnabled();

        assertThat(isEnabled).isFalse();
    }

    @Test
    @DisplayName("[SPOTKANIA.3] User can join a meeting.")
    void userCanJoinMeeting() {
        this.loginPage.loginAs(Const.USER_III_NAME);
        this.page.addNewMeeting("Spotkanie do zapisania", "Zapisz się");
        this.page.click(this.page.getAddMeetingParticipantBtn());
        List<String> participants = this.page.getParticipantsListForMeeting("Spotkanie do zapisania");

        assertThat(participants).contains(Const.USER_I_NAME);
    }


    @Test
    @DisplayName("[SPOTKANIA.4] User can delete a meeting with no participants.")
    void userCanDeleteEmptyMeeting() {
        this.loginPage.loginAs(Const.USER_III_NAME);
        this.page.addNewMeeting("Puste spotkanie", "Bez uczestników");
        int before = this.page.getMeetings().size();
        this.page.click(this.page.getRemoveEmptyMeetingBtn());
        this.page.sleep(1);
        int after = this.page.getMeetings().size();

        assertThat(after).isEqualTo(before - 1);
    }

    @AfterEach
    void exit() {
        this.page.quit();
        this.removeAllMeeting();
    }

}
