package androidvnua.vnua.sitemaptimoday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ArrayUrl> urlArrayList;
    AdapterView adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        dbConnect();
        paserXML();
        getData();

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,
                            "ID:" + urlArrayList.get(position).getId() +"\n"
                            + "URL: " + urlArrayList.get(position).getUrl() +"\n"
                            + "Số ảnh của trang: " + urlArrayList.get(position).getCount(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void paserXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            InputStream is = getAssets().open("sitemap.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<InfoSite> urls = new ArrayList<>();
        int eventType = parser.getEventType();
        InfoSite currentURL = null;
        int count = 0;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if ("url".equals(eltName)) {
                        count = 0;
                        currentURL = new InfoSite();
                        urls.add(currentURL);
                    } else if (currentURL != null) {
                        if ("loc".equals(eltName)) {
                            currentURL.url = parser.nextText();
                        } else if ("lastmod".equals(eltName)) {
                            currentURL.date = parser.nextText();
                        } else if ("priority".equals(eltName)) {
                            currentURL.priority = parser.nextText();
                        } else if ("changefreq".equals(eltName)) {
                            currentURL.changefreq = parser.nextText();
                        } else if ("image:image".equals(eltName)) {
                            count++;
                            currentURL.count = String.valueOf(count);
                        }
                    }
                break;
            }
            eventType = parser.next();
        }

        printList(urls);
    }

    private void printList(ArrayList<InfoSite> list) {
        ContentValues data = new ContentValues();
        for (InfoSite url : list) {
            data.put("URL", url.url);
            data.put("LastChange", url.date);
            data.put("Images", url.count);
            data.put("Priority", url.priority);
            data.put("ChangeFrequency", url.changefreq);
            db.insert("tblSiteMap", null, data);
        }

    }

    private void dbConnect() {
        try {
            db = SQLiteDatabase.openDatabase("/data/data/androidvnua.vnua.sitemaptimoday/myDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("CREATE TABLE IF NOT EXISTS tblSiteMap (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "URL varchar(200) UNIQUE," +
                    "Images varchar(20) /*tinyint*/," +
                    "Priority float," +
                    "ChangeFrequency varchar(20)," +
                    "LastChange datetime" +
                    ");"
            );
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        urlArrayList.clear();
        String table = "tblSiteMap";
        String [] colums = {"id", "URL", "LastChange", "Priority", "ChangeFrequency", "Images"};

        Cursor c1 = db.query(table, colums, null, null, null, null, "id");

        int id = c1.getColumnIndex("id");
        int idUrl = c1.getColumnIndex("URL");
        int idDate = c1.getColumnIndex("LastChange");
        int idPriority = c1.getColumnIndex("Priority");
        int idChangeFrequency = c1.getColumnIndex("ChangeFrequency");
        int idImages = c1.getColumnIndex("Images");

        while (c1.moveToNext()) {
            int idValue = c1.getInt(id);
            String Url = c1.getString(idUrl);
            String Date = c1.getString(idDate);
            String Priority = c1.getString(idPriority);
            String ChangeFrequency = c1.getString(idChangeFrequency);
            String Images = c1.getString(idImages);

            urlArrayList.add(new ArrayUrl(String.valueOf(idValue), Url, Date, Priority, ChangeFrequency, Images));
        }

        adapter.notifyDataSetChanged();
    }

    private void clearTbl() {
        db.delete("tblSiteMap", null, null);
    }

    private void anhxa() {
        listView = (ListView) findViewById(R.id.lvList);
        urlArrayList = new ArrayList<>();

        adapter = new AdapterView(MainActivity.this, R.layout.line_info, urlArrayList);
        listView.setAdapter(adapter);
    }
}