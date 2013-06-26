package enumerateup.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class PlayScreen implements Screen {

	protected int playerScore = 0;
	protected int aiScore = 0;

	private SpriteBatch batch;

	public PlayScreen(SpriteBatch batch) {
		this.batch = batch;
	}

	@Override
	public void render(float delta) {
		batch.begin();
		// TODO: render player scores
		// TODO: render input box
		// TODO: render locked answer
		// TODO: render timer
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
