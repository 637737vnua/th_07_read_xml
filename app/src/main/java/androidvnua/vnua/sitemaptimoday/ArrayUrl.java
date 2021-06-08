package androidvnua.vnua.sitemaptimoday;

public class ArrayUrl {

    private String Id;
    private String Url;
    private String Date;
    private String Priority;
    private String ChangeFrequency;
    private String Count;

    public ArrayUrl(String id, String url, String date, String priority, String changeFrequency, String count) {
        Id = id;
        Url = url;
        Date = date;
        Priority = priority;
        ChangeFrequency = changeFrequency;
        Count = count;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public String getDate() {
        return Date;
    }

    public String getPriority() {
        return Priority;
    }

    public String getChangeFrequency() {
        return ChangeFrequency;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public void setChangeFrequency(String changeFrequency) {
        ChangeFrequency = changeFrequency;
    }
}
