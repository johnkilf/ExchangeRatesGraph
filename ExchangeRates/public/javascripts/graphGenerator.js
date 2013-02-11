function generateGraph(dataset){

	//set graph height, width and padding
	var w = 800;
	var h = 400;
	var padding = 30;
	
	//append svg canvas
	var svg = d3.select("body")
        .append("svg")
        .attr("width", w)
        .attr("height", h);
        
	//append conversion info div
	var infoDiv = d3.select("body").append("div");
        
	//define scales for graph
	var minDate = Date.parse(dataset[dataset.length-1].date);
	var maxDate = Date.parse(dataset[0].date);
	var xScale = d3.time.scale().domain([minDate, maxDate]).range([padding, w-padding]);
	var yScale = d3.scale.linear()
                 .domain([d3.min(dataset, function(d) { return d.rate; })*(99/100), d3.max(dataset, function(d) { return d.rate; }) * (101/100)])
                 .range([h - padding, padding]);
	
	//Create path
	var d3line = d3.svg.line()
		.x(function(d){return xScale(Date.parse((d.date)));})
		.y(function(d){return yScale(d.rate);})
		.interpolate("linear");
	svg.append("svg:path")
		.attr("d", d3line(dataset))
		.style("stroke-width", 2)
		.style("stroke", "steelblue")
		.style("fill", "none");
		
	//Create dots
	svg.selectAll("circle")
		.data(dataset)
		.enter()
		.append("circle")
		.attr("cx", function(d) {
			return xScale(Date.parse(d.date));
		})
		.attr("cy", function(d) {
			return yScale(d.rate);
		})
		.attr("r", 3.5)
		.on("mouseover", function(d){mouseover(d);})
		.on("mouseout", function(d){mouseout();})
 

	//Create X axis
	var xAxis = d3.svg.axis()
              .scale(xScale)
              .orient("bottom")
			  .ticks(10);
	svg.append("g")
		.attr("class", "axis")
		.attr("transform", "translate(0," + (h - padding) + ")")
		.call(xAxis);
	//Create Y axis
	var yAxis = d3.svg.axis()
              .scale(yScale)
              .orient("left")
              .ticks(10);
	svg.append("g")
		.attr("class", "axis")
		.attr("transform", "translate(" + padding + ",0)")
		.call(yAxis);
		
	//Create Title
	svg.append("svg:text")
       .attr("x", padding + w/4)
       .attr("y", 20)
       .text("EUR-USD exchange rate - Last 90 days");
       
       
	function mouseover(d) {
		showConversionInfo(d.rate, d.date);
	}
	
	function showConversionInfo(rate, date){
		infoDiv
			.text(date + " - 1 EUR = " + rate + " USD");
	}
	 
	function mouseout() {
		infoDiv
			.text("");
	}
}