package com.whiskytangofox.ptbadiscordbot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;


@QuarkusMain
public class AppQuarkus implements QuarkusApplication {

    public static void main(String... args) {
        App.logger.info("************************************Running AppQuarkus.Main method");
        Quarkus.run(AppQuarkus.class);
    }

    public int run(String... args) throws Exception {
        App.logger.info("************************************Running QUARKUS.run method");
        App.mainApp(args);
        Quarkus.waitForExit();
        return 0;
    }
}
