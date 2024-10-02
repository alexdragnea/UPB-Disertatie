package bridge;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

public class IotBridgeSimulation extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "http://localhost:8888");
    String apiKey = System.getProperty("apiKey", "$2a$10$.38sm26c5GkmuqLXsUIkE.rXAmQJl0DusYGlP5Ve16gzp3TtcGB2O");
    String userId = System.getProperty("userId", "66fc5051dfdad708e6cf43cf");

    // Parse users and duration from system properties
    int users = Integer.parseInt(System.getProperty("users", "10"));
    int duration = Integer.parseInt(System.getProperty("duration", "10"));

    // Define the HTTP protocol
    HttpProtocolBuilder httpProtocol = http.baseUrl(baseUrl)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Define the scenario
    ScenarioBuilder iotBridgeScenario = scenario("IoT Bridge API Simulation")
            .exec(session -> {
                // Generate a random value for each request
                int randomValue = (int) (Math.random() * 100);
                String jsonBody = String.format("""
                            {
                                "measurement": "Iot Bridge Simulation",
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
                    .check(status().is(202)))
            .pause(1);

    {
        // Set up the simulation with the user load
        setUp(iotBridgeScenario.injectOpen(rampUsers(users).during(Duration.ofSeconds(duration))))
                .protocols(httpProtocol);
    }
}