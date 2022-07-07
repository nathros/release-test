package lto.manager.web.handlers.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateEmptyPage;
import lto.manager.web.handlers.templates.TemplateEmptyPage.TemplateEmptyPageModel;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.models.EmptyModel;

public class SandpitHandler extends BaseHandler {
	public static final String PATH = "/sandpit";
	public static DynamicHtml<EmptyModel> view = DynamicHtml.view(SandpitHandler::body);

	static void body(DynamicHtml<EmptyModel> view, EmptyModel model) {
		view
			.div().attrStyle("text-align:center")
				.p().a().attrHref(DatabaseTestHandler.PATH).text("Database test").__().__()
			.__(); //  div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		TemplateHeadModel thm = TemplateHeadModel.of("Sandpit");
		TemplateEmptyPageModel tepm = TemplateEmptyPageModel.of(view, thm);
		String response = TemplateEmptyPage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
