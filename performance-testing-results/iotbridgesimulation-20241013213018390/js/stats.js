var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "40000",
        "ok": "30816",
        "ko": "9184"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "5",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "20244",
        "ok": "20244",
        "ko": "10006"
    },
    "meanResponseTime": {
        "total": "349",
        "ok": "423",
        "ko": "102"
    },
    "standardDeviation": {
        "total": "2007",
        "ok": "2215",
        "ko": "1001"
    },
    "percentiles1": {
        "total": "7",
        "ok": "8",
        "ko": "1"
    },
    "percentiles2": {
        "total": "9",
        "ok": "10",
        "ko": "1"
    },
    "percentiles3": {
        "total": "89",
        "ok": "216",
        "ko": "2"
    },
    "percentiles4": {
        "total": "13213",
        "ok": "14182",
        "ko": "10001"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 29453,
    "percentage": 73.63250000000001
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 68,
    "percentage": 0.16999999999999998
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 1295,
    "percentage": 3.2375000000000003
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 9184,
    "percentage": 22.96
},
    "meanNumberOfRequestsPerSecond": {
        "total": "165.98",
        "ok": "127.87",
        "ko": "38.11"
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
        "total": "40000",
        "ok": "30816",
        "ko": "9184"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "5",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "20244",
        "ok": "20244",
        "ko": "10006"
    },
    "meanResponseTime": {
        "total": "349",
        "ok": "423",
        "ko": "102"
    },
    "standardDeviation": {
        "total": "2007",
        "ok": "2215",
        "ko": "1001"
    },
    "percentiles1": {
        "total": "7",
        "ok": "8",
        "ko": "1"
    },
    "percentiles2": {
        "total": "9",
        "ok": "10",
        "ko": "1"
    },
    "percentiles3": {
        "total": "89",
        "ok": "216",
        "ko": "2"
    },
    "percentiles4": {
        "total": "13213",
        "ok": "14182",
        "ko": "10001"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 29453,
    "percentage": 73.63250000000001
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 68,
    "percentage": 0.16999999999999998
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 1295,
    "percentage": 3.2375000000000003
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 9184,
    "percentage": 22.96
},
    "meanNumberOfRequestsPerSecond": {
        "total": "165.98",
        "ok": "127.87",
        "ko": "38.11"
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
