let bg;
let playerBlue;
let playerOrange;

let playerBlueDead;
let playerOrangeDead;
let scale;
let allScales;


function preload() {
  playerBlue = loadImage("img/icon/player-blue.png");
  playerOrange = loadImage("img/icon/player-orange.png");

  playerBlueDead = loadImage("img/icon/player-blue-dead.png"); // 41 53
  playerOrangeDead = loadImage("img/icon/player-orange-dead.png");
  let mapName = params.map;
  bg = loadImage('img/map/' + mapName + '.png');

  for (let i = 0; i < allScales.length;i++) {
  	if (mapName === allScales[i].map) {
  		scale = allScales[i];
	}
  }
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
			push()
			var player = players[i];
			translate((player.position.x + scale.posX) / scale.scale, (player.position.y * -1 + scale.posY) / scale.scale);

			let playerIcon;

			if (player.isAlive) {
				rotate(PI / 180 * (90 - player.rotation.y));
				if(player.team === 2) {
					playerIcon = playerBlue;
				} else {
					playerIcon = playerOrange;
				}
			} else {
				if(player.team === 2) {
					playerIcon = playerBlueDead;
				} else {
					playerIcon = playerOrangeDead;
				}
			}
			image(playerIcon, -7.5, -9.5,15,19); // 41 53
			pop()
		}
	}
}

function mouseClicked() {
	//ellipse(mouseX, mouseY, 5, 5);
	return false;
}

allScales = [
	{
		"map": "mp_angel_city",
		"posX": 6516,
		"posY": 6633,
		"scale": 11.353
	},
	{
		"map": "mp_black_water_canal",
		"posX": 5416,
		"posY": 6437,
		"scale": 12
	},
	{
		"map": "mp_coliseum",
		"posX": 1510,
		"posY": 1561,
		"scale": 3
	},
	{
		"map": "mp_coliseum_column",
		"posX": 1510,
		"posY": 1561,
		"scale": 3
	},
	{
		"map": "mp_colony02",
		"posX": 9504,
		"posY": 8961,
		"scale": 13
	},
	{
		"map": "mp_complex3",
		"posX": 10855,
		"posY": 3451,
		"scale": 12
	},
	{
		"map": "mp_crashsite3",
		"posX": 10668,
		"posY": 4576,
		"scale": 12
	},
	{
		"map": "mp_drydock",
		"posX": 5659,
		"posY": 574910.5,
		"scale": 3
	},
	{
		"map": "mp_eden",
		"posX": 4705,
		"posY": 6163,
		"scale": 11
	},
	{
		"map": "mp_forwardbase_kodai",
		"posX": 5272,
		"posY": 5671,
		"scale": 10
	},
	{
		"map": "mp_glitch",
		"posX": 8386,
		"posY": 8336,
		"scale": 16
	},
	{
		"map": "mp_grave",
		"posX": 2690,
		"posY": 3866,
		"scale": 14
	},
	{
		"map": "mp_homestead",
		"posX": 4839,
		"posY": 5871,
		"scale": 13
	},
	{
		"map": "mp_lf_deck",
		"posX": 2344,
		"posY": 2780,
		"scale": 5
	},
	{
		"map": "mp_lf_meadow",
		"posX": 3150,
		"posY": 2699,
		"scale": 6.4
	},
	{
		"map": "mp_lf_stacks",
		"posX": 2953,
		"posY": 2699,
		"scale": 6.3
	},
	{
		"map": "mp_lf_township",
		"posX": 2317,
		"posY": 2766,
		"scale": 5
	},
	{
		"map": "mp_lf_traffic",
		"posX": 2216,
		"posY": 2788,
		"scale": 5
	},
	{
		"map": "mp_lf_uma",
		"posX": 2216,
		"posY": 2788,
		"scale": 5
	},
	{
		"map": "mp_relic02",
		"posX": 7428,
		"posY": 2375,
		"scale": 15
	},
	{
		"map": "mp_rise",
		"posX": 7558,
		"posY": 7399,
		"scale": 12
	},
	{
		"map": "mp_thaw",
		"posX": 5046,
		"posY": 5515,
		"scale": 11.188
	},
	{
		"map": "mp_wargames",
		"posX": 5923,
		"posY": 5105,
		"scale": 9.5
	}
]