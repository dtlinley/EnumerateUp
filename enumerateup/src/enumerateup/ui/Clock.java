package enumerateup.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import enumerateup.EnumerateUp;

public class Clock implements Renderable {

	private final static int MAX_VERTICES = 241;
	private final static float TIME_IN_CLOCK = 10;
	private final static Color COLOR = new Color(1, 1, 1, 0.25f);

	private float time;
	private boolean done = false;
	private boolean running = false;

	private final float x;
	private final float y;
	private final float radius;
	private final Texture texture;
	private Sprite sprite;
	private final Mesh shade;
	private final float initialTime;

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
		this.texture = new Texture(Gdx.files.internal("clock.png"));
		sprite = new Sprite(texture);
		sprite.setScale(radius / sprite.getWidth());
		sprite.setPosition(-sprite.getWidth() / 2 + x, -sprite.getHeight() / 2 + y);
		this.shade = new Mesh(false, MAX_VERTICES, MAX_VERTICES, new VertexAttribute(Usage.Position, 3, "position"));
		// new VertexAttribute(Usage.ColorPacked, 4, "color"));
		resetMesh();
	}

	@Override
	public void render(float delta) {
		if (running) {
			time -= delta;
			updateOverlay();
			if (time <= 0) {
				end();
			}
		}
		// EnumerateUp.getBatch().draw(texture, 100, 100);
		sprite.draw(EnumerateUp.getBatch());
		EnumerateUp.getBatch().end();
		EnumerateUp.getBatch().begin();
		shade.render(GL10.GL_TRIANGLE_FAN);
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
		float time = (desiredVertices() - 2) * timeBetweenVertices();
		double radians = Math.PI * ((time * 2) / TIME_IN_CLOCK);
		Vector2 vec = new Vector2((float) Math.sin(radians), (float) Math.cos(radians));
		vec.mul(radius);
		vec.add(x, y);
		return vec;
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

		float vertices[] = new float[(shade.getNumVertices() * 3) + 3];
		short indices[] = new short[shade.getNumIndices() + 1];
		if (shade.getNumVertices() > 0)
			shade.getVertices(vertices);
		if (shade.getNumIndices() > 0)
			shade.getIndices(indices);
		indices[indices.length - 1] = (short) (indices.length - 1);
		vertices[vertices.length - 3] = x;
		vertices[vertices.length - 2] = y;
		vertices[vertices.length - 1] = 0;
		// vertices[vertices.length - 1] = COLOR.toFloatBits();

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
