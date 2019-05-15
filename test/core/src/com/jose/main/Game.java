package com.jose.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.Random;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Box bird, floor, ceiling;
	Texture bg;
	Sprite topPipe, botPipe;

	Sound flap, hit, point;

	boolean paused = true, dead = false;
	int scoreCount = 0;
	BitmapFont score, tapToContinue;


	ArrayList<Box> pipes;
	float gravedad = -0.5f, velY = 10, jump = 34, floorHeight = 400, gapSize = 350, pipenum = 5;
	float w, h;

	
	@Override
	public void create () {

		initComponents();

		createPipes();
	}

	public void initComponents(){
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
		bird = new Box(120, 80, w / 10, h / 2, new Sprite(new Texture("yellowbird_midflap.png")));
		bird.sprite.setSize(bird.w, bird.h);

		bg = new Texture("bg.png");
		floor = new Box(w, floorHeight, 0, 0, new Sprite(new Texture("base.png")));
		ceiling = new Box(w, 100, 0, h, new Sprite(new Texture("base.png")));
		floor.sprite.setPosition(0,0);
		floor.sprite.setSize(w,floorHeight);
		topPipe = new Sprite(new Texture("toptube.png"));
		botPipe = new Sprite(new Texture("bottomtube.png"));

		flap = Gdx.audio.newSound(Gdx.files.internal("flap.mp3"));
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		point = Gdx.audio.newSound(Gdx.files.internal("point.mp3"));

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("courier.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 46;
		parameter.characters = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";
		score = generator.generateFont(parameter);
		tapToContinue = generator.generateFont(parameter);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(!paused) {
			if(!dead) {
				if (Gdx.input.justTouched()) {
					touch();
				}
				updateBirdPos();
				if (checkColission()) {
					bird = new Box(120, 80, w / 10, h / 2, new Sprite(new Texture("yellowbird_midflap.png")));
					bird.sprite.setSize(bird.w, bird.h);
					hit.play();
					dead = true;
					gravedad = 0;
					scoreCount = 0;
					createPipes();
				}
				updatePipes();

			}
			else if(Gdx.input.justTouched()){
				dead = false;
			}
			batch.begin();
			batch.draw(bg, 0, 0, w, h);
			floor.sprite.draw(batch);
			bird.sprite.setPosition(bird.x, bird.y);
			bird.sprite.setRotation(velY);
			bird.sprite.draw(batch);
			score.draw(batch, Integer.toString(scoreCount), w/2,h - h/10);
			for (int i = 0; i < pipes.size(); i++) {
				pipes.get(i).sprite.draw(batch);
			}
			batch.end();
		}
		else{
			batch.begin();
			batch.draw(bg, 0, 0, w, h);
			tapToContinue.draw(batch, "Tap to continue", w/2, h/2);
			batch.end();
			if(Gdx.input.justTouched()){
				paused = false;
				dead = false;
			}
		}
	}



	@Override
	public void dispose () {
		batch.dispose();
	}

	public void updateBirdPos(){

		bird.y = bird.y + velY + gravedad;
		velY = velY * 0.9f;
		gravedad-=0.5;
	}


	public boolean checkColission(){

		if(bird.isTouching(floor) || bird.isTouching(ceiling)){
			return true;
		}

		for(int i = 0; i < pipes.size(); i++){
			if(bird.isTouching(pipes.get(i))){
				return true;
			}
		}
		return false;
	}

	public void updatePipes(){
		for(int i = 0; i < pipes.size(); i++) {
			pipes.get(i).x-=10;
			pipes.get(i).sprite.setPosition(pipes.get(i).x, pipes.get(i).y);


			if(pipes.get(i).x + pipes.get(i).w <= 0){
				scoreCount++;
				point.play();
				pipes.remove(0);
				pipes.remove(0);
				newPipe();
				i = 0;
			}
		}
	}

	public void touch(){

		velY += jump;
		gravedad = 0;
		flap.play();
		if(Gdx.input.getX() < 300){
			bird.x = 50;
			bird.y = 500;
			createPipes();
		}
	}

	public void createPipes(){

		pipes = new ArrayList();

		newPipe();

	}

	public void newPipe(){

		Random rand = new Random();

		int bottomHeight = rand.nextInt((int) (h - floorHeight - gapSize));
		Box bot = new Box(180, bottomHeight, w, floorHeight, botPipe);
		bot.sprite.setSize(bot.w, bot.h);

		int topHeight = (int) (h - (bottomHeight + floorHeight + gapSize));
		Box top = new Box(180, topHeight, w, h - topHeight, topPipe);
		top.sprite.setSize(top.w, top.h);

		pipes.add(bot);
		pipes.add(top);

	}

}
