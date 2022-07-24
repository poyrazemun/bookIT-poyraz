package com.bookit.utilities;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookItApiUtil {

    public static String generateToken(String email, String password) {


        Response response = given().accept(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("password", password)
                .when().get(ConfigurationReader.getProperty("url") + "/sign");

        String token = response.path("accessToken");

        return "Bearer " + token;

    }


    public static void deleteStudent(String studentEmail,String studentPassword){


        //send a get request to get token with student info
        String studentToken = BookItApiUtil.generateToken(studentEmail, studentPassword);

        //send a get request to /api/users/me endpoint and get the id number
        int idToDelete = given().accept(ContentType.JSON)
                .and().header("Authorization", studentToken)
                .when().get(ConfigurationReader.getProperty("url") + "/api/users/me")
                .then().statusCode(200)
                .extract().jsonPath().getInt("id");

        //send a delete request as a teacher to /api/students/{id} endpoint to delete the student
        String teacherToken = BookItApiUtil.generateToken(ConfigurationReader.getProperty("teacher_email"), ConfigurationReader.getProperty("teacher_password"));

        given().pathParam("id", idToDelete)
                .and().header("Authorization", teacherToken)
                .when()
                .delete(ConfigurationReader.getProperty("url") + "/api/students/{id}")
                .then().statusCode(204);








    }



}
