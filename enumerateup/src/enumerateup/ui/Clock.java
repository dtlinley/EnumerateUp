package enumerateup.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import enumerateup.EnumerateUp;

public class Clock implements Renderable {

	private final static int MAX_VERTICES = 241;
	private final static float TIME_IN_CLOCK = 60;
	private final static Color COLOR = new Color(0.71f, 0.71f, 0.57f, 0.05f);

	private float time;
	private boolean done = false;
	private boolean running = false;

	private final float x;
	private final float y;
	private final float radius;
	private final Sprite face;
	private final Mesh shade;
	private final float initialTime;
	private final Sprite hand;

	/**
	 * 
	 * @param time
	 *            time to be measured (in seconds)
	 */
	public Clock(float time, float x, float y, float radius) {
		this.time = time;
		this.initialTime = time;
		this.x = x;
		this.y = y;
		this.radius = radius;
		Texture faceTexture = new Texture(Gdx.files.internal("clock.png"));
		faceTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		face = new Sprite(faceTexture);
		face.setScale(2f * radius / face.getWidth());
		face.setPosition(-face.getWidth() / 2 + x, -face.getHeight() / 2 + y);

		Texture handTexture = new Texture(Gdx.files.internal("hand.png"));
		handTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		hand = new Sprite(handTexture);
		hand.setScale(2 * radius / hand.getHeight());
		hand.setPosition(-hand.getWidth() / 2 + x, -hand.getHeight() / 2 + y);

		this.shade = new Mesh(false, MAX_VERTICES, MAX_VERTICES, new VertexAttribute(Usage.Position, 2, "position"),
				new VertexAttribute(Usage.ColorPacked, 4, "color"));
		resetMesh();
	}

	@Override
	public void render(float delta) {
		if (running) {
			time -= delta;
			updateOverlay();
			hand.setRotation(-getShownTime() * 360f);
			if (time <= 0) {
				end();
			}
		}
		face.draw(EnumerateUp.getBatch());
		EnumerateUp.getBatch().end();
		// FIXME: This is not rendering with proper alpha blending. Proper blending is possible, but it seems something about the
		// spritebatch rendering is messing it up.
		shade.render(GL10.GL_TRIANGLE_FAN);
		EnumerateUp.getBatch().begin();
		hand.draw(EnumerateUp.getBatch());
	}

	private void updateOverlay() {
		if (shade.getNumVertices() > desiredVertices())
			resetMesh();

		while (shade.getNumVertices() < desiredVertices()) {
			addNextVertex();
		}
	}

	private void addNextVertex() {
		Vector2 nextVertex = findNextVertex();
		addMeshVertex(nextVertex.x, nextVertex.y);
	}

	private Vector2 findNextVertex() {
		double radians = Math.PI * 2 * getShownTime();
		Vector2 vec = new Vector2((float) Math.sin(radians), (float) Math.cos(radians));
		vec.mul(radius);
		vec.add(x, y);
		return vec;
	}

	/**
	 * The time that should be shown on the clock face, with 0 being 12 o'clock, 0.5 being 6 o'clock and 1 being 12 again
	 * 
	 * @return The time to show on the clock face as a number in [0,1]
	 */
	private float getShownTime() {
		return (desiredVertices() - 2) * timeBetweenVertices() / TIME_IN_CLOCK;
	}

	// Get the desired number of vertices for the shade mesh at this point in time based on the elapsed time, the total time the
	// clock can display and the number of vertices a full clock should have
	private int desiredVertices() {
		return (int) Math.floor((timeElapsed() % TIME_IN_CLOCK) / timeBetweenVertices()) + 2;
	}

	private float timeBetweenVertices() {
		return TIME_IN_CLOCK / ((float) MAX_VERTICES - 1);
	}

	private void addMeshVertex(float x, float y) {
		if (shade.getNumIndices() >= MAX_VERTICES) {
			resetMesh();
			return;
		}

		// 3 is the number of components a vertex has-- x, y and color. Consider extracting to a static field
		float vertices[] = new float[(shade.getNumVertices() * 3) + 3];
		short indices[] = new short[shade.getNumIndices() + 1];
		if (shade.getNumVertices() > 0)
			shade.getVertices(vertices);
		if (shade.getNumIndices() > 0)
			shade.getIndices(indices);

		indices[indices.length - 1] = (short) (indices.length - 1);
		vertices[vertices.length - 3] = x;
		vertices[vertices.length - 2] = y;
		vertices[vertices.length - 1] = COLOR.cpy().toFloatBits();

		shade.setIndices(indices);
		shade.setVertices(vertices);
	}

	private void resetMesh() {
		shade.setVertices(new float[0]);
		shade.setIndices(new short[0]);
		addMeshVertex(x, y);
		addMeshVertex(x, y + radius);
	}

	private float timeElapsed() {
		return initialTime - time;
	}

	private void end() {
		running = false;
		done = true;
	}

	public void start() {
		running = true;
	}

	public boolean isDone() {
		return done;
	}

}
