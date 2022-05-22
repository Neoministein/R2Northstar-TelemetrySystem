let bg;


function preload() {
  let mapName = 'mp_forwardbase_kodai';
  bg = loadImage('/img/map/' + mapName + '.png');
}

function setup() {
  // The background image must be the same size as the parameters
  // into the createCanvas() method. In this program, the size of
  // the image is 720x400 pixels.
  createCanvas(1024, 1024);
}

function draw() {
	background(bg);
	if(json) {
		let highest = 5;
		for (let i = 0; i < json.entries.length; i++) {
			let c = color(json.entries[i].count / highest * 255, 255, 255);

			fill(c);
			noStroke();
			//set(json.entries[i].posX, json.entries[i].posY, c);
			rect(json.entries[i].x, json.entries[i].y, 4, 4);
		}
	}
}