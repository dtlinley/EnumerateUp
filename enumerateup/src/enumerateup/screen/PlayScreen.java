package enumerateup.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enumerateup.ui.Clock;

public abstract class PlayScreen implements Screen {

	protected int playerScore = 0;
	protected int aiScore = 0;

	private final Clock clock;
	private final SpriteBatch batch;

	public PlayScreen(SpriteBatch batch) {
		this.batch = batch;
		this.clock = new Clock(30, 0.5f, 0.5f, 0.25f);
		clock.start();
	}

	@Override
	public void render(float delta) {
		batch.enableBlending();
		batch.begin();
		// TODO: render player scores
		// TODO: render input box
		// TODO: render locked answer
		clock.render(delta);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
