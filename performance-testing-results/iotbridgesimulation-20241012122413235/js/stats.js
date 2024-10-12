var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "10000",
        "ok": "8164",
        "ko": "1836"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "8",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "35929",
        "ok": "35929",
        "ko": "34867"
    },
    "meanResponseTime": {
        "total": "5236",
        "ok": "4981",
        "ko": "6373"
    },
    "standardDeviation": {
        "total": "6117",
        "ok": "6162",
        "ko": "5780"
    },
    "percentiles1": {
        "total": "3530",
        "ok": "2916",
        "ko": "10001"
    },
    "percentiles2": {
        "total": "9039",
        "ok": "8029",
        "ko": "10003"
    },
    "percentiles3": {
        "total": "18244",
        "ok": "18625",
        "ko": "12156"
    },
    "percentiles4": {
        "total": "26782",
        "ok": "27530",
        "ko": "23358"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 3150,
    "percentage": 31.5
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 195,
    "percentage": 1.95
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 4819,
    "percentage": 48.19
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 1836,
    "percentage": 18.360000000000003
},
    "meanNumberOfRequestsPerSecond": {
        "total": "41.49",
        "ok": "33.88",
        "ko": "7.62"
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
        "ok": "8164",
        "ko": "1836"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "8",
        "ko": "0"
    },
    "maxResponseTime": {
        "total": "35929",
        "ok": "35929",
        "ko": "34867"
    },
    "meanResponseTime": {
        "total": "5236",
        "ok": "4981",
        "ko": "6373"
    },
    "standardDeviation": {
        "total": "6117",
        "ok": "6162",
        "ko": "5780"
    },
    "percentiles1": {
        "total": "3530",
        "ok": "2916",
        "ko": "10001"
    },
    "percentiles2": {
        "total": "9039",
        "ok": "8029",
        "ko": "10003"
    },
    "percentiles3": {
        "total": "18244",
        "ok": "18625",
        "ko": "12156"
    },
    "percentiles4": {
        "total": "26782",
        "ok": "27530",
        "ko": "23358"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 3150,
    "percentage": 31.5
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 195,
    "percentage": 1.95
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 4819,
    "percentage": 48.19
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 1836,
    "percentage": 18.360000000000003
},
    "meanNumberOfRequestsPerSecond": {
        "total": "41.49",
        "ok": "33.88",
        "ko": "7.62"
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
