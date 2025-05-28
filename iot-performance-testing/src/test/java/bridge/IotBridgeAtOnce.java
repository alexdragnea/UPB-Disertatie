//package bridge;
//
//import static io.gatling.javaapi.core.CoreDsl.*;
//import static io.gatling.javaapi.http.HttpDsl.*;
//
//import io.gatling.javaapi.core.*;
//import io.gatling.javaapi.http.*;
//
//import java.time.Duration;
//
//public class IotBridgeAtOnce extends Simulation {
//
//    String baseUrl = System.getProperty("baseUrl", "https://localhost:8888");
//    String apiKey = System.getProperty("apiKey", "$2a$10$OLB3I1oFwlbyw6FHtrBhZO.D/pFBIyAGv/w2OaqVFjLmwLJBgLguO");
//
//    int users = Integer.parseInt(System.getProperty("users", "1000")); // Default: 1000
//    int duration = Integer.parseInt(System.getProperty("duration", "600")); // Default: 600 seconds
//
//    // Define the HTTP protocol
//    HttpProtocolBuilder httpProtocol = http.baseUrl(baseUrl)
//            .acceptHeader("application/json")
//            .contentTypeHeader("application/json");
//
//    // Define the scenario
//    ScenarioBuilder iotBridgeScenario = scenario("IoT Bridge Sustained Load Scenario")
//            .forever().on(
//                    exec(session -> {
//                        String jsonBody = """
//                    {
//                        "measurement": "reactivePerfTest",
//                        "userId": "68335a432fc15479d37a9b7c",
//                        "value": 30,
//                        "unit": "%"
//                    }
//                """;
//                        return session.set("jsonBody", jsonBody);
//                    })
//                            .exec(http("Post IoT Data")
//                                    .post("/v1/iot-bridge")
//                                    .header("x-api-key", apiKey)
//                                    .body(StringBody(session -> session.getString("jsonBody")))
//                                    .check(status().is(202))
//                            )
//                            .pause(1) // 1-second pause between each user's request
//            );
//
//    {
//        setUp(
//                iotBridgeScenario.injectOpen(
//                        constantUsersPerSec(users).during(Duration.ofSeconds(duration))
//                )
//        )
//                .protocols(httpProtocol)
//                .maxDuration(Duration.ofSeconds(duration)); // Total test time
//    }
//}
