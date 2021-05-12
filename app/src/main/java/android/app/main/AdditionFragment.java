package android.app.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class AdditionFragment extends Fragment {
    private SharedViewModel viewModel;
    private Button okButton;
    private Button clearButton;
    private GestureView gestureView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addition, container, false);
        okButton = root.findViewById(R.id.okButton);
        clearButton = root.findViewById(R.id.clearButton);
        gestureView = root.findViewById(R.id.gestureView);

        gestureView.subscribeDrawnEvent(new GestureView.GestureEvent() {
            public void invoke() {
                okButton.setEnabled(true);
                okButton.setBackgroundResource(R.color.colorPrimary);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                okButton.setEnabled(false);
                okButton.setBackgroundResource(R.color.colorDisabled);
                gestureView.clear();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                requestSave();
            }
        });

        return root;
    }

    private void requestSave() {
        final EditText textBox = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Gesture Name");
        builder.setMessage("Please specify the name of the gesture.");
        builder.setView(textBox);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!textBox.getText().toString().trim().equals("")) {
                    viewModel.addGesture(gestureView.save(textBox.getText().toString().trim()));
                    okButton.setEnabled(false);
                    okButton.setBackgroundResource(R.color.colorDisabled);
                    gestureView.clear();
                } else {
                    requestSave();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}