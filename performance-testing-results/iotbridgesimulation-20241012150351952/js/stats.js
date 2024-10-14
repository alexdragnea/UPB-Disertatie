var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "20000",
        "ok": "13299",
        "ko": "6701"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "7",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "35328",
        "ok": "35328",
        "ko": "10019"
    },
    "meanResponseTime": {
        "total": "1977",
        "ok": "2399",
        "ko": "1139"
    },
    "standardDeviation": {
        "total": "5388",
        "ok": "6168",
        "ko": "3175"
    },
    "percentiles1": {
        "total": "11",
        "ok": "15",
        "ko": "1"
    },
    "percentiles2": {
        "total": "62",
        "ok": "246",
        "ko": "2"
    },
    "percentiles3": {
        "total": "13694",
        "ok": "17542",
        "ko": "10002"
    },
    "percentiles4": {
        "total": "26072",
        "ok": "27554",
        "ko": "10003"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 10703,
    "percentage": 53.515
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 186,
    "percentage": 0.9299999999999999
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 2410,
    "percentage": 12.049999999999999
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 6701,
    "percentage": 33.505
},
    "meanNumberOfRequestsPerSecond": {
        "total": "82.99",
        "ok": "55.18",
        "ko": "27.8"
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
        "ok": "13299",
        "ko": "6701"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "7",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "35328",
        "ok": "35328",
        "ko": "10019"
    },
    "meanResponseTime": {
        "total": "1977",
        "ok": "2399",
        "ko": "1139"
    },
    "standardDeviation": {
        "total": "5388",
        "ok": "6168",
        "ko": "3175"
    },
    "percentiles1": {
        "total": "11",
        "ok": "15",
        "ko": "1"
    },
    "percentiles2": {
        "total": "62",
        "ok": "246",
        "ko": "2"
    },
    "percentiles3": {
        "total": "13694",
        "ok": "17542",
        "ko": "10002"
    },
    "percentiles4": {
        "total": "26072",
        "ok": "27554",
        "ko": "10003"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 10703,
    "percentage": 53.515
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 186,
    "percentage": 0.9299999999999999
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 2410,
    "percentage": 12.049999999999999
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 6701,
    "percentage": 33.505
},
    "meanNumberOfRequestsPerSecond": {
        "total": "82.99",
        "ok": "55.18",
        "ko": "27.8"
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
