package bridge;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

public class IotBridgeSimulation extends Simulation {
    String baseUrl = System.getProperty("baseUrl", "https://localhost:8888");
    String apiKey = System.getProperty("apiKey", "$2a$10$td7Z9a43j7na6xVtAksCyOr11kJPlB3Z/sXXFtt4tAXE5kU1aRUye");
    String userId = System.getProperty("userId", "67195f4376a6e56d06d6cb3d");

    // Parse users and duration from system properties
    int users = Integer.parseInt(System.getProperty("users", "100")); // Increased number of users
    int duration = Integer.parseInt(System.getProperty("duration", "300")); // Increased duration

    // Define the HTTP protocol
    HttpProtocolBuilder httpProtocol = http.baseUrl(baseUrl)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Define the scenario
    ScenarioBuilder iotBridgeScenario = scenario("IoT Bridge Load Test Scenario")
            .exec(session -> {
                // Generate a random value for each request
                int randomValue = (int) (Math.random() * 100);
                String jsonBody = String.format("""
                            {
                                "measurement": "Iot Bridge Load Test",
                                "userId": "%s",
                                "value": %d,
                                "unit": "C"
                            }
                        """, userId, randomValue);

                // Store the JSON body in the session for the next request
                return session.set("jsonBody", jsonBody);
            })
            .exec(http("Post IoT Data")
                    .post("/v1/iot-bridge")
                    .header("x-api-key", apiKey)
                    .body(StringBody(session -> session.getString("jsonBody"))) // Use the stored JSON body directly
                    .check(status().is(202)) // Check for HTTP status 202
            )
            .pause(1);

    {
        // Set up the simulation with the user load
        setUp(iotBridgeScenario.injectOpen(
                rampUsers(users).during(Duration.ofSeconds(duration)), // Ramp users up over duration
                constantUsersPerSec((double) users / duration).during(Duration.ofSeconds(duration)) // Maintain user load
        ))
                .protocols(httpProtocol);
    }
}