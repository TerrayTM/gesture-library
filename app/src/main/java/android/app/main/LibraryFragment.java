package android.app.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class LibraryFragment extends Fragment {
    private SharedViewModel viewModel;
    private LinearLayout collection;
    private ScrollView collectionTop;
    private LinearLayout replaceControl;
    private TextView replaceLabel;
    private GestureView gestureView;
    private Button okButton;
    private Button clearButton;
    private TextView emptyLabel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        replaceControl = root.findViewById(R.id.replaceControl);
        collectionTop = root.findViewById(R.id.collectionTop);
        collection = root.findViewById(R.id.collection);
        replaceLabel = root.findViewById(R.id.replaceLabel);
        gestureView = root.findViewById(R.id.gestureView);
        okButton = root.findViewById(R.id.okButton);
        clearButton = root.findViewById(R.id.clearButton);
        emptyLabel = root.findViewById(R.id.emptyLabel);

        viewModel.getGestures().observe(getViewLifecycleOwner(), new Observer<List<Gesture>>() {
            @Override
            public void onChanged(@Nullable List<Gesture> gestures) {
                if (gestures.size() > 0) {
                    emptyLabel.setVisibility(View.GONE);
                } else {
                    emptyLabel.setVisibility(View.VISIBLE);
                }

                for (final Gesture gesture : gestures) {
                    LinearLayout row = new LinearLayout(getContext());
                    row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding(32, 32, 32, 32);
                    row.setGravity(Gravity.CENTER_VERTICAL);

                    ImageView image = new ImageView(getContext());
                    LinearLayout.LayoutParams imageLayout = new LinearLayout.LayoutParams(200, 200);
                    imageLayout.setMargins(0, 0, 32, 0);
                    image.setLayoutParams(imageLayout);
                    image.setImageBitmap(gesture.getBitmap());

                    View divider = new View(getContext());
                    divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(getResources().getDisplayMetrics().density * 1)));
                    divider.setBackgroundColor(Color.GRAY);

                    TextView label = new TextView(getContext());
                    label.setText(gesture.getName());
                    label.setTextSize(16);
                    label.setMaxWidth(500);

                    ImageButton replaceButton = new ImageButton(getContext());
                    replaceButton.setBackgroundResource(R.drawable.ic_baseline_refresh_24);
                    replaceButton.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

                    replaceButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            beginReplace(gesture);
                        }
                    });

                    Button deleteButton = new Button(getContext());
                    deleteButton.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                    deleteButton.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirm Delete");
                            builder.setMessage("Are you sure you want to delete this gesture?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                collection.removeAllViews();
                                collection.addView(emptyLabel);
                                viewModel.deleteGesture(gesture);
                                }
                            });
                            builder.setNegativeButton("No", null);
                            builder.show();
                        }
                    });

                    Space spaceOne = new Space(getContext());
                    spaceOne.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                    row.addView(image);
                    row.addView(label);
                    row.addView(spaceOne);
                    row.addView(replaceButton);
                    row.addView(deleteButton);

                    collection.addView(row);
                    collection.addView(divider);
                }
            }
        });

        return root;
    }

    private void beginReplace(final Gesture gesture) {
        gestureView.subscribeDrawnEvent(new GestureView.GestureEvent() {
            public void invoke() {
                okButton.setEnabled(true);
                okButton.setBackgroundResource(R.color.colorPrimary);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gestureView.clear();
                replaceControl.setVisibility(View.GONE);
                collectionTop.setVisibility(View.VISIBLE);
                okButton.setEnabled(false);
                okButton.setBackgroundResource(R.color.colorDisabled);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                collection.removeAllViews();
                collection.addView(emptyLabel);
                Gesture next = gestureView.save(gesture.getName());
                viewModel.replaceGesture(gesture, next);
                gestureView.clear();
                replaceControl.setVisibility(View.GONE);
                collectionTop.setVisibility(View.VISIBLE);
                okButton.setEnabled(false);
                okButton.setBackgroundResource(R.color.colorDisabled);
            }
        });

        replaceLabel.setText("You are replacing gesture \"" + gesture.getName() + "\".");
        collectionTop.setVisibility(View.GONE);
        replaceControl.setVisibility(View.VISIBLE);
    }
}