package androidvnua.vnua.sitemaptimoday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ArrayUrl> urlArrayList;
    AdapterView adapter;
    SQLiteDatabase db;
    TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        dbConnect();
        new ReadRSS().execute("https://timoday.edu.vn/post-sitemap.xml");
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

    private class ReadRSS extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            XmlPullParserFactory parserFactory = null;
            try {
                parserFactory = XmlPullParserFactory.newInstance();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            XmlPullParser parser = null;
            try {
                parser = parserFactory.newPullParser();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            try {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setDoInput(true);
                http.connect();

                InputStream is = http.getInputStream();
                
                parser.setInput(is, null);

                processParsing(parser);
            } catch (MalformedURLException | XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                return processParsing(parser);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private String processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {
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
        return null;
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
                    "Images tinyint," +
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
        Cursor c1 = db.rawQuery("SELECT * FROM tblSiteMap", null);

        int id = c1.getColumnIndex("id");
        int idUrl = c1.getColumnIndex("URL");
        int idDate = c1.getColumnIndex("LastChange");
        int idPriority = c1.getColumnIndex("Priority");
        int idChangeFrequency = c1.getColumnIndex("ChangeFrequency");
        int idImages = c1.getColumnIndex("Images");
        int count = 0;
        while (c1.moveToNext()) {
            count++;
            int idValue = c1.getInt(id);
            String Url = c1.getString(idUrl);
            String Date = c1.getString(idDate);
            String Priority = c1.getString(idPriority);
            String ChangeFrequency = c1.getString(idChangeFrequency);
            String Images = c1.getString(idImages);

            urlArrayList.add(new ArrayUrl(String.valueOf(idValue), Url, Date, Priority, ChangeFrequency, Images));
        }

        txtCount.setText("Số bản ghi hiện có là: "+ count);
        adapter.notifyDataSetChanged();
    }

    private void clearTbl() {
        db.delete("tblSiteMap", null, null);
    }

    private void anhxa() {
        listView = (ListView) findViewById(R.id.lvList);
        txtCount = (TextView) findViewById(R.id.txtCountCollect);
        urlArrayList = new ArrayList<>();

        adapter = new AdapterView(MainActivity.this, R.layout.line_info, urlArrayList);
        listView.setAdapter(adapter);
    }
}