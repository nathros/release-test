package lto.manager.web.handlers.http.templates;

import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import lto.manager.common.Main;
import lto.manager.web.handlers.http.AdminHandler;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.files.FilesHandler;
import lto.manager.web.handlers.http.jobs.JobsHandler;
import lto.manager.web.handlers.http.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.tapes.TapesHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class TemplatePage {
	public static enum SelectedPage {
		Admin,
		Tapes,
		Files,
		Sandpit,
		Jobs,
		Missing
	}

	public static class TemplatePageModel {
		final DynamicHtml<BodyModel> dynamicHtml;
		final HeadModel head;
		final SelectedPage page;
		final BodyModel body;

		private TemplatePageModel(DynamicHtml<BodyModel> dynamicHtml, HeadModel head, SelectedPage page, BodyModel body) {
			this.dynamicHtml = dynamicHtml;
			this.head = head;
			this.page = page;
			this.body = body;
		}

		public static TemplatePageModel of(DynamicHtml<BodyModel> dynamicHtml, HeadModel head, SelectedPage page, BodyModel body) {
			return new TemplatePageModel(dynamicHtml, head, page, body);
		}

		public BodyModel getBodyModel() { return body; }
		public HeadModel getHeadModel() { return head; }
	}

	public static DynamicHtml<TemplatePageModel> view = DynamicHtml.view(TemplatePage::template);

	static void template(DynamicHtml<TemplatePageModel> view, TemplatePageModel model) {
		final String selected = " selected";
		final DynamicHtml<TemplatePageModel> viewTmp = view;

		try {
			view
				.html().attrLang(BaseHTTPHandler.LANG_VALUE)
				.dynamic(head -> viewTmp.addPartial(TemplateHead.view, model.getHeadModel()))
					.body()
						.header().attrClass("header-root")
						.div().attrClass("header-root-container")
							.div().attrId("nav-toggle")
								.input().attrType(EnumTypeInputType.CHECKBOX).__()
								.div() // Menu Icon
									.span().__()
									.span().__()
									.span().__()
								.__() // div
								.ul().attrId("nav-menu")
									.li()
										.a().dynamic(a -> a.attrHref(AdminHandler.PATH)
											.attrClass("icon-admin" + (model.page == SelectedPage.Admin ? selected : ""))
											.text("Admin"))
										.__()
									.__()
									.li()
										.a().dynamic(a -> a.attrHref(TapesHandler.PATH)
											.attrClass("icon-tape" + (model.page == SelectedPage.Tapes ? selected : ""))
											.text("Tapes"))
										.__()
									.__()
									.li()
										.a().dynamic(a -> a.attrHref(FilesHandler.PATH)
											.attrClass("icon-folder" + (model.page == SelectedPage.Files ? selected : ""))
											.text("Files"))
										.__()
									.__()
									.li()
										.a().dynamic(a -> a.attrHref(JobsHandler.PATH)
											.attrClass("icon-jobs" + (model.page == SelectedPage.Jobs ? selected : ""))
											.text("Jobs"))
										.__()
									.__()
									.li().dynamic(li -> {
										if (Main.DEBUG_MODE) {
											li.a()
											.attrHref(SandpitHandler.PATH).attrClass("icon-sandpit" + (model.page == SelectedPage.Sandpit ? selected : ""))
											.text("Sandpit")
											.__();
										}
									})
								.__()
							.__() // div nav-toggle
						.__() // div header-root-container
					.__() // header
					.div().attrClass("main-content")
						.div().attrClass("nav-area").__()
						.div().attrClass("main-content-wrapper")
							.dynamic(div -> viewTmp.addPartial(model.dynamicHtml, model.getBodyModel()))
						.__() // div
					.__() //div
				.__() // body
			.__(); // html

		} catch (Exception e) {
			TemplatePage.view = DynamicHtml.view(TemplatePage::template);
			throw e;
		}
	}
}