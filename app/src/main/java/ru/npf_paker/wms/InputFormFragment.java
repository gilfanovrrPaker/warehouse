package ru.npf_paker.wms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InputFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFormFragment extends Fragment {
    private static InputFormFragment fragment = null;
    private EditText text;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    EditText quantityEditText = (EditText) findViewById(R.id.quantity);

    //    private OnFragmentInteractionListener mListener;
// Define the listener of the interface type
// listener is the activity itself
    private OnLinkItemSelectedListener mListener;


    public InputFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputFormFragment newInstance(String param1, String param2) {
        if (fragment == null)
            fragment = new InputFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            FragmentActivity activity = this.getActivity();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_input_form, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.input_quantity:
                // Handle fragment menu item
//                MainActivity ma = (MainActivity)getActivity();
//                ma.quantityDialog.show();
                openQuantityPicker();
                return true;
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_form, container, false);

        text = (EditText) view.findViewById(R.id.editText);

        Button saveButton = view.findViewById(R.id.saveButton);
        if (saveButton != null)
            saveButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText textFragment = (EditText) view.findViewById(R.id.editText);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("KMAT", textFragment.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mListener.onLinkItemSelected(jsonObject.toString());
                        }
                    });
        TextView batchTitle = view.findViewById(R.id.batchTitle);
        if (batchTitle != null)
            batchTitle.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openBatchPicker();
                        }
                    });
        return view;
    }

    @Override
    public void onStart () {
        if (text != null)
            text.setText("");
        openBatchPicker();
        super.onStart();
    }

    @Override
    public void onDestroyView () {

        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        FragmentActivity activity = this.getActivity();
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        super.onAttach(context);

        if (context instanceof OnLinkItemSelectedListener) {
            mListener = (OnLinkItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public void setQuantity(String link) {
        FragmentActivity activity = this.getActivity();
        EditText textFragment = (EditText) activity.findViewById(R.id.editText);
        if (textFragment != null)
            textFragment.setText(link);
    }

    public void setBatch(String value) {
        FragmentActivity activity = this.getActivity();
        TextView batchTitle = activity.findViewById(R.id.batchTitle);
        if (batchTitle != null)
            batchTitle.setText(value);
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    // Define the events that the fragment will use to communicate
    public interface OnLinkItemSelectedListener {
        public void onLinkItemSelected(String link);
    }

    private static final int REQUEST_QNT = 1;
    private static final int REQUEST_BATCH = 2;
    public void openQuantityPicker() {
        DialogFragment fragment = new QntDialogFragment();
        fragment.setTargetFragment(this, REQUEST_QNT);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }
    public void openBatchPicker() {
        DialogFragment fragment = new BatchDialogFragment();
        fragment.setTargetFragment(this, REQUEST_BATCH);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_QNT:
                    int quantity = data.getIntExtra(QntDialogFragment.TAG_QNT_SELECTED, -1);
                    //используем полученные результаты
                    //...
                    setQuantity(Integer.toString(quantity));
                    break;
                case REQUEST_BATCH:
                    String s = data.getStringExtra(BatchDialogFragment.TAG_SELECTED);
                    setBatch(s);
                    break;
                //обработка других requestCode
            }
//            updateUI();
        }
    }
}
