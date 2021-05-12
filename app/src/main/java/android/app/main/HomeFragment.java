package android.app.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private SharedViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final GestureView gestureView = root.findViewById(R.id.gestureView);
        final LinearLayout resultContainer = root.findViewById(R.id.resultContainer);
        final TextView statusLabel = root.findViewById(R.id.statusLabel);

        gestureView.subscribeDrawnEvent(new GestureView.GestureEvent() {
            public void invoke() {
                Gesture current = gestureView.save("Main");
                List<ScoreTuple> tuples = new ArrayList<>();

                for (Gesture gesture : viewModel.getGestures().getValue()) {
                    tuples.add(new ScoreTuple(current.similarityScore(gesture), gesture));
                }

                Comparator<ScoreTuple> comparator = new Comparator<ScoreTuple>() {
                    public int compare(ScoreTuple one, ScoreTuple two) {
                        return Float.valueOf(one.getScore()).compareTo(two.getScore());
                    }
                };

                Collections.sort(tuples, comparator);
                resultContainer.removeAllViews();
                int length = Math.min(tuples.size(), 3);

                if (length > 0) {
                    Space spacer = new Space(getContext());
                    spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    resultContainer.addView(spacer);
                }

                for (int i = 0; i < length; ++i) {
                    LinearLayout entry = new LinearLayout(getContext());
                    entry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    entry.setOrientation(LinearLayout.VERTICAL);
                    entry.setGravity(Gravity.CENTER);

                    ImageView image = new ImageView(getContext());
                    image.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                    image.setImageBitmap(tuples.get(i).getGesture().getBitmap());

                    if (i == 0) {
                        image.setColorFilter(Color.argb(60, 175, 247, 17));
                    }

                    TextView label = new TextView(getContext());
                    label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    label.setText(tuples.get(i).getGesture().getName());
                    label.setTextSize(12);
                    label.setMaxWidth(200);

                    entry.addView(image);
                    entry.addView(label);

                    resultContainer.addView(entry);

                    if (i + 1 < length) {
                        Space spacer = new Space(getContext());
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        resultContainer.addView(spacer);
                    }
                }

                if (length > 0) {
                    Space spacer = new Space(getContext());
                    spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    resultContainer.addView(spacer);
                } else {
                    statusLabel.setText("You have no saved gestures!");
                    resultContainer.addView(statusLabel);
                }
            }
        });

        return root;
    }
}