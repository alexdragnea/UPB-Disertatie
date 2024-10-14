var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "20000",
        "ok": "14880",
        "ko": "5120"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "6",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "32562",
        "ok": "32562",
        "ko": "10010"
    },
    "meanResponseTime": {
        "total": "1253",
        "ok": "1439",
        "ko": "714"
    },
    "standardDeviation": {
        "total": "3933",
        "ok": "4287",
        "ko": "2573"
    },
    "percentiles1": {
        "total": "10",
        "ok": "12",
        "ko": "1"
    },
    "percentiles2": {
        "total": "31",
        "ok": "104",
        "ko": "1"
    },
    "percentiles3": {
        "total": "10001",
        "ok": "10177",
        "ko": "10001"
    },
    "percentiles4": {
        "total": "21246",
        "ok": "23481",
        "ko": "10002"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 12090,
    "percentage": 60.45
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 182,
    "percentage": 0.91
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 2608,
    "percentage": 13.04
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 5120,
    "percentage": 25.6
},
    "meanNumberOfRequestsPerSecond": {
        "total": "82.99",
        "ok": "61.74",
        "ko": "21.24"
    }
},
contents: {
"req_post-iot-data-466489404": {
        type: "REQUEST",
        name: "Post IoT Data",
path: "Post IoT Data",
pathFormatted: "req_post-iot-data-466489404",
stats: {
    "name": "Post IoT Data",
    "numberOfRequests": {
        "total": "20000",
        "ok": "14880",
        "ko": "5120"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "6",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "32562",
        "ok": "32562",
        "ko": "10010"
    },
    "meanResponseTime": {
        "total": "1253",
        "ok": "1439",
        "ko": "714"
    },
    "standardDeviation": {
        "total": "3933",
        "ok": "4287",
        "ko": "2573"
    },
    "percentiles1": {
        "total": "10",
        "ok": "12",
        "ko": "1"
    },
    "percentiles2": {
        "total": "31",
        "ok": "104",
        "ko": "1"
    },
    "percentiles3": {
        "total": "10001",
        "ok": "10177",
        "ko": "10001"
    },
    "percentiles4": {
        "total": "21246",
        "ok": "23481",
        "ko": "10002"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 12090,
    "percentage": 60.45
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 182,
    "percentage": 0.91
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 2608,
    "percentage": 13.04
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 5120,
    "percentage": 25.6
},
    "meanNumberOfRequestsPerSecond": {
        "total": "82.99",
        "ok": "61.74",
        "ko": "21.24"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
