function drawPie(data, chartcolor, targetsvg, charttext) {
    var dataSet = [data, 100 - data]; // 데이터셋, 비율
    var graphSet;

    if (dataSet[0] >= 100) {
        graphSet = [100, 0];
    } else {
        graphSet = dataSet;
    }

    var svgWidth = 218; // SVG 요소의 넓이
    var svgHeight = 218; // SVG 요소의 높이
    var color = ["#" + chartcolor, "#CCCCCC"];

    var pie = d3.layout
        .pie()
        .sort(null)
        .value(function (d, i) {
            return d;
        });  // 원그래프 레이아웃

    var arc = d3.svg.arc()
        .innerRadius(92)
        .outerRadius(100); // 원그래프 안쪽 반지름 바깥쪽 반지름

    var pieElements = d3.select("#" + targetsvg)
        .attr("width", svgWidth)
        .attr("height", svgHeight)
        .selectAll("g")
        .data(pie(graphSet)) // 데이터에 요소를 연결
        .enter()
        .append("g")
        .attr("transform", "translate(" + svgWidth / 2 + ", " + svgHeight / 2 + ")");

    pieElements.append("path")
        .attr("class", "pie")
        .style("fill", function (d, i) {
            return color[i];
        })
        .transition()
        .duration(200)
        .delay(function (d, i) {
            return i * 200;
        })
        .ease("linear").attrTween("d", function (d, i) {
        var interpolate = d3.interpolate(
            {startAngle: d.startAngle, endAngle: d.startAngle}, // 각 부분의 시작 각도
            {startAngle: d.startAngle, endAngle: d.endAngle} // 각 부분의 종료각도
        );
        return function (t) {
            return arc(interpolate(t)); // 시간에 따라 처리
        };
    });

    // PIE진행율, 학습권장내용
    d3.select("#" + targetsvg)
        .append("text")
        .attr("class", "total")
        .attr("text-anchor", "middle")
        .attr("transform", "translate(" + (svgWidth / 2) + ", " + (svgHeight / 2 + 5) + ")").text("" + dataSet[0] + "%");

    d3.select("#" + targetsvg)
        .append("text")
        .attr("class", "pieText")
        .attr("text-anchor", "middle")
        .attr("transform", "translate(" + (svgWidth / 2) + ", " + (svgHeight / 2 + 40) + ")").text(charttext);

    pieElements.append("text")
        .attr("class", "pieNum")
        .attr("transform", function (d, i) {
            return "translate(" + arc.centroid(d) + ")";
        });
}

function drawBarChart(data, chartcolor, targetsvg, chartwidth) {
    var backDataList = [];
    for (var i in data) {
        var backData = {};
        backData["orgnm"] = data[i].orgnm;
        backData["certihoursavg"] = data[i].certihoursavg;
        backData["certihoursrate"] = 1 - data[i].certihoursrate;
        backDataList.push(backData);
    }
    var dataset1 = [
        {
            data: data
        },
        {
            data: backDataList
        }
    ];
    var barColors = ["#" + chartcolor, "#ACACAC"];
    var width = chartwidth;
    var height = data.length * 51 + 14;

    var dataset2 = dataset1.map(function (d) {
        return d.data.map(function (o, i) {
            // Structure it so that your numeric
            // axis (the stacked amount) is y
            return {
                y: o.certihoursrate,
                x: o.orgnm + " " + o.certihoursavg + "시간"
            };
        });
    });
    stack = d3.layout.stack();
    stack(dataset2);
    var dataset = dataset2.map(function (group) {
        return group.map(function (d) {
            // Invert the x and y values, and y0 becomes x0
            return {
                x: d.y,
                y: d.x,
                y0: d.x0,
                x0: d.y0
            };
        });
    });

    svg = d3.select("#" + targetsvg)
        .attr("width", width)
        .attr("height", height)
        .append("g"),
        xMax = d3.max(dataset, function (group) {
            return d3.max(group, function (d) {
                return d.x + d.x0;
            });
        }),
        xScale = d3.scale
            .linear()
            .domain([0, xMax])
            .range([0, width]),
        rates = dataset[0].map(function (d) {
            return d.y;
        }),

        yScale = d3.scale
            .ordinal()
            .domain(rates)
            .rangeRoundBands([0, height], .2),
        xAxis = d3.svg
            .axis()
            .scale(xScale)
            .orient("bottom"),
        yAxis = d3.svg
            .axis()
            .scale(yScale)
            .orient("left"),
        groups = svg.selectAll("g")
            .data(dataset)
            .enter()
            .append("g")
            .style("fill", function (d, i) {
                return barColors[i];
            }),
        rects = groups.selectAll("rect")
            .data(function (d) {
                return d;
            })
            .enter()
            .append("rect")
            .attr("x", function (d) {
                return xScale(d.x0);
            })
            .attr("y", function (d, i) {
                return yScale(d.y);
            })
            .attr("height", function (d) {
                return yScale.rangeBand();
            })
            .attr("width", function (d) {
                return xScale(d.x);
            });

    rates.forEach(function (m, i) {
        svg.append("text")
            .attr("fill", "white")
            .attr("x", 10)
            .attr("y", i * 51 + 40)
            .text(m);
    });
}

function drawLineChart(data, colorrange, targetsvg) {
    var svg = d3.select("#" + targetsvg),
        margin = {top: 20, right: 50, bottom: 30, left: 50},
        width = svg.attr("width") - margin.left - margin.right,
        height = svg.attr("height") - margin.top - margin.bottom,
        g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var x = d3.time
        .scale()
        .range([0, width], 0.5);

    var y = d3.scale
        .linear()
        .range([height, 0]);

    var color = d3.scale
        .ordinal()
        .range(colorrange);

    var xAxis = d3.svg
        .axis()
        .scale(x)
        .orient("bottom")
        .tickFormat(d3.format(".4"));

    var yAxis = d3.svg
        .axis()
        .scale(y)
        .orient("left");

    var line = d3.svg.line().x(function (d) {
        return x(d.date);
    }).y(function (d) {
        return y(d.rectype);
    });

    color.domain(d3.keys(data[0]).filter(function (key) {
        return key !== "levelnm" && key !== "_id";
    }));

    data.forEach(function (d) {
        d.date = d.levelnm;
    });

    var rectypes = color.domain().map(function (name) {
        return {
            name: name,
            values: data.map(function (d) {
                return {
                    date: d.date,
                    rectype: +d[name]
                };
            })
        };
    });

    x.domain(d3.extent(data, function (d) {
        return d.date;
    }));

    y.domain([
        d3.min(rectypes, function (c) {
            return d3.min(c.values, function (v) {
                return v.rectype;
            });
        }),
        d3.max(rectypes, function (c) {
            return d3.max(c.values, function (v) {
                return v.rectype * 1.2;
            });
        })
    ]);

    var div = d3.select("body").append("div")
        .attr("class", "graphtip")
        .style("opacity", 0);

    g.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
        .append("text")
        .attr("x", 480)
        .attr("dx", ".71em")
        .style("text-anchor", "end")
        .text("Level");
    ;

    g.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("퍼센트 (%)");

    var recTypeLine = g.selectAll(".recTypeLine")
        .data(rectypes)
        .enter()
        .append("g")
        .attr("class", "recTypeLine");

    recTypeLine.append("path")
        .attr("class", "line")
        .attr("d", function (d) {
            return line(d.values);
        })
        .style("fill", "none")
        .style("stroke", function (d) {
            return color(d.name);
        });

    recTypeLine.append("g")
        .selectAll("circle")
        .data(function (d) {
            return d.values;
        })
        .enter()
        .append("circle")
        .attr("r", 4)
        .attr("cx", function (dd) {
            return x(dd.date);
        })
        .attr("cy", function (dd) {
            return y(dd.rectype);
        })
        .attr("fill", function (d) {
            return color(this.parentNode.__data__.name);
        })
        .attr("stroke", function (d) {
            return color(this.parentNode.__data__.name);
        })
        .on("mouseover", function (d) {
            div.transition()
                .duration(200)
                .style("opacity", .9);
            div.html(d.date + " Lv. <br/>" + d.rectype + "%")
                .style("text-anchor", "middle")
                .style("left", (d3.event.pageX - 35) + "px")
                .style("top", (d3.event.pageY - 50) + "px");
        })
        .on("mouseout", function (d) {
            div.transition()
                .duration(500)
                .style("opacity", 0);
        });
}