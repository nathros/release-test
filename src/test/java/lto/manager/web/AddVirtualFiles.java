package lto.manager.web;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.records.RecordFile;
import lto.manager.common.database.tables.records.RecordTape;

public class AddVirtualFiles {

	public static void main(String[] args) throws InterruptedException, SQLException, IOException {
		Runnable runnable = () -> { lto.manager.web.MainWeb.main(args); };
		Thread thread = new Thread(runnable);
		thread.start();
		Thread.sleep(1000);
		//
		final int tapeID = 1;

		var manu = Database.getAllTapeManufacturers();
		var types = Database.getAllTapeTypes();
		var testTape = Database.getTapeAtID(tapeID);
		if (testTape == null) {
			testTape = new RecordTape(1, manu.get(0), types.get(0), "barcode", "serial", 0, null);
			Database.addTape(testTape);
		}

		String[] fileStr = new String[] {
			"/home/", "/",
			"/emptydir/", "/home/",
			"/root/", "/home/",
			"readme.txt", "/home/root/",
			"/anotheremptydir/", "/",
			"test1.json", "/json/",
			"test2.json", "/json/",
			"test3.json", "/json/",
			"test4.json", "/json/",
			"/json/", "/",
			"/nested/", "/json/",
			"backup1.json", "/json/nested/",
			"backup2.json", "/json/nested/",
			"backup1file.json", "/json/nested/inner/",
			"backup2file.json", "/json/nested/inner/",
			"/inner/", "/json/nested/",
		};
		Random rand = new Random();

		List<RecordFile> newFiles = new ArrayList<RecordFile>();
		LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < fileStr.length; i+=2) {
			var size = fileStr[i].equals("") ? 0 : rand.nextInt(1024 * 10);
			var f = RecordFile.of(fileStr[i], fileStr[i + 1], fileStr[i], fileStr[i + 1], size, now, now, tapeID, rand.nextInt(), "");
			newFiles.add(f);
		}
		TableFile.addFiles(Database.connection, tapeID, newFiles);

		thread.interrupt();
		System.exit(0);
	}

}
