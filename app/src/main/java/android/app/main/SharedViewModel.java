package android.app.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Gesture>> gestures;

    public SharedViewModel() {
        gestures = new MutableLiveData<>();
        gestures.setValue(new ArrayList<Gesture>());
    }

    public void addGesture(Gesture gesture) {
        List<Gesture> copy = new ArrayList<>();
        copy.addAll(gestures.getValue());
        copy.add(gesture);
        gestures.setValue(copy);
    }

    public void deleteGesture(Gesture gesture) {
        List<Gesture> copy = new ArrayList<>();
        copy.addAll(gestures.getValue());
        copy.remove(gesture);
        gestures.setValue(copy);
    }

    public void replaceGesture(Gesture old, Gesture next) {
        List<Gesture> copy = new ArrayList<>();
        copy.addAll(gestures.getValue());
        int index = copy.indexOf(old);
        copy.remove(index);
        copy.add(index, next);
        gestures.setValue(copy);
    }

    public LiveData<List<Gesture>> getGestures() {
        return gestures;
    }

    public void setGestures(List<Gesture> gestures) {
        this.gestures.setValue(gestures);
    }
}