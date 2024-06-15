package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.*;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.io.File;

public class Collection extends AbstractListModel<Photo>
        implements Iterable<Photo>, ListSelectionListener {
    public static ArrayList<Collection> allCollections = new ArrayList<>();
    public static CollectionList colList = new CollectionList();
    public static Collection allPhotosCollection = new Collection("All Photos");
    String title;
    public boolean toSave = true;
    HashMap<File, Photo> photos = new HashMap<>();

    public Collection(String title) {
        this.title = title;
        allCollections.add(this);
    }

    public Collection() {

    }

    public void add(Photo photo) {
        photos.put(photo.getFile(), photo);
    }

    public Iterator<Photo> iterator() {
        return photos.values().iterator();
    }

    public Collection filterByTag(String tag) {
        Collection result = new Collection(tag);
        for (Photo photo : photos.values()) {
            if (photo.getTags().contains(tag)) {
                result.add(photo);
            }
        }
        return result;
    }

    public void remove(Photo photo) {
        photos.remove(photo.getFile());
    }

    public String getTitle() {
        return title;
    }

    public Stream<Photo> stream() {
        return photos.values().stream();
    }

    public Set<File> keys() {
        return photos.keySet();
    }

    public int size() {
        return photos.size();
    }

    public Photo getByKey(File key) {
        return photos.get(key);
    }

    @Override
    public String toString() {
        return title;
    }

    public java.util.Collection<Photo> getPhotos() {
        return photos.values();
    }

    @Override
    public int getSize() {
        return photos.size();
    }

    @Override
    public Photo getElementAt(int index) {
        return photos.values().stream().collect(Collectors.toList()).get(index);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void valueChanged(ListSelectionEvent e) {
        MainPanel.showPhotoInfo((Photo) ((JList) e.getSource()).getSelectedValue());
    }

    static class CollectionList extends JList<Collection> implements ListSelectionListener {
        CollectionListModel lModel;

        public CollectionList() {
            super();
            lModel = new CollectionListModel();
            setModel(lModel);
            addListSelectionListener(this);
        }

        public CollectionListModel getModel() {
            return this.lModel;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
                return;

            MainPanel.updateCollectionView();
        }

    }

    static class CollectionListModel extends AbstractListModel<Collection> {

        public CollectionListModel() {
        }

        @Override
        public int getSize() {
            return allCollections.size();
        }

        @Override
        public Collection getElementAt(int index) {
            return allCollections.get(index);
        }

        public void add(Collection collection) {
            allCollections.add(collection);
        }

        public void removeAt(int index) {
            allCollections.remove(index);
        }
    }

}
