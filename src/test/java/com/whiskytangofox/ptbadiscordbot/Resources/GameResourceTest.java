package com.whiskytangofox.ptbadiscordbot.Resources;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class GameResourceTest {



   @Test
    public void testGameResourceCommand(){
        String sheetID = "test1";
        String author = "Test2";
        String command = "test3";
        given()
            .when()
                .get("/game/sheet/whiskytangofox/hello world")
            .then()
                .statusCode(200);
    }

}