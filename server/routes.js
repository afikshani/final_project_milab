const express = require('express');
const app = express();
const server = require('http').createServer(app); //create server 
const io = require('socket.io')(server);
const port = process.env.PORT || 3000;

//request google route 
const googleMapsClient = require('@google/maps').createClient({
  key: "AIzaSyA55Fgqx8yShAamvF7B3llMO3ZrIKBZyAs"
});

let inputs = {
  destination: "Dizengoff Center Mall",//end
  origin: "IDC Herzliya",//start
  mode: "bicycling",
  avoid:"highways",
  alternatives:true
};

//function routeRequest(origin,dest){
  googleMapsClient.directions(inputs, function(err, response) {
  console.log("Response: ", JSON.stringify(response, null, ' '));
  response['color'] = "green";
  let obj = JSON.stringify(response, null, ' ');
  
  console.log(obj);
  });
//};
///weather example/// לעשות את המשיכה מהדטה בייס אסנכרונית
//////////////////////////////////////////////////////////

//socket.io server
server.listen(port, function () {
	console.log('Server is listening at port %d', port);
});

//Socket io implementation while app is alive
// io.on('connection', function (socket) {
//   console.log('Client is on connetion');
//   //when the client emits destination value, this listens and executes
//   socket.on('route_req', routeRequest(origin,dest));
//   console.log('origin dest values accepted');
//   io.emit('route_req', obj);

// }).catch(err => {
//   console.error("Error: " + err);
// });
        


