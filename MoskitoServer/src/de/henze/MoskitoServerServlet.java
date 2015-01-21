package de.henze;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class MoskitoServerServlet extends HttpServlet {
	DatastoreService datastore;
	Key gameKey;
	int max = 10;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		String game = req.getParameter("game");
		String name = req.getParameter("name");
		String score = req.getParameter("score");
		String level = req.getParameter("level");

		if (game != null && name != null && score != null && level != null) {
			addHighscore(game, name, score, level);
		}
		getHighscore(resp);
	}

	private void getHighscore(HttpServletResponse resp) {
		Query query = new Query("highscore", gameKey);
		datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> highscore_list = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(max));
		query.addSort("points", Query.SortDirection.DESCENDING);
		for (Entity e : highscore_list) {
			try {
				resp.getWriter().println("Spieler "+
						e.getProperty("playername") + " erreichte "
								+ e.getProperty("points")+" Punkte in Level "+e.getProperty("level")+".");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void addHighscore(String _game, String _name, String _points, String _level) {
		datastore = DatastoreServiceFactory.getDatastoreService();
		gameKey = KeyFactory.createKey("game", _game);
		Entity highscore_entity = new Entity("highscore", gameKey);
		highscore_entity.setProperty("playername", _name);
		highscore_entity.setProperty("points", _points);
		highscore_entity.setProperty("level", _level);
		datastore.put(highscore_entity);
	}

}