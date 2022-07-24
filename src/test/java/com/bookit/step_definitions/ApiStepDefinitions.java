package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.awt.print.Book;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefinitions {

    String token;
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_bookit_api_using_and(String email, String password) {

        token = BookItApiUtil.generateToken(email, password);
        emailGlobal = email;


    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {

        response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when().get(ConfigurationReader.getProperty("url") + "/api/users/me");
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {

        Assert.assertEquals(statusCode, response.statusCode());

    }


    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        //we will compare database and API in this step

        //get info from db
        //connection is from hooks, and it will be ready

        String query = "select id,firstname,lastname,role from users\n" +
                "where email='" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);

        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        // int expectedId = (int) dbMap.get("id");
        String expectedRole = (String) dbMap.get("role");

        //get info from API

        JsonPath jsonPath = response.jsonPath();

        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        // int actualId = jsonPath.getInt("id");
        String actualRole = jsonPath.getString("role");

        //compare API and DB

        Assert.assertEquals(expectedFirstName, actualFirstName);
        Assert.assertEquals(expectedLastName, actualLastName);
        // Assert.assertEquals(expectedId,actualId);
        Assert.assertEquals(expectedRole, actualRole);
    }


    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() {

        //we will compare database and API in this step

        //get info from db
        //connection is from hooks, and it will be ready

        String query = "select id,firstname,lastname,role from users\n" +
                "where email='" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);

        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        // int expectedId = (int) dbMap.get("id");
        String expectedRole = (String) dbMap.get("role");

        //get info from API

        JsonPath jsonPath = response.jsonPath();

        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        // int actualId = jsonPath.getInt("id");
        String actualRole = jsonPath.getString("role");

        SelfPage selfPage = new SelfPage();
        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println(actualUIName);
        System.out.println(actualUIRole);

        //UI VS DB
        String expectedFullName = expectedFirstName + " " + expectedLastName;
        //verify ui fullName vs db fullName
        Assert.assertEquals(expectedFullName, actualUIName);
        Assert.assertEquals(expectedRole, actualUIRole);

        //UI vs API
        //Create a fullName for api

        String actualFullName = actualFirstName + " " + actualLastName;

        Assert.assertEquals(actualFullName, actualUIName);
        Assert.assertEquals(actualRole, actualUIRole);
    }


    @When("I send POST request to {string} endpoint with following information")
    public void i_send_post_request_to_endpoint_with_following_information(String path, Map<String, String> studentInfo) {

        studentEmail=studentInfo.get("email");
        studentPassword=studentInfo.get("password");

        response = given().accept(ContentType.JSON)
                .queryParams(studentInfo)
                .header("Authorization", token)
                .when().post(ConfigurationReader.getProperty("url") + path);


    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {

        BookItApiUtil.deleteStudent(studentEmail,studentPassword);


    }
}
