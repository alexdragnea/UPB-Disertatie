var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "10000",
        "ok": "8235",
        "ko": "1765"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "7",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "33146",
        "ok": "33146",
        "ko": "32511"
    },
    "meanResponseTime": {
        "total": "2455",
        "ok": "1327",
        "ko": "7718"
    },
    "standardDeviation": {
        "total": "5755",
        "ok": "5075",
        "ko": "5816"
    },
    "percentiles1": {
        "total": "25",
        "ok": "21",
        "ko": "10002"
    },
    "percentiles2": {
        "total": "314",
        "ok": "79",
        "ko": "10003"
    },
    "percentiles3": {
        "total": "15334",
        "ok": "13647",
        "ko": "16104"
    },
    "percentiles4": {
        "total": "26674",
        "ok": "26535",
        "ko": "29992"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 7513,
    "percentage": 75.13
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 122,
    "percentage": 1.22
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 600,
    "percentage": 6.0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 1765,
    "percentage": 17.65
},
    "meanNumberOfRequestsPerSecond": {
        "total": "41.49",
        "ok": "34.17",
        "ko": "7.32"
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
        "total": "10000",
        "ok": "8235",
        "ko": "1765"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "7",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "33146",
        "ok": "33146",
        "ko": "32511"
    },
    "meanResponseTime": {
        "total": "2455",
        "ok": "1327",
        "ko": "7718"
    },
    "standardDeviation": {
        "total": "5755",
        "ok": "5075",
        "ko": "5816"
    },
    "percentiles1": {
        "total": "25",
        "ok": "21",
        "ko": "10002"
    },
    "percentiles2": {
        "total": "314",
        "ok": "79",
        "ko": "10003"
    },
    "percentiles3": {
        "total": "15334",
        "ok": "13647",
        "ko": "16104"
    },
    "percentiles4": {
        "total": "26674",
        "ok": "26535",
        "ko": "29992"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 7513,
    "percentage": 75.13
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 122,
    "percentage": 1.22
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 600,
    "percentage": 6.0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 1765,
    "percentage": 17.65
},
    "meanNumberOfRequestsPerSecond": {
        "total": "41.49",
        "ok": "34.17",
        "ko": "7.32"
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
