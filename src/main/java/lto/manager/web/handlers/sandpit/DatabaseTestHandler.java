package lto.manager.web.handlers.sandpit;

import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.StaticHtml;
import lto.manager.common.database.Database;
import lto.manager.web.handlers.BaseHandler;

public class DatabaseTestHandler extends BaseHandler {

	@Override
	public void requestHadle(HttpExchange he) throws Exception {
		String response =
				StaticHtml
					.view()
						.html().attrLang(BaseHandler.LANG_VALUE)
							.head()
								.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
								.title().text("Database").__()
							.__() //head
							.body()
								.p().text("ssssw").__()
							.__() //body
						.__() //html
					.render();
		int a = 0;
		a = a / 0;
		Database d = new Database();
		d.openDatabase("config/base.db");

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
