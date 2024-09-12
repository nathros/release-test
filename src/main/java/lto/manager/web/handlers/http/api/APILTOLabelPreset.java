package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordLabelPreset;
import lto.manager.web.check.FormValidator;
import lto.manager.web.check.FormValidator.ValidatorOptions;
import lto.manager.web.check.FormValidator.ValidatorStatus;
import lto.manager.web.check.FormValidator.ValidatorType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;
import lto.manager.web.resource.JSON.JSONMap;

public class APILTOLabelPreset extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_API_BASE + "ltolabelpreset/";

	private static final FormValidator nameValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueNotEmpty().valueNotNull(), "name");
	private static final FormValidator configValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueNotEmpty().valueNotNull(), "config");

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		try {
			final BodyModel bm = BodyModel.of(he, null);
			final String operation = bm.getQueryNoNull("op");
			final int userId = bm.getUserIDViaSession();

			JSONMap json = new JSONMap();

			if (operation.equals("add")) { // FIXME finish
				final String name = nameValidator.validateThrow(bm.getQueryNoNull("name"), true);
				if (Database.getUserLabelPreset(userId, name) != null) {
					throw new Exception("[" + name + "] already exists");
				}
				final String config = configValidator.validateThrow(bm.getQueryNoNull("config"), true);
				Database.addUserLabelPreset(RecordLabelPreset.of(userId, name, config));
			} else if (operation.equals("update")) {

			} else if (operation.equals("delete")) {

			} else {
				throw new Exception("Unknown operation: " + operation);
			}

			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.ok, json), CONTENT_TYPE_JSON);

		} catch (ValidatorStatus e) {
			requestHandleCompleteAPITextError(he,
					JSON.populateAPIResponse(APIStatus.error, e.getUserMessage().replaceAll("\"", "'")), CONTENT_TYPE_JSON);
		} catch (Exception e) {
			requestHandleCompleteAPITextError(he,
					JSON.populateAPIResponse(APIStatus.error, e.getMessage().replaceAll("\"", "'")), CONTENT_TYPE_JSON);
		}

	}
}