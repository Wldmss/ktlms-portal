/**** D3 v4 ****/

function comma(data) {
    return data;
}

function null2Zero(val) {
    return (val == undefined || val == null) ? 0 : Number(val);
}

/**** 원형차트 ****/
var wrPieV4 = function (obj, param) {

    var easeType = "exp";
    var duration = 1000;

    var originR;
    var originWidth;

    var width = 300, //width
        r = 100, //radius
        ir = 30,
        labelField = "label",
        valueField = "value",
        data;

    var tots;

    var color = [
        "#424242",
        "#2E79DE",
        "#FEC64B",
        "#73C16B",
        "#419FDA",
        "#125498"
    ];


    var fontSize = 14;
    var unit = "";
    var innerRadius;
    var showType = "value"; //value, percent, gun(2022/10/12 추가. "건")

    var showText = false;        //true, false
    var showTooltip = false;     //true, false

    this.setData = function (d) {
        data = d
        if (param != undefined) {
            for (var col in param) {
                if (param[col] != undefined) {
                    /** 사용금지
                     eval(col + "=param[col]");
                     console.log(col + '=' + param[col]);
                     **/
                    if ('color' == col) {
                        color = param[col];
                    } else if ('data' == col) {
                        data = param[col];
                    } else if ('fontSize' == col) {
                        fontSize = param[col];
                    } else if ('innerRadius' == col) {
                        innerRadius = param[col];
                    } else if ('labelField' == col) {
                        labelField = param[col];
                    } else if ('r' == col) {
                        r = param[col];
                    } else if ('showType' == col) {
                        showType = param[col];
                    } else if ('unit' == col) {
                        unit = param[col];
                    } else if ('valueField' == col) {
                        valueField = param[col];
                    } else if ('width' == col) {
                        width = param[col];
                    }
                }
                ;
            }
        }

        if (data == undefined) return;

        originR = r;
        originWidth = width;

        tots = d3.sum(data, function (d) {
            return d[valueField];
        });
        drawPie();
    }

    function drawPie() {

        if (!$.isNumeric(tots) || tots == 0) return false;

        //var clwidth = obj.parent()[0].clientWidth;
        var clwidth = obj.parent()[0];
        // 상위 div보다 크게 그려지는것을 방지
        if (originR > clwidth / 2) {
            r = clwidth / 2;
            width = clwidth;
        } else {
            r = originR;
            width = r * 2;
        }
        ir = r * innerRadius;

        d3.selectAll(obj.toArray()).select("svg").remove();

        var vis = d3.selectAll(obj.toArray()).append("svg:svg")
            .data([data])
            .attr("width", width)
            .attr("height", width).append("svg:g")
            .attr("transform", "translate(" + r + "," + r + ")")

        var tip;
        if (tip == undefined && showTooltip) {
            tip = d3.tip()
                .attr('class', 'tooltip')
                .offset([-10, 0])
                .html(function (d) {
                    return d.data[labelField] + " : " + comma(null2Zero(d.data[valueField])) + unit + " (" + null2Zero(Math.round(d.data[valueField] / tots * 100)) + "%" + ")";
                })
            vis.call(tip);
        }


        var arc = d3.arc()
            .outerRadius(r)
            .innerRadius(ir)
        ;

        var pie = d3.pie()
            .sort(null)
            .value(function (d) {
                return ($.isNumeric(d[valueField])) ? d[valueField] : 0;
            });

        var arcs = vis.selectAll("g.slice")
            .data(pie)
            .enter()
            .append("svg:g")
            .attr("class", "slice pieChartSlice")
        ;

        arcs.append("svg:path")
            .attr("fill", function (d, i) {
                return color[i];
            })
            .transition()
            .duration(duration)
            .attrTween('d', function (d, i) {
                d.innerRadius = 0;
                var i = d3.interpolate({startAngle: 0, endAngle: 0}, d);
                return function (t) {
                    return arc(i(t));
                }
            });

        arcs.selectAll("path")
            .on('mouseover', function (d) {
                if (showTooltip) {
                    tip.show(d);
                }
            })
            .on('mouseout', function (d) {
                if (showTooltip) {
                    tip.hide();
                }
            });


        if (showText) {
            var arcsText = arcs
                .append("svg:text")
                .attr("transform", function (d) {
                    d.innerRadius = 0;
                    d.outerRadius = r;
                    return "translate(" + arc.centroid(d) + ")";
                })
                .attr("class", "pieChartText")
                .style("fill", "#FFFFFF")
                .attr("text-anchor", "middle")
                .style("font-size", fontSize + "px")
                .style("font-weight", "bold")
                .text(function (d, i) {
                    var result = "";
                    if (showType == "value") {
                        result = comma(data[i][valueField]);
                    } else if (showType == "percent") {

                        if (Math.round(data[i][valueField] / tots * 100) > 5) {
                            result = Math.round(data[i][valueField] / tots * 100) + "%";
                        }
                    } else if (showType == "gun") {
                        result = comma(data[i][valueField]);
                    }

                    if (data[i][valueField] == 0) result = "";
                    return result;
                })
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)

            ;
        }

    };

    this.setData();


    $(window).resize(function () {
        drawPie();
    });

}


/**** 반원형차트 ****/
var wrHalfPieV4 = function (obj, param) {

    var easeType = "exp";
    var duration = 1000;

    var originR;
    var originWidth;

    var width = 370, // width
        labelField = "label",
        valueField = "value",
        percentField = "percent",
        r = 155, // radius
        ir = 130, // innerRadius
        or = r - 10, // outerRadius
        data;

    var tots;

    var color = [
        "#424242",
        "#2E79DE",
        "#FEC64B",
        "#73C16B",
        "#419FDA",
        "#125498"
    ];

    var fontSize = 14;
    var unit = "";
    var innerRadius;
    var showType = "value"; //value, percent

    var showText = false;        //true, false
    var showTooltip = false;     //true, false

    this.setData = function (d) {
        data = d
        if (param != undefined) {
            for (var col in param) {
                if (param[col] != undefined) {
                    /** 사용금지
                     eval(col + "=param[col]");
                     console.log(col + '=' + param[col]);
                     **/
                    if ('data' == col) {
                        data = param[col];
                    } else if ('width' == col) {
                        width = param[col];
                    } else if ('r' == col) {
                        r = param[col];
                    } else if ('innerRadius' == col) {
                        innerRadius = param[col];
                    } else if ('labelField' == col) {
                        labelField = param[col];
                    } else if ('valueField' == col) {
                        valueField = param[col];
                    } else if ('percentField' == col) {
                        percentField = param[col];
                    } else if ('fontSize' == col) {
                        fontSize = param[col];
                    } else if ('color' == col) {
                        color = param[col];
                    } else if ('unit' == col) {
                        unit = param[col];
                    } else if ('showTooltip' == col) {
                        showTooltip = param[col];
                    } else if ('showType' == col) {
                        showType = param[col];
                    }
                }
            }
        }

        if (data == undefined) return;

        originR = r;
        originWidth = width;

        tots = d3.sum(data, function (d) {
            return d[valueField];
        });
        drawHalfPie();
    }

    function drawHalfPie() {

        if (!$.isNumeric(tots) || tots == 0) return false;

        /*var clwidth = obj.parent()[0].clientWidth;
        // 상위 div보다 크게 그려지는것을 방지
        if(originR>clwidth/2){
        	r = clwidth/2;
            width = clwidth;
        }else{
        	r= originR;
            width = r*2;
        }*/

        d3.selectAll(obj.toArray()).select("svg").remove();

        var vis = d3.selectAll(obj.toArray()).append("svg:svg")
            .data([data])
            .attr("width", width)
            .attr("height", width).append("svg:g")
            .attr("transform", "translate(" + width / 2 + "," + width / 2 + ")")

        var tip;
        if (tip == undefined && showTooltip) {
            tip = d3.tip()
                .attr('class', 'tooltip')
                .offset([-10, 0])
                .html(function (d) {
                    if (d.data[percentField] > 100) {
                        result = "100";
                    } else if (d.data[percentField] < 0) {
                        result = "0";
                    } else {
                        result = null2Zero(Math.round(parseFloat(d.data[percentField]) * 10) / 10); // 툴팁 퍼센트 표시
                    }
                    return d.data[labelField] + " : " + comma(null2Zero(d.data[valueField])) + unit
                        // + " ("+null2Zero(Math.round(d.data[percentField]))+"%"+")"; //20211217 주석처리
                        + " (" + result + "%" + ")";

                    //ORI 20210929 수정
                    //return d.data[labelField] + " : " + comma(null2Zero(d.data[valueField])) + unit + " ("+null2Zero(Math.round(d.data[valueField]/tots*100))+"%A"+")";

                })
            vis.call(tip);
        }


        var arc = d3.arc()
            .outerRadius(or)
            .innerRadius(ir)
        ;

        var pie = d3.pie()
            .sort(null)
            .startAngle(-90 * (Math.PI / 180))
            .endAngle(90 * (Math.PI / 180))
            .value(function (d) {
                if ($.isNumeric(d[percentField]) > 100) { // 100% 초과하면 100%로 표시됨
                    result = "100";
                } else if ($.isNumeric(d[percentField]) < 0) { // 0% 미만이면 0%로 표시됨
                    result = "0";
                } else {
                    result = d[percentField];
                }
                // 데이터 값의 비율만큼 게이지 색상이 채워지기 위해 valueField가 아닌 percentField 사용
                // return ($.isNumeric(d[percentField]))?d[percentField]:0;  //20211217 주석처리
                return ($.isNumeric(d[percentField])) ? result : 0;
            });


        var arcs = vis.selectAll("g.slice")
            .data(pie)
            .enter()
            .append("svg:g")
            .attr("class", "slice pieChartSlice")
        ;

        arcs.append("svg:path")
            .attr("fill", function (d, i) {
                return color[i];
            })
            .transition()
            .duration(duration)
            .attrTween('d', function (d, i) {
                d.innerRadius = 0;
                var i = d3.interpolate({startAngle: -90 * (Math.PI / 180), endAngle: -90 * (Math.PI / 180)}, d);
                return function (t) {
                    return arc(i(t));
                }
            });

        arcs.selectAll("path")
            .on('mouseover', function (d) {
                if (showTooltip) {
                    tip.show(d);
                }
            })
            .on('mouseout', function (d) {
                if (showTooltip) {
                    tip.hide();
                }
            });


        if (showText) {
            var arcsText = arcs
                .append("svg:text")
                .attr("transform", function (d) {
                    d.innerRadius = 0;
                    d.outerRadius = r;
                    return "translate(" + arc.centroid(d) + ")";
                })
                .attr("class", "pieChartText")
                .style("fill", "#FFFFFF")
                .attr("text-anchor", "middle")
                .attr("alignment-baseline", "middle") /* 2023-04-12(수) 전진우 수정 (%값 중앙정렬) */
                .style("font-size", fontSize + "px")
                .style("font-weight", "bold")
                .text(function (d, i) {
                    var result = "";
                    if (showType == "value") {
                        result = comma(data[i][valueField]);
                    } else if (showType == "percent") {
                        // 20210929 ORI
                        //result = Math.round(data[i][valueField]/tots*100)+"%";

                        if (null2Zero(Math.round(data[i][percentField])) > 100) { // 100% 초과하면 100%로 표시됨
                            result = "100%";
                        } else if (null2Zero(Math.round(data[i][percentField])) < 0) { // 0% 미만이면 0%로 표시됨
                            result = "0%";
                        } else if (null2Zero(Math.round(data[i][percentField])) < 3) { // 3% 미만이면 텍스트 표시 안 함
                            result = "";
                        } else {
                            result = null2Zero(Math.round(parseFloat(data[i][percentField]) * 10) / 10) + "%"; // 반원 위에 퍼센트 표시
                        }

                        //result = null2Zero(Math.round(data[i][percentField]))+"%";
                    }
                    ///if(data[i][valueField]==0)result=""; 20210929 ORI
                    if (result == '0%') {
                        result = "";
                    }
                    return result;
                })
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)

            ;
        }

    };

    this.setData();


    $(window).resize(function () {
        drawHalfPie();
    });

}


/**** 막대 + 선 그래프 (y축 있는 버전. IT재원현황분석 페이지) ****/
var gStackLineChart = function (param) {

    // General
    var id;
    var data;
    var xName;

    // Column
    var columnType = "";       // stack, overlay
    var columnFields;
    var columnNames;
    var columnColors;
    var columnStrokeColors;
    var columnTextXOffSets;
    var columnTextYOffSets;
    var columnTextColors;
    var columnTextAligns;           // top(default), center, rightCenter,
    var columnTextShows;            // all, last, none, lastValue
    var columnTextFncs;
    var columnWidthRatio = 0.6;
    var columnUnits;
    var columnSignals;

    var useBlankColumn = false;


    // Line
    var lineFields;
    var lineNames;
    var lineColors;
    var lineTextXOffSets;
    var lineTextYOffSets;
    var lineTextShows;               //none, all, last
    var lineTextAligns;              // top, right(default)
    var lineTextFncs;
    var lineUnits;
    var lineTypes;                   // solidLine, dotLine
    var lineSignals;
    var useZeroLine;

    // Axis
    var firstAxisRange;
    var secondAxisFields;
    var secondAxisRange;


    //callBackFunc
    var overFunc;
    var clickFunc;


    // inner Vars
    var stackMax = 0;
    var stackMin = 0;
    var lineValues = [];

    var easeType = "exp";
    var duration = 800;

    // Margin
    var margin = {top: 10, right: 10, bottom: 24, left: 50};

    // color
    var color = d3.scaleOrdinal().range(["#BAB6B6", "#8A8A8A", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
    var signalColor = {Y: "#F4A701", G: "#2EA80E", R: "#ED3938"};

    // set param
    if (param != undefined) {
        for (var col in param) {
            if (param[col] != undefined) {
                /** eval 사용 금지
                 eval(col + "=param[col]");
                 console.log('gChart]' + col + '=' + param[col]);
                 **/
                if ('id' == col) {
                    id = param[col];
                } else if ('data' == col) {
                    data = param[col];
                } else if ('xName' == col) {
                    xName = param[col];
                } else if ('columnType' == col) {
                    columnType = param[col];
                } else if ('columnFields' == col) {
                    columnFields = param[col];
                } else if ('columnNames' == col) {
                    columnNames = param[col];
                } else if ('columnColors' == col) {
                    columnColors = param[col];
                } else if ('columnTextAligns' == col) {
                    columnTextAligns = param[col];
                } else if ('columnTextColors' == col) {
                    columnTextColors = param[col];
                } else if ('columnWidthRatio' == col) {
                    columnWidthRatio = param[col];
                } else if ('columnUnits' == col) {
                    columnUnits = param[col];
                } else if ('useBlankColumn' == col) {
                    useBlankColumn = param[col];
                }
            }
        }
    }

    // SVG Size
    var width = $("#" + id).width() - margin.left - margin.right,
        height = $("#" + id).height() - margin.top - margin.bottom;

    d3.select("#" + id).selectAll("g").remove();

    // Chart SVG
    var chart = d3.select("#" + id)
        .attr("width", width + margin.left + margin.right) // 기존
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    chart.append("rect")
        .attr("width", "100%")
        .attr("height", margin.bottom)
        .attr("y", height)
        .style("fill", "white")
    ;

    // X Scale
    var x = d3.scaleBand()
        .rangeRound([0, width]) // 반대로 바꾸면 x축(혹은 tick?) 위의 데이터값 0들이 사라짐
        .paddingInner(0.38) // 막대 사이의 거리
        .paddingOuter(0.38); // 막대가 0에서부터 떨어지는 거리 (막대 크기도 자동 조절됨)

    // for group bar
    var x2 = d3.scaleOrdinal();

    // first Y Scale
    var y = d3.scaleLinear()
        .range([height, 0]);

    // second Y Scale
    var y2 = d3.scaleLinear()
        .range([height, 0]);

    /************************************ Aixs ************************************/
        // X Axis
    var xAxis = d3.axisBottom(x)
            .scale(x)
            .tickSize(0)
            .tickPadding(10)
            .tickFormat(function (d, i) {
                return d;
            });

    // YAxis
    var yAxis = d3.axisLeft()
        .scale(y)
        .ticks(5, "") // 0으로 바꾸면 y축 텍스트 사라짐
        .tickSize(0) //.tickSize(-width) 은 가로줄 생김 -> 깔끔하지 않아서 우선 주석처리.
        .tickPadding(22) // tickSize와 tickPadding이 없으면 y축에 눈금 생김
        .tickFormat(d3.format(".1f")); // ".0f"로 바꾸면 소수점 삭제 가능

    // add x Axis
    chart.append("g")
        .attr("class", "x axis axisGrey")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
    ;

    // add Y Axis
    chart.append("g")
        .attr("class", "y axis axisGrey")
        .call(yAxis)
        .append("text")
        .attr("transform", "translate(0," + height + ")") // "rotate(-90)"처럼 변경 가능
        //.attr("y", 6)
        .attr("y", -180) // 숫자가 높을 수록 y축의 아래에 위치
        .attr("dy", ".71em")
        .style("text-anchor", "end")
    //.text("(억)") - 그래프 사이즈가 달라지면 매번 위치지정을 수동으로 해줘야 해서 불편할 듯. 우선 주석처리.

    /************************************ Tooltip ************************************/
        // Tool Tip(extension Lib)
    var tip = d3.tip()
            .attr('class', 'tooltip')
            .offset([-10, 0])
            .html(function (d) {
                //return "value: <span style='color:" + ((val > 0) ? 'red' : 'blue') + "'>" + comma(val) + "</span>";
                return d.label + " : " + comma(d.value) + d.unit;
            })
    chart.call(tip);


    /*****************************************************************************
     * private function
     *****************************************************************************/
    function arrangeData() {
        data.forEach(function (d, k) {
            var dColumns = [];
            if (columnFields) {
                columnFields.map(function (name, i, j) {
                    var result;

                    if ($.isArray(name)) {
                        // is Stack
                        y1Arr = [0];
                        var y0 = 0;
                        var y1 = 0;

                        for (var l = 0; l < name.length; l++) {
                            var val = +d[name[l]];
                            if (columnType == "stack") y0 = (val > 0) ? +d3.max(y1Arr) : +d3.min(y1Arr);
                            y1 = y0 + val;
                            if (isNaN(y1)) y1 = 0;

                            y1Arr.push(y1);
                            if (stackMax < y1) stackMax = y1;
                            if (stackMin > y1) stackMin = y1;

                            var obj = {
                                name: name[l],
                                label: columnNames[i][l],
                                value: val,
                                color: columnColors[i][l],
                                strokeColor: (columnStrokeColors) ? columnStrokeColors[i][l] : undefined,
                                signal: (columnSignals) ? d[columnSignals[i][l]] : undefined,
                                y0: y0,
                                y1: y1,
                                idx: k,
                                groupIdx: i,
                                stackIdx: l,
                                unit: (columnUnits) ? columnUnits[i][l] : "",
                                textAlign: (columnTextAligns) ? columnTextAligns[i][l] : "top",
                                textColor: (columnTextColors) ? columnTextColors[i][l] : "black",
                                textShow: (columnTextShows) ? columnTextShows[i][l] : "all",//"last"
                                textFnc: (columnTextFncs) ? columnTextFncs[i][l] : undefined,
                                xOffset: (columnTextXOffSets) ? columnTextXOffSets[i][l] : 0,
                                yOffset: (columnTextYOffSets) ? columnTextYOffSets[i][l] : 0,
                                data: d
                            };
                            dColumns.push(obj);
                        }
                    } else {
                        // is Group
                        obj = {
                            name: name,
                            label: columnNames[i],
                            value: +d[name],
                            color: columnColors[i],
                            strokeColor: (columnStrokeColors) ? columnStrokeColors[i] : undefined,
                            signal: (columnSignals) ? d[columnSignals[i]] : undefined,
                            y0: 0,
                            y1: +d[name],
                            idx: k,
                            groupIdx: i,
                            unit: (columnUnits) ? columnUnits[i] : "",
                            textAlign: (columnTextAligns) ? columnTextAligns[i] : "top",
                            textColor: (columnTextColors) ? columnTextColors[i] : "black",
                            textShow: (columnTextShows) ? columnTextShows[i] : "all",//"last"
                            textFnc: (columnTextFncs) ? columnTextFncs[i] : undefined,
                            xOffset: (columnTextXOffSets) ? columnTextXOffSets[i] : 0,
                            yOffset: (columnTextYOffSets) ? columnTextYOffSets[i] : 0,
                            data: d
                        };
                        dColumns.push(obj);
                    }
                });
                d.columns = dColumns;
            }

            // Line Circle
            if (lineFields) {
                d.lineCircles = lineFields.map(function (name, i) {
                    return {
                        name: name,
                        label: lineNames[i],
                        value: +d[name],
                        signal: (lineSignals) ? d[lineSignals[i]] : undefined,
                        idx: k,
                        unit: (lineUnits) ? lineUnits[i] : "",
                        textAlign: (lineTextAligns) ? lineTextAligns[i] : "right",
                        textShow: (lineTextShows) ? lineTextShows[i] : "none",
                        textFnc: (lineTextFncs) ? lineTextFncs[i] : undefined,
                        color: (lineColors) ? lineColors[i] : "gray",
                        xOffset: (lineTextXOffSets) ? lineTextXOffSets[i] : 0,
                        yOffset: (lineTextYOffSets) ? lineTextYOffSets[i] : -5,
                    };
                });
            }
        });

        // Line
        if (lineFields) {
            lineFields.map(function (name, k) {
                var values = [];
                data.forEach(function (d, i) {
                    values.push({
                        name: name,
                        label: lineNames[k],
                        value: +d[name],
                        idx: k,
                        color: (lineColors) ? lineColors[k] : "gray",
                        lineType: (lineTypes) ? lineTypes[k] : "solid"
                    });
                })
                lineValues.push(values);
            });
        }
    }

    // set Domain X,Y rage
    function setDomain() {
        if (data) {

            x.domain(data.map(function (d) {
                return d[xName];
            }))
            // ★  x.rangeBand()가 문제라서 주석처리.
            // first Axis
            /*if(columnFields){
                x2.domain(d3.range(columnFields.length))
                	//.rangeRoundBands([0, x.rangeBand()]);
                	.rangeRound([0, x.rangeBand()]);
            }*/

            var dataFields;
            if (columnFields && lineFields) {
                dataFields = (columnFields.join() + "," + lineFields.join()).split(",");
            } else if (columnFields) {
                dataFields = columnFields.join().split(",");
            } else if (lineFields) {
                dataFields = lineFields.join().split(",");
            }
            var minVal = 0;
            var maxVal = 0;


            for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < dataFields.length; j++) {
                    var val = data[i][dataFields[j]];
                    if ($.isNumeric(val)) {
                        minVal = Math.min(minVal, val);
                        maxVal = Math.max(maxVal, val);
                    }
                }
            }

            if (columnType == "stack") {
                stackMin = Math.min(stackMin, minVal);
                stackMax = Math.max(stackMax, maxVal);
                y.domain([stackMin * 1.1, stackMax * 1.1]);     // 최대치 여백을 만들기 위해 1.1정도 추가
            } else {
                y.domain([minVal * 1.1, maxVal * 1.1]);
            }

            if (firstAxisRange != undefined) y.domain(firstAxisRange);
            if (y.domain()[0] < 0) useZeroLine = true;  // 마이너스 값이 있는 경우 ZeroLine 표시

            // second Axis
            if (secondAxisFields != undefined) {
                if (secondAxisRange != undefined) {
                    // Axis Range 값이 있는경우
                    y2.domain(secondAxisRange);
                } else {
                    // Axis Range 값이 없는경우
                    var sminVal = 0;
                    var smaxVal = 0;
                    for (var i = 0; i < data.length; i++) {
                        for (var j = 0; j < secondAxisFields.length; j++) {
                            var val = data[i][secondAxisFields[j]];
                            if ($.isNumeric(val)) {
                                sminVal = Math.min(sminVal, val);
                                smaxVal = Math.max(smaxVal, val);
                            }
                        }
                    }
                    y2.domain([sminVal * 1.1, smaxVal * 1.1]);
                }
            }
        } else {
            // default domain range 0~100
            y.domain([0, 100]);
        }
    }


    // Bar Drawing
    function setColumn(selector) {
        selector
            .filter(function (d, i, xIdx) {
                return d.value != 0 && $.isNumeric(d.value);
            })
            .style("fill", function (d, i, xIdx) {
                if (d.signal) {
                    return signalColor[d.signal];
                } else if ($.isFunction(d.color)) {
                    return d.color(d);
                } else {
                    return (columnColors) ? d.color : color[i];
                }
            })
            .style("stroke", function (d) {
                return (d.strokeColor) ? d.strokeColor : undefined;
            })
            .style("stroke-width", 0.3)
            .on('click', function (d) {
                if (clickFunc) clickFunc(d);
            })
            .on('mouseover', function (d) {
                tip.show(d);
                if (overFunc) overFunc(d);
            })
            .on('mouseout', tip.hide)
            //.attr("width", 0)
            .attr("y", height)
            .attr("height", 0)
            .transition()
            //.ease(easeType)
            .ease(d3.easeLinear)
            .duration(duration)
            .attr("class", "bar")
            .attr("label", function (d) {
                return d.label;
            })
            .attr("value", function (d) {
                return d.value;
            })
            .attr("x", function (d) {
                // ★ x2.range()에서 x.range로 수정. 결과 차이는 없음.
                return x.range()[d.groupIdx];
            })
            //.attr("width", x2.rangeBand())
            // ★  x2.bandwidth()가 문제, x.bandwidth()로 수정
            .attr("width", x.bandwidth())
            .attr("y", function (d) {
                return (d.y1 > 0) ? y(d.y1) : y(d.y0);
            })
            .attr("height", function (d) {
                return (d.y1 > 0) ? (y(d.y0) - y(d.y1)) : (y(d.y1) - y(d.y0));
            })
        ;
        if (useBlankColumn) {
            selector
                .filter(function (d, i, xIdx) {
                    // 실제값이 있는 경우 isAllNoValue = false
                    var isAllNoValue = true;
                    for (var i = 0; i < d.data.columns.length; i++) {
                        var val = d.data.columns[i].value;
                        if ($.isNumeric(val) && val != 0) {
                            isAllNoValue = false;
                            break;
                        }
                    }
                    return (d.groupIdx == 0 && isAllNoValue); // 첫번째값이며 모든 컬럼이 값이 없거나 0인 경우 blank를 그림
                })
                .style("fill", function (d, i, xIdx) {
                    return '#DDDDDD';
                })
                .style("fill-opacity", 0.1)
                .style("stroke-width", 0.2)
                .on('mouseover', tip.hide)
                .on('mouseout', tip.hide)
                .attr("y", height)
                .attr("height", 0)
                .transition()
                //.ease(easeType)
                .ease(d3.easeLinear)
                .duration(duration)
                .attr("class", "bar")
                .attr("label", function (d) {
                    return d.label;
                })
                .attr("value", function (d) {
                    return d.value;
                })
                .attr("x", function (d) {
                    // ★ x2.range()에서 x.range로 수정. 결과 차이는 없음.
                    return x.range()[d.groupIdx];
                })
                //.attr("width", x2.rangeBand())
                // ★ .attr("width", x2.bandwidth())에서 다른 x.bandwidth()와 통일하기 위해 수정. 결과 차이는 없음.
                .attr("width", x.bandwidth())
                .attr("y", function (d) {
                    return height * 0.1;
                })
                .attr("height", function (d) {
                    return height * 0.9;
                });
        }
    }

    // Line Spot Drawing
    function setCircle(selector) {
        selector
            .filter(function (d, i, xIdx) {
                return $.isNumeric(d.value);
            })
            .on('mouseover', tip.show)
            .on('mouseout', tip.hide)
            .attr("cy", height)
            .transition()
            //.ease(easeType)
            .ease(d3.easeLinear)
            .duration(duration)
            //.attr("class", "circle")
            .attr("label", function (d) {
                return d.label;
            })
            .attr("value", function (d) {
                return d.value;
            })
            .attr("cx", function (d) {
                //return x.rangeBand() / 2;
                return x.bandwidth() / 2;
            })
            .attr("cy", function (d, i, j) {
                return chooseAxis(d);
            })
            .attr("r", 4.5)
    }

    // zeroLine Drawing
    function setZeroLine(selector) {
        if (useZeroLine) {
            selector
                .attr("x1", 0)
                .attr("x2", width)
                .attr("y1", y(0))
                .attr("y2", y(0))
                .attr("class", "zeroLine");
            ;
        }
    }

    // Choose fist/second Axis
    function chooseAxis(d) {
        if (secondAxisFields && secondAxisFields.toString().indexOf(d.name) > -1) {
            return y2(d.value);
        } else {
            return y(d.value);
        }
    }


    function arrangeText(texts) {
        for (var i = 0; i < 10; i++) {
            texts.each(function (d, i) {
                var iObj = this,
                    a = this.getBoundingClientRect();
                texts.each(function (d) {
                    var jObj = this,
                        b = this.getBoundingClientRect();

                    if (iObj != jObj) {
                        moveTexts(iObj, jObj);
                    }
                });
            });
        }
    }

    function moveTexts(aObj, bObj) {
        var a = d3.select(aObj);
        var b = d3.select(bObj);
        var aY = parseFloat(a.attr("y"));
        var bY = parseFloat(b.attr("y"));
        var h = 9;

        if (
            (aY < bY && (aY + h) > bY)
            || (bY < aY && (bY + h) > aY)
        ) {
            if (aY < bY) {
                if (aY > 15) a.attr("y", aY - 1);
                b.attr("y", bY + 1)
            } else if (bY < aY) {
                if (bY > 15) b.attr("y", bY - 1);
                a.attr("y", aY + 1);
            }
        }
    }

    /*****************************************************************************
     * public function
     *****************************************************************************/

    // set Data with reload
    this.setData = function (d) {
        data = d;
        this.reload();
    }

    // return Data
    this.getData = function () {
        return data;
    }

    // Data Reload (public)
    this.reload = function reload() {
        arrangeData();
        setDomain();

        chart.selectAll("g.y.axis").call(yAxis);
        chart.selectAll("g.x.axis").call(xAxis);

        // 리로드시 추가가 없을경우 selector가 null이 될수 있음 으로 append와 selector를 별도로 분리

        if (columnFields) {
            /************************************ Colunm ************************************/

            chart.selectAll(".barArea")
                .data(data)
                .exit()
                .remove();


            chart.selectAll(".barArea")
                .data(data)
                .enter().append("g")
                .attr("class", "barArea")
            /*.attr("transform", function (d) {
                return "translate(" + x(d[xName]) + ",0)";
            })*/
            ;

            chart.selectAll(".barArea")
                .attr("transform", function (d) {
                    return "translate(" + x(d[xName]) + ",0)"; // barArea를 0에서부터 시작하지 않으려면 x(d[xName]) + 20할 것. 하지만 이러면 bar 1개만 나옴.
                });

            var barArea = chart.selectAll(".barArea");


            barArea.selectAll("rect")
                .data(function (d) {
                    return d.columns;
                })
                .exit()
                .remove();

            barArea.selectAll("rect")
                .data(function (d) {
                    return d.columns;
                })
                .enter().append("rect")
            ;
            barArea.selectAll("rect")
                .call(setColumn)
            ;

            /************************************ Column Text ************************************/
            barArea.selectAll(".columnText")
                .data(function (d) {
                    return d.columns;
                })
                .exit()
                .remove();
            barArea.selectAll(".columnText").text("");


            barArea.selectAll(".columnText")
                .data(function (d) {
                    return d.columns;
                })
                .enter().append("text")
                .attr("class", "columnText")
            ;

            barArea.selectAll(".columnText")
                .filter(function (d, i, xIdx) {

                    // 긴존의 배열의 마지막이 아닌 실제 데이터가 있는 마지막 index
                    var lastDataIdx = 0;
                    for (var i = data.length - 1; i > -1; i--) {
                        if (data[i][d.name] != undefined && data[i][d.name] != 0 && data[i][d.name] != null && data[i][d.name] != "null") {
                            lastDataIdx = i;
                            break;
                        }
                    }

                    return (
//                    	(d.value!=undefined && !isNaN(d.value) &&  d.value!=null && d.value!="null")
                        $.isNumeric(d.value)
                        && ((d.textShow == "last" && xIdx == lastDataIdx)//&& xIdx == data.length - 1
                            || d.textShow == "all")
                    )
                })
                .text(function (d, i, xIdx) {
                    var result = comma(d.value);

                    if ($.isFunction(d.textFnc) && result) {
                        result = d.textFnc(d, this);
                    }

                    if (result != 0) return result; // ★ 원래 if (result)였으나 if (result != 0)로 변경. columnText값이 0이면 안 나오도록 했습니다.
                })
                .attr("x", function (d) {
                    var result;
                    if (d.textAlign.toLowerCase().indexOf("right") > -1) {
                        result = x.bandwidth() + 20;
                        //result = x.rangeBand() + 20;
                    } else {
                        // ★  x2.bandwidth()가 문제, x2에서 x로 수정
                        // result = x2.range()[d.groupIdx] + x2.bandwidth() / 2;
                        result = x.range()[d.groupIdx] + x.bandwidth() / 2;
                        //result = x2.range()[d.groupIdx] + x2.rangeBand() / 2;
                    }
                    return result + d.xOffset;
                })
                .attr("y", function (d) {
                    var result = 0;
                    if (d.textAlign.toLowerCase().indexOf("center") > -1) {
                        var thisHeight = 8;
                        result = (y(d.y0) - y(d.y1)) / 2 + y(d.y1) + thisHeight / 2;
                    } else if (d.textAlign.toLowerCase().indexOf("top") > -1) {
                        if (d.y1 > 0) {
                            result = y(d.y1) - 5;
                        } else {
                            result = y(d.y1) + 7;
                        }
                    }
                    return result + d.yOffset;
                })
                .style("fill", function (d) {
                    return d.textColor;
                })
            //.style("text-anchor", function(d){
            //    return (d.textAlign.indexOf("right")>-1)?"right":"middle";
            //})
            ;
            barArea.selectAll(".columnText")
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)
            ;
            var columnTexts = barArea.selectAll(".columnText").filter(function (d, i, xIdx) {
                return data.length - 1 == d.idx;
            })
            arrangeText(columnTexts);
        }

        if (lineFields) {
            /************************************ Line ************************************/
            chart.selectAll(".path")
                .data(lineValues)
                .enter()
                .append("g")
                .append("path")
                .attr("class", function (d) {
                    var colorCls = (d[0].color + "Line");
                    var typeCls = (d[0].lineType + "Line");
                    return colorCls + " " + typeCls;
                })
                .attr("d", function (d) {
                    var line = d3.line()
                        .defined(function (d) {
                            return $.isNumeric(d.value);
                        })
                        .x(function (d, i) {
                            return x.range()[i] + x.bandwidth() / 2;
                            //return x.range()[i] + x.rangeBand() / 2;
                            //return x(d[i]+d.bandwidth()/2);
                        })
                        .y(height)
                        .curve(d3.curveMonotoneX)
                    return line(d);
                })
                .transition()
                //.ease(easeType)
                .ease(d3.easeLinear)
                .duration(duration)
                .attr("d", function (d) {
                    var line = d3.line()
                        .defined(function (d) {
                            return !isNaN(d.value) && d.value != undefined && d.value != null && d.value != "null"
                        })
                        .x(function (d, i) {
                            return x.range()[i] + x.bandwidth() / 2;
                            //return x.range()[i] + x.rangeBand() / 2;
                            //return x(d[i]+d.bandwidth()/2);
                        })
                        .y(function (d) {
                            return chooseAxis(d);
                        })
                        .curve(d3.curveMonotoneX)
                    return line(d);
                })
            ;
            /************************************ Line Circle ************************************/

            chart.selectAll(".circleArea")
                .data(data)
                .enter().append("g")
                .attr("class", "circleArea")
                .attr("transform", function (d) {
                    return "translate(" + x(d[xName]) + ",0)";
                })
            ;

            var circleArea = chart.selectAll(".circleArea");
            circleArea.selectAll("circle")
                .data(function (d) {
                    return d.lineCircles;
                })
                .enter().append("circle")
                .attr("class", function (d) {
                    if (d.signal) {
                        return getSignalStr(d.signal) + "Circle";
                    } else {
                        return d.color + "Circle"
                    }
                })
            ;
            circleArea.selectAll("circle")
                .call(setCircle)
            ;
            /************************************ Line Text ************************************/
            circleArea.selectAll(".lineText")
                .data(function (d) {
                    return d.lineCircles;
                })
                .enter().append("text")
                .attr("class", "lineText")
            ;

            circleArea.selectAll(".lineText")
                //.transition()
                .filter(function (d, i, xIdx) {
                    var lastDataIdx = 0;
                    for (var i = data.length - 1; i > -1; i--) {
                        if (data[i][d.name] != 0 && data[i][d.name] != null && data[i][d.name] != "null") {
                            lastDataIdx = i;
                            break;
                        }
                    }

                    return (
                        $.isNumeric(d.value)
                        && ((d.textShow == "last" && xIdx == lastDataIdx)
                            || (d.textShow == "firstLast" && (xIdx == 0 || xIdx == lastDataIdx))
                            || d.textShow == "all")
                    )
                })
                .text(function (d, i, xIdx) {
                    var result = comma(d.value);
                    if ($.isFunction(d.textFnc) && result) {
                        result = d.textFnc(d, this);
                    }
                    if (result) return result;
                })
                .attr("x", function (d) {
                    var result;
                    if (d.textAlign.indexOf("right") > -1) {
                        //result = x.rangeBand() + 25;
                        //result = x.bandwidth() + 25;
                        result = x.bandwidth() + 25;
                    } else {
                        //result = x.rangeBand() / 2;
                        //result = x.bandwidth() / 2;
                        result = x.bandwidth() / 2;
                    }

                    return result + d.xOffset;
                })
                .attr("y", function (d) {
                    var result;
                    if (d.textAlign.indexOf("right") > -1) {
                        result = chooseAxis(d) + 3;
                    } else {
                        result = chooseAxis(d) - 5;
                    }
                    result = result + d.yOffset;
                    return result;
                })
                .attr("text-anchor", function (d) {
                    return (d.textAlign.indexOf("right") > -1) ? "right" : "middle";
                })
                .attr("class", function (d) {
                    return d.color + "Text";
                })
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)
            ;

            // arrange text
            // target
            var lineTexts = circleArea.selectAll(".lineText").filter(function (d, i, xIdx) {
                return (d.textShow == "last" && xIdx == data.length - 1)
            })

            // get max text width
            var maxTextWidth = 0;
            lineTexts.each(function (d) {
                maxTextWidth = Math.max(maxTextWidth, this.getComputedTextLength())
            });

            // arrange x
            lineTexts.each(function (d) {
                var th = d3.select(this);
                th.attr("x", parseFloat(+th.attr("x")) + (maxTextWidth - this.getComputedTextLength()) / 2)
            });

            // arrange y
            arrangeText(lineTexts);

        }
        /************************************ zeroLine ************************************/
        if (chart.select(".zeroLine").empty() && y.domain()[0] < 0) {
            chart.append("line")
                .call(setZeroLine)
            ;
        } else {
            chart.select(".zeroLine")
                .call(setZeroLine)
            ;
        }
    }

    if (data) {
        this.reload();
    }
}

/**** 막대 + 선 그래프 (y축 없는 버전. IT비용추이분석 페이지) ****/
var gStackNoYChart = function (param) {

    // General
    var id;
    var data;
    var xName;

    // Column
    var columnType = "";       // stack, overlay
    var columnFields;
    var columnNames;
    var columnColors;
    var columnStrokeColors;
    var columnTextXOffSets;
    var columnTextYOffSets;
    var columnTextColors;
    var columnTextAligns;           // top(default), center, rightCenter,
    var columnTextShows;            // all, last, none, lastValue
    var columnTextFncs;
    var columnWidthRatio = 0.6;
    var columnUnits;
    var columnSignals;

    var useBlankColumn = false;
    var percentField; // = "percent"; // ★★ 2021.12.20 추가


    // Line
    var lineFields;
    var lineNames;
    var lineColors;
    var lineTextXOffSets;
    var lineTextYOffSets;
    var lineTextShows;               //none, all, last
    var lineTextAligns;              // top, right(default)
    var lineTextFncs;
    var lineUnits;
    var lineTypes;                   // solidLine, dotLine
    var lineSignals;
    var useZeroLine;

    // Axis
    var firstAxisRange;
    var secondAxisFields;
    var secondAxisRange;


    //callBackFunc
    var overFunc;
    var clickFunc;


    // inner Vars
    var stackMax = 0;
    var stackMin = 0;
    var lineValues = [];

    var easeType = "exp";
    var duration = 800;

    // Margin
    var margin = {top: 0, right: 20, bottom: 24, left: 20};

    // color
    var color = d3.scaleOrdinal().range(["#BAB6B6", "#8A8A8A", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
    var signalColor = {Y: "#F4A701", G: "#2EA80E", R: "#ED3938"};

    // set param
    if (param != undefined) {
        for (var col in param) {
            if (param[col] != undefined) {
                /** eval 사용 금지
                 eval(col + "=param[col]");
                 console.log('gChart]' + col + '=' + param[col]);
                 **/
                if ('id' == col) {
                    id = param[col];
                } else if ('data' == col) {
                    data = param[col];
                } else if ('xName' == col) {
                    xName = param[col];
                } else if ('columnType' == col) {
                    columnType = param[col];
                } else if ('columnFields' == col) {
                    columnFields = param[col];
                } else if ('columnNames' == col) {
                    columnNames = param[col];
                } else if ('columnColors' == col) {
                    columnColors = param[col];
                } else if ('columnTextAligns' == col) {
                    columnTextAligns = param[col];
                } else if ('columnTextColors' == col) {
                    columnTextColors = param[col];
                } else if ('columnWidthRatio' == col) {
                    columnWidthRatio = param[col];
                } else if ('columnUnits' == col) {
                    columnUnits = param[col];
                } else if ('useBlankColumn' == col) {
                    useBlankColumn = param[col];
                } else if ('percentField' == col) {  // 2021.12.20 추가
                    percentField = param[col];
                }
            }
        }
    }

    // SVG Size
    var width = $("#" + id).width() - margin.left - margin.right,
        height = $("#" + id).height() - margin.top - margin.bottom;

    d3.select("#" + id).selectAll("g").remove();

    // Chart SVG
    var chart = d3.select("#" + id)
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    chart.append("rect")
        .attr("width", "100%")
        .attr("height", margin.bottom)
        .attr("y", height)
        .style("fill", "white")
    ;

    // X Scale
    var x = d3.scaleBand()
        .rangeRound([0, width]) // 반대로 바꾸면 x축(혹은 tick?) 위의 데이터값 0들이 사라짐
        .paddingInner(0.5) // 막대 사이의 거리
        .paddingOuter(0.5); // 막대가 0에서부터 떨어지는 거리 (막대 크기도 자동 조절됨)

    // for group bar
    var x2 = d3.scaleOrdinal();

    // first Y Scale
    var y = d3.scaleLinear()
        .range([height, 0]);

    // second Y Scale
    var y2 = d3.scaleLinear()
        .range([height, 0]);

    /************************************ Aixs ************************************/
        // X Axis
    var xAxis = d3.axisBottom(x)
            .scale(x)
            .tickSize(0)
            .tickPadding(10)
            .tickFormat(function (d, i) {
                return d;
            });

    // YAxis
    var yAxis = d3.axisLeft()
        .scale(y)
        .ticks(5, "") // 0으로 바꾸면 y축 텍스트 사라짐
        .tickSize(0) //.tickSize(-width) 은 가로줄 생김 -> 깔끔하지 않아서 우선 주석처리.
        .tickPadding(10) // tickSize와 tickPadding이 없으면 y축에 눈금 생김
        .tickFormat(d3.format(".0f"));

    // add x Axis
    chart.append("g")
        .attr("class", "x axis axisGrey")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
    ;

    // add Y Axis
    /*chart.append("g")
        .attr("class", "y axis axisGrey")
        .call(yAxis)
        .append("text")
        .attr("transform", "translate(0," + height + ")") // "rotate(-90)"처럼 변경 가능
        //.attr("y", 6)
        .attr("y", -180) // 숫자가 높을 수록 y축의 아래에 위치
        .attr("dy", ".71em")
        .style("text-anchor", "end")*/
    //.text("(억)") - 그래프 사이즈가 달라지면 매번 위치지정을 수동으로 해줘야 해서 불편할 듯. 우선 주석처리.

    /************************************ Tooltip ************************************/
        // Tool Tip(extension Lib)
    var tip = d3.tip()
            .attr('class', 'tooltip')
            .offset([-10, 0])
            .html(function (d) {
                //return "value: <span style='color:" + ((val > 0) ? 'red' : 'blue') + "'>" + comma(val) + "</span>";
                return d.label + " : " + comma(d.value) + d.unit;
            })
    chart.call(tip);


    /*****************************************************************************
     * private function
     *****************************************************************************/
    function arrangeData() {
        data.forEach(function (d, k) {
            var dColumns = [];
            if (columnFields) {
                columnFields.map(function (name, i, j) {
                    var result;

                    if ($.isArray(name)) {
                        // is Stack
                        y1Arr = [0];
                        var y0 = 0;
                        var y1 = 0;

                        for (var l = 0; l < name.length; l++) {
                            var val = +d[name[l]];
                            if (columnType == "stack") y0 = (val > 0) ? +d3.max(y1Arr) : +d3.min(y1Arr);
                            y1 = y0 + val;
                            if (isNaN(y1)) y1 = 0;

                            y1Arr.push(y1);
                            if (stackMax < y1) stackMax = y1;
                            if (stackMin > y1) stackMin = y1;

                            var obj = {
                                name: name[l],
                                label: columnNames[i][l],
                                value: val,
                                color: columnColors[i][l],
                                strokeColor: (columnStrokeColors) ? columnStrokeColors[i][l] : undefined,
                                signal: (columnSignals) ? d[columnSignals[i][l]] : undefined,
                                y0: y0,
                                y1: y1,
                                idx: k,
                                groupIdx: i,
                                stackIdx: l,
                                unit: (columnUnits) ? columnUnits[i][l] : "",
                                textAlign: (columnTextAligns) ? columnTextAligns[i][l] : "top",
                                textColor: (columnTextColors) ? columnTextColors[i][l] : "black",
                                textShow: (columnTextShows) ? columnTextShows[i][l] : "all",//"last"
                                textFnc: (columnTextFncs) ? columnTextFncs[i][l] : undefined,
                                xOffset: (columnTextXOffSets) ? columnTextXOffSets[i][l] : 0,
                                yOffset: (columnTextYOffSets) ? columnTextYOffSets[i][l] : 0,
                                data: d,
                                percent: d[percentField[i][l]] //percentField[i][l] // ★★21.12.28 퍼센트 추가
                            };
                            dColumns.push(obj);
                        }
                    } else {
                        // is Group
                        obj = {
                            name: name,
                            label: columnNames[i],
                            value: +d[name],
                            color: columnColors[i],
                            strokeColor: (columnStrokeColors) ? columnStrokeColors[i] : undefined,
                            signal: (columnSignals) ? d[columnSignals[i]] : undefined,
                            y0: 0,
                            y1: +d[name],
                            idx: k,
                            groupIdx: i,
                            unit: (columnUnits) ? columnUnits[i] : "",
                            textAlign: (columnTextAligns) ? columnTextAligns[i] : "top",
                            textColor: (columnTextColors) ? columnTextColors[i] : "black",
                            textShow: (columnTextShows) ? columnTextShows[i] : "all",//"last"
                            textFnc: (columnTextFncs) ? columnTextFncs[i] : undefined,
                            xOffset: (columnTextXOffSets) ? columnTextXOffSets[i] : 0,
                            yOffset: (columnTextYOffSets) ? columnTextYOffSets[i] : 0,
                            data: d,
                            percent: d[percentField[i][l]] // ★★21.12.28 퍼센트 추가
                        };
                        dColumns.push(obj);
                    }
                });
                d.columns = dColumns;
            }

            // Line Circle
            if (lineFields) {
                d.lineCircles = lineFields.map(function (name, i) {
                    return {
                        name: name,
                        label: lineNames[i],
                        value: +d[name],
                        signal: (lineSignals) ? d[lineSignals[i]] : undefined,
                        idx: k,
                        unit: (lineUnits) ? lineUnits[i] : "",
                        textAlign: (lineTextAligns) ? lineTextAligns[i] : "right",
                        textShow: (lineTextShows) ? lineTextShows[i] : "none",
                        textFnc: (lineTextFncs) ? lineTextFncs[i] : undefined,
                        color: (lineColors) ? lineColors[i] : "gray",
                        xOffset: (lineTextXOffSets) ? lineTextXOffSets[i] : 0,
                        yOffset: (lineTextYOffSets) ? lineTextYOffSets[i] : -5,
                    };
                });
            }
        });

        // Line
        if (lineFields) {
            lineFields.map(function (name, k) {
                var values = [];
                data.forEach(function (d, i) {
                    values.push({
                        name: name,
                        label: lineNames[k],
                        value: +d[name],
                        idx: k,
                        color: (lineColors) ? lineColors[k] : "gray",
                        lineType: (lineTypes) ? lineTypes[k] : "solid"
                    });
                })
                lineValues.push(values);
            });
        }
    }

    // set Domain X,Y rage
    function setDomain() {
        if (data) {

            x.domain(data.map(function (d) {
                return d[xName];
            }))
            // ★  x.rangeBand()가 문제라서 주석처리.
            // first Axis
            /*if(columnFields){
                x2.domain(d3.range(columnFields.length))
                	//.rangeRoundBands([0, x.rangeBand()]);
                	.rangeRound([0, x.rangeBand()]);
            }*/

            var dataFields;
            if (columnFields && lineFields) {
                dataFields = (columnFields.join() + "," + lineFields.join()).split(",");
            } else if (columnFields) {
                dataFields = columnFields.join().split(",");
            } else if (lineFields) {
                dataFields = lineFields.join().split(",");
            }
            var minVal = 0;
            var maxVal = 0;


            for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < dataFields.length; j++) {
                    var val = data[i][dataFields[j]];
                    if ($.isNumeric(val)) {
                        minVal = Math.min(minVal, val);
                        maxVal = Math.max(maxVal, val);
                    }
                }
            }

            if (columnType == "stack") {
                stackMin = Math.min(stackMin, minVal);
                stackMax = Math.max(stackMax, maxVal);
                y.domain([stackMin * 1.1, stackMax * 1.1]);     // 최대치 여백을 만들기 위해 1.1정도 추가
            } else {
                y.domain([minVal * 1.1, maxVal * 1.1]);
            }

            if (firstAxisRange != undefined) y.domain(firstAxisRange);
            if (y.domain()[0] < 0) useZeroLine = true;  // 마이너스 값이 있는 경우 ZeroLine 표시

            // second Axis
            if (secondAxisFields != undefined) {
                if (secondAxisRange != undefined) {
                    // Axis Range 값이 있는경우
                    y2.domain(secondAxisRange);
                } else {
                    // Axis Range 값이 없는경우
                    var sminVal = 0;
                    var smaxVal = 0;
                    for (var i = 0; i < data.length; i++) {
                        for (var j = 0; j < secondAxisFields.length; j++) {
                            var val = data[i][secondAxisFields[j]];
                            if ($.isNumeric(val)) {
                                sminVal = Math.min(sminVal, val);
                                smaxVal = Math.max(smaxVal, val);
                            }
                        }
                    }
                    y2.domain([sminVal * 1.1, smaxVal * 1.1]);
                }
            }
        } else {
            // default domain range 0~100
            y.domain([0, 100]);
        }
    }


    // Bar Drawing
    function setColumn(selector) {
        selector
            .filter(function (d, i, xIdx) {
                return d.value != 0 && $.isNumeric(d.value);
            })
            .style("fill", function (d, i, xIdx) {
                if (d.signal) {
                    return signalColor[d.signal];
                } else if ($.isFunction(d.color)) {
                    return d.color(d);
                } else {
                    return (columnColors) ? d.color : color[i];
                }
            })
            .style("stroke", function (d) {
                return (d.strokeColor) ? d.strokeColor : undefined;
            })
            .style("stroke-width", 0.3)
            .on('click', function (d) {
                if (clickFunc) clickFunc(d);
            })
            .on('mouseover', function (d) {
                tip.show(d);
                if (overFunc) overFunc(d);
            })
            .on('mouseout', tip.hide)
            //.attr("width", 0)
            .attr("y", height)
            .attr("height", 0)
            .transition()
            //.ease(easeType)
            .ease(d3.easeLinear)
            .duration(duration)
            .attr("class", "bar")
            .attr("label", function (d) {
                return d.label;
            })
            .attr("value", function (d) {
                return d.value;
            })
            .attr("x", function (d) {
                // ★ x2.range()에서 x.range로 수정. 결과 차이는 없음.
                return x.range()[d.groupIdx];
            })
            //.attr("width", x2.rangeBand())
            // ★  x2.bandwidth()가 문제, x.bandwidth()로 수정
            .attr("width", x.bandwidth())
            .attr("y", function (d) {
                return (d.y1 > 0) ? y(d.y1) : y(d.y0);
            })
            .attr("height", function (d) {
                return (d.y1 > 0) ? (y(d.y0) - y(d.y1)) : (y(d.y1) - y(d.y0));
            })
        ;
        if (useBlankColumn) {
            selector
                .filter(function (d, i, xIdx) {
                    // 실제값이 있는 경우 isAllNoValue = false
                    var isAllNoValue = true;
                    for (var i = 0; i < d.data.columns.length; i++) {
                        var val = d.data.columns[i].value;
                        if ($.isNumeric(val) && val != 0) {
                            isAllNoValue = false;
                            break;
                        }
                    }
                    return (d.groupIdx == 0 && isAllNoValue); // 첫번째값이며 모든 컬럼이 값이 없거나 0인 경우 blank를 그림
                })
                .style("fill", function (d, i, xIdx) {
                    return '#DDDDDD';
                })
                .style("fill-opacity", 0.1)
                .style("stroke-width", 0.2)
                .on('mouseover', tip.hide)
                .on('mouseout', tip.hide)
                .attr("y", height)
                .attr("height", 0)
                .transition()
                //.ease(easeType)
                .ease(d3.easeLinear)
                .duration(duration)
                .attr("class", "bar")
                .attr("label", function (d) {
                    return d.label;
                })
                .attr("value", function (d) {
                    return d.value;
                })
                .attr("x", function (d) {
                    // ★ x2.range()에서 x.range로 수정. 결과 차이는 없음.
                    return x.range()[d.groupIdx];
                })
                //.attr("width", x2.rangeBand())
                // ★ .attr("width", x2.bandwidth())에서 다른 x.bandwidth()와 통일하기 위해 수정. 결과 차이는 없음.
                .attr("width", x.bandwidth())
                .attr("y", function (d) {
                    return height * 0.1;
                })
                .attr("height", function (d) {
                    return height * 0.9;
                });
        }
    }

    // Line Spot Drawing
    function setCircle(selector) {
        selector
            .filter(function (d, i, xIdx) {
                return $.isNumeric(d.value);
            })
            .on('mouseover', tip.show)
            .on('mouseout', tip.hide)
            .attr("cy", height)
            .transition()
            //.ease(easeType)
            .ease(d3.easeLinear)
            .duration(duration)
            //.attr("class", "circle")
            .attr("label", function (d) {
                return d.label;
            })
            .attr("value", function (d) {
                return d.value;
            })
            .attr("cx", function (d) {
                //return x.rangeBand() / 2;
                return x.bandwidth() / 2;
            })
            .attr("cy", function (d, i, j) {
                return chooseAxis(d);
            })
            .attr("r", 4.5)
    }

    // zeroLine Drawing
    function setZeroLine(selector) {
        if (useZeroLine) {
            selector
                .attr("x1", 0)
                .attr("x2", width)
                .attr("y1", y(0))
                .attr("y2", y(0))
                .attr("class", "zeroLine");
            ;
        }
    }

    // Choose fist/second Axis
    function chooseAxis(d) {
        if (secondAxisFields && secondAxisFields.toString().indexOf(d.name) > -1) {
            return y2(d.value);
        } else {
            return y(d.value);
        }
    }


    function arrangeText(texts) {
        for (var i = 0; i < 10; i++) {
            texts.each(function (d, i) {
                var iObj = this,
                    a = this.getBoundingClientRect();
                texts.each(function (d) {
                    var jObj = this,
                        b = this.getBoundingClientRect();

                    if (iObj != jObj) {
                        moveTexts(iObj, jObj);
                    }
                });
            });
        }
    }

    function moveTexts(aObj, bObj) {
        var a = d3.select(aObj);
        var b = d3.select(bObj);
        var aY = parseFloat(a.attr("y"));
        var bY = parseFloat(b.attr("y"));
        var h = 9;

        if (
            (aY < bY && (aY + h) > bY)
            || (bY < aY && (bY + h) > aY)
        ) {
            if (aY < bY) {
                if (aY > 15) a.attr("y", aY - 1);
                b.attr("y", bY + 1)
            } else if (bY < aY) {
                if (bY > 15) b.attr("y", bY - 1);
                a.attr("y", aY + 1);
            }
        }
    }

    /*****************************************************************************
     * public function
     *****************************************************************************/

    // set Data with reload
    this.setData = function (d) {
        data = d;
        this.reload();
    }

    // return Data
    this.getData = function () {
        return data;
    }

    // Data Reload (public)
    this.reload = function reload() {
        arrangeData();
        setDomain();

        chart.selectAll("g.y.axis").call(yAxis);
        chart.selectAll("g.x.axis").call(xAxis);

        // 리로드시 추가가 없을경우 selector가 null이 될수 있음 으로 append와 selector를 별도로 분리

        if (columnFields) {
            /************************************ Colunm ************************************/

            chart.selectAll(".barArea")
                .data(data)
                .exit()
                .remove();


            chart.selectAll(".barArea")
                .data(data)
                .enter().append("g")
                .attr("class", "barArea")
            /*.attr("transform", function (d) {
                return "translate(" + x(d[xName]) + ",0)";
            })*/
            ;

            chart.selectAll(".barArea")
                .attr("transform", function (d) {
                    return "translate(" + x(d[xName]) + ",0)"; // barArea를 0에서부터 시작하지 않으려면 x(d[xName]) + 20할 것. 하지만 이러면 bar 1개만 나옴.
                });

            var barArea = chart.selectAll(".barArea");


            barArea.selectAll("rect")
                .data(function (d) {
                    return d.columns;
                })
                .exit()
                .remove();

            barArea.selectAll("rect")
                .data(function (d) {
                    return d.columns;
                })
                .enter().append("rect")
            ;
            barArea.selectAll("rect")
                .call(setColumn)
            ;

            /************************************ Column Text ************************************/
            barArea.selectAll(".columnText")
                .data(function (d) {
                    return d.columns;
                })
                .exit()
                .remove();
            barArea.selectAll(".columnText")
                .text("");

            barArea.selectAll(".columnText")
                .data(function (d) { // 기존
                    // console.log(d.columns);
                    return d.columns;
                })
                /*.data(function(d){ // ★★ 진행 중. 10% 이상이면 columnText 나오도록 하기.
                    //console.log(d.columns[1].percent);
                    //console.log(d.columns[1].value);
                    //console.log(d.columns.length);
                    for (var i=0; i<d.columns.length; i++) {
                        var pnt = d.columns[i].percent;
                        console.log(pnt);
                        if (pnt != null && pnt >= 50) {
                             return d.columns;
                        }
                    }
                })*/
                .enter().append("text")
                .attr("class", "columnText")
            ;

            barArea.selectAll(".columnText")
                .filter(function (d, i, xIdx) {

                    // 긴존의 배열의 마지막이 아닌 실제 데이터가 있는 마지막 index
                    var lastDataIdx = 0;
                    for (var i = data.length - 1; i > -1; i--) {
                        if (data[i][d.name] != undefined && data[i][d.name] != 0 && data[i][d.name] != null && data[i][d.name] != "null") {
                            lastDataIdx = i;
                            break;
                        }
                    }

                    return (
//                    	(d.value!=undefined && !isNaN(d.value) &&  d.value!=null && d.value!="null")
                        $.isNumeric(d.value)
                        && ((d.textShow == "last" && xIdx == lastDataIdx)//&& xIdx == data.length - 1
                            || d.textShow == "all")
                    )
                })
                .text(function (d, i, xIdx) {
                    var result = comma(d.value);

                    /*if ($.isFunction(d.textFnc) && result) {
                        result = d.textFnc(d, this);
                    }*/

                    if (result != 0) return result; // ★ 원래 if (result)였으나 if (result != 0)로 변경. columnText값이 0이면 안 나오도록 했습니다.
                })
                .attr("x", function (d) {
                    var result;
                    if (d.textAlign.toLowerCase().indexOf("right") > -1) {
                        result = x.bandwidth() + 20;
                        //result = x.rangeBand() + 20;
                    } else {
                        // ★  x2.bandwidth()가 문제, x2에서 x로 수정
                        // result = x2.range()[d.groupIdx] + x2.bandwidth() / 2;
                        result = x.range()[d.groupIdx] + x.bandwidth() / 2;
                        //result = x2.range()[d.groupIdx] + x2.rangeBand() / 2;
                    }
                    return result + d.xOffset;
                })
                .attr("y", function (d) {
                    var result = 0;
                    if (d.textAlign.toLowerCase().indexOf("center") > -1) {
                        var thisHeight = 8;
                        result = (y(d.y0) - y(d.y1)) / 2 + y(d.y1) + thisHeight / 2;
                    } else if (d.textAlign.toLowerCase().indexOf("top") > -1) {
                        if (d.y1 > 0) {
                            result = y(d.y1) - 5;
                        } else {
                            result = y(d.y1) + 7;
                        }
                    }
                    return result + d.yOffset;
                })
                .style("fill", function (d) {
                    return d.textColor;
                })
            //.style("text-anchor", function(d){
            //    return (d.textAlign.indexOf("right")>-1)?"right":"middle";
            //})
            ;
            barArea.selectAll(".columnText")
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)
            ;
            var columnTexts = barArea.selectAll(".columnText").filter(function (d, i, xIdx) {
                return data.length - 1 == d.idx;
            })
            arrangeText(columnTexts);
        }

        if (lineFields) {
            /************************************ Line ************************************/
            chart.selectAll(".path")
                .data(lineValues)
                .enter()
                .append("g")
                .append("path")
                .attr("class", function (d) {
                    var colorCls = (d[0].color + "Line");
                    var typeCls = (d[0].lineType + "Line");
                    return colorCls + " " + typeCls;
                })
                .attr("d", function (d) {
                    var line = d3.line()
                        .defined(function (d) {
                            return $.isNumeric(d.value);
                        })
                        .x(function (d, i) {
                            return x.range()[i] + x.bandwidth() / 2;
                            //return x.range()[i] + x.rangeBand() / 2;
                            //return x(d[i]+d.bandwidth()/2);
                        })
                        .y(height)
                        .curve(d3.curveMonotoneX)
                    return line(d);
                })
                .transition()
                //.ease(easeType)
                .ease(d3.easeLinear)
                .duration(duration)
                .attr("d", function (d) {
                    var line = d3.line()
                        .defined(function (d) {
                            return !isNaN(d.value) && d.value != undefined && d.value != null && d.value != "null"
                        })
                        .x(function (d, i) {
                            return x.range()[i] + x.bandwidth() / 2;
                            //return x.range()[i] + x.rangeBand() / 2;
                            //return x(d[i]+d.bandwidth()/2);
                        })
                        .y(function (d) {
                            return chooseAxis(d);
                        })
                        .curve(d3.curveMonotoneX)
                    return line(d);
                })
            ;
            /************************************ Line Circle ************************************/

            chart.selectAll(".circleArea")
                .data(data)
                .enter().append("g")
                .attr("class", "circleArea")
                .attr("transform", function (d) {
                    return "translate(" + x(d[xName]) + ",0)";
                })
            ;

            var circleArea = chart.selectAll(".circleArea");
            circleArea.selectAll("circle")
                .data(function (d) {
                    return d.lineCircles;
                })
                .enter().append("circle")
                .attr("class", function (d) {
                    if (d.signal) {
                        return getSignalStr(d.signal) + "Circle";
                    } else {
                        return d.color + "Circle"
                    }
                })
            ;
            circleArea.selectAll("circle")
                .call(setCircle)
            ;
            /************************************ Line Text ************************************/
            circleArea.selectAll(".lineText")
                .data(function (d) {
                    return d.lineCircles;
                })
                .enter().append("text")
                .attr("class", "lineText")
            ;

            circleArea.selectAll(".lineText")
                //.transition()
                .filter(function (d, i, xIdx) {
                    var lastDataIdx = 0;
                    for (var i = data.length - 1; i > -1; i--) {
                        if (data[i][d.name] != 0 && data[i][d.name] != null && data[i][d.name] != "null") {
                            lastDataIdx = i;
                            break;
                        }
                    }

                    return (
                        $.isNumeric(d.value)
                        && ((d.textShow == "last" && xIdx == lastDataIdx)
                            || (d.textShow == "firstLast" && (xIdx == 0 || xIdx == lastDataIdx))
                            || d.textShow == "all")
                    )
                })
                .text(function (d, i, xIdx) {
                    var result = comma(d.value);
                    if ($.isFunction(d.textFnc) && result) {
                        result = d.textFnc(d, this);
                    }
                    if (result) return result;
                })
                .attr("x", function (d) {
                    var result;
                    if (d.textAlign.indexOf("right") > -1) {
                        //result = x.rangeBand() + 25;
                        //result = x.bandwidth() + 25;
                        result = x.bandwidth() + 25;
                    } else {
                        //result = x.rangeBand() / 2;
                        //result = x.bandwidth() / 2;
                        result = x.bandwidth() / 2;
                    }

                    return result + d.xOffset;
                })
                .attr("y", function (d) {
                    var result;
                    if (d.textAlign.indexOf("right") > -1) {
                        result = chooseAxis(d) + 3;
                    } else {
                        result = chooseAxis(d) - 5;
                    }
                    result = result + d.yOffset;
                    return result;
                })
                .attr("text-anchor", function (d) {
                    return (d.textAlign.indexOf("right") > -1) ? "right" : "middle";
                })
                .attr("class", function (d) {
                    return d.color + "Text";
                })
                .style('opacity', 0)
                .transition()
                .delay(duration)
                .duration(duration / 2)
                .style('opacity', 1)
            ;

            // arrange text
            // target
            var lineTexts = circleArea.selectAll(".lineText").filter(function (d, i, xIdx) {
                return (d.textShow == "last" && xIdx == data.length - 1)
            })

            // get max text width
            var maxTextWidth = 0;
            lineTexts.each(function (d) {
                maxTextWidth = Math.max(maxTextWidth, this.getComputedTextLength())
            });

            // arrange x
            lineTexts.each(function (d) {
                var th = d3.select(this);
                th.attr("x", parseFloat(+th.attr("x")) + (maxTextWidth - this.getComputedTextLength()) / 2)
            });

            // arrange y
            arrangeText(lineTexts);

        }
        /************************************ zeroLine ************************************/
        if (chart.select(".zeroLine").empty() && y.domain()[0] < 0) {
            chart.append("line")
                .call(setZeroLine)
            ;
        } else {
            chart.select(".zeroLine")
                .call(setZeroLine)
            ;
        }
    }

    if (data) {
        this.reload();
    }
}
