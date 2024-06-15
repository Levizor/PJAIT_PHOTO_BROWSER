package src;

import java.io.File;
import java.io.Serializable;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Photo implements Comparable<Photo>, Serializable {
    private File file;
    private String title;
    private String description = new String();
    private Date date;
    private ArrayList<String> tags;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public static HashMap<String, Photo> allPhotos = new HashMap<>() {
        @Override
        public Photo put(String key, Photo value) {
            Photo res = super.put(key, value);
            Collection.allPhotosCollection.add(value);
            return res;
        }
    };

    public Photo() {
    }

    public Photo(String pathstring) throws NoSuchFileException {
        file = new File(pathstring);
        if (Collection.allPhotosCollection.keys().contains(new File(pathstring))) {
            if (!file.exists()) {
                throw new NoSuchFileException(pathstring);
            }
            Photo copy = Collection.allPhotosCollection.getByKey(file);
            title = copy.getTitle();
            description = copy.getDescription();
            date = copy.getDate();
            tags = copy.getTags();
        } else {
            tags = new ArrayList<>();
            title = pathstring;

        }

        Collection.allPhotosCollection.add(this);
    }

    public Photo copy() {
        try {
            return new Photo(getFile().toPath().toString(), getTitle(), getDescription(), getTags(), getDate());
        } catch (Exception e) {
            return new Photo();
        }
    }

    public Photo(String path, String title, String description, ArrayList<String> tags, Date date) throws Exception {
        this(path);
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.date = date;

    }

    public boolean hasTag(String searchTag) {
        for (String tag : tags) {
            if (tag.equals(searchTag))
                return true;
        }
        return false;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    // public void addToMap() {
    // allPhotos.put(file.getPath(), this);
    // }

    public void setDate(String date) throws java.text.ParseException {
        this.date = sdf.parse(date);
    }

    public void setDescription(String newDiscription) {
        description = newDiscription;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void setTags(List<String> tags) {
        this.tags.clear();
        for (String tag : tags) {
            addTag(tag);
        }
    }

    public ArrayList<String> getTags() {
        if (tags != null)
            return tags;

        ArrayList<String> ar = new ArrayList<String>();
        ar.add("");
        return ar;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }

    public Date getDate() {
        if (date != null) {
            return date;
        }

        return new Date();
    }

    public String getDescription() {
        return description;
    }

    public int compareTo(Photo photo) {
        return this.title.compareTo(photo.getTitle());
    }

    @Override
    public String toString() {
        return title;
    }

}
