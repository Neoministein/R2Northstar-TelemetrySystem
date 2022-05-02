let bg;
let playerBlue;
let playerOrange;


function preload() {
  playerBlue = loadImage("img/player-blue.png");
  playerOrange = loadImage("img/player-orange.png");
  bg = loadImage('img/forward_base_kodai.png');
}

function setup() {
  // The background image must be the same size as the parameters
  // into the createCanvas() method. In this program, the size of
  // the image is 720x400 pixels.
  createCanvas(1024, 1024);
}

function draw() {
  background(bg);
  
  let currentJsonData = json;
	
	//console.log(currentJsonData)
	if(currentJsonData) {
		let players = currentJsonData.players;
  
		for(var i = 0; i < players.length; i++) {
		var player = players[i];
	
		translate((player.position.x + 5325) / 10.1, (player.position.y * -1 + 5700) / 10.1);
		rotate(PI / 180 * (90 - player.rotation.y));
	
		if(player.team == 2) {
			image(playerBlue, -7.5, -9.5, 15, 19);
		} else {
			image(playerOrange, -7.5, -9.5, 15, 19);
		}
	}
	}
}