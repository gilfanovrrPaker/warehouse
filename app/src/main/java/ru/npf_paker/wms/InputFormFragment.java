package ru.npf_paker.wms;

import android.app.Activity;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import helpers.Op;
import helpers.ProductBatch;
import helpers.ReindexerRequest;
import helpers.RestResponse;
import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InputFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFormFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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

        text = view.findViewById(R.id.editText);

        Button saveButton = view.findViewById(R.id.saveButton);
        if (saveButton != null)
            saveButton.setOnClickListener(
                    v -> {
                        EditText textFragment = view.findViewById(R.id.editText);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("KMAT", textFragment.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mListener.onLinkItemSelected(jsonObject.toString());
                    });
        TextView batchTitle = view.findViewById(R.id.batchTitle);
        if (batchTitle != null)
            batchTitle.setOnClickListener(
                    v -> openBatchPicker());

        AutoCompleteTextView autoComplete = view.findViewById(R.id.autoCompleteTextView);
        Context context = this.getContext();
        autoComplete.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                new getJson().execute(newText);
//                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hi");
                Long replyTo = MainActivity.mqttHelper.subscriptionTopic;
                ReindexerRequest rr = new ReindexerRequest();
                rr.ReplyTo = replyTo.toString();

//                String newText = s.toString();
//                Long l;
//                try {
//                    l = Long.valueOf(newText);
//                } catch (NumberFormatException e){
//                    l = Long.MIN_VALUE;
//                }
//                if (l > 9999999) {
//                    Op op = new Op();
//                    op.Method = "WhereString";
//                    op.Params.add("barcode");
//                    op.Params.add(newText);
//                    rr.Operations.add(op);
//                } else {
//                    Op op = new Op();
//                    op.Method = "Match";
//                    op.Params.add("text_search");
//                    op.Params.add(newText);
//                    rr.Operations.add(op);
//                };
//                Op op2 = new Op();
//                op2.Method = "Limit";
//                op2.Params.add(10);
//                rr.Operations.add(op2);
//
//                Op selectOp = new Op();
//                selectOp.Method = "Select";
//                selectOp.Params.add("product_id");
//                selectOp.Params.add("product_title");
//                selectOp.Params.add("batch_id");
//                selectOp.Params.add("batch_title");
//
//                rr.Operations.add(selectOp);
                Gson gson = new Gson();
                MainActivity.mqttHelper.PublishToTopic("product-batch-request", gson.toJson(rr));
//                DataStore.promisesMap.put(replyTo,  single);
                Function f = (Object obj) -> {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(obj.toString());
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = jsonObject.getJSONArray("product_batches");
                            ArrayList<ProductBatch> suggest = new ArrayList<ProductBatch>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                ProductBatch pb = gson.fromJson(jo.toString(), ProductBatch.class);
                                suggest.add(pb);
                            }
                            ArrayAdapter<ProductBatch> aAdapter = new ArrayAdapter<ProductBatch>(context, R.layout.support_simple_spinner_dropdown_item, suggest);
                            autoComplete.setOnItemClickListener( (adapterView, v, position, id) -> {
                                ProductBatch pb = (ProductBatch) adapterView.getItemAtPosition(position);
                                autoComplete.setText(pb.product_title);
                            });
                            autoComplete.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(obj);
                    return "";
                };
                DataStore.functionHashMap.put(replyTo, f);
            }

        });
        return view;
    }

//    class getJson extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... key) {
//            String newText = key[0];
//            newText = newText.trim();
//            newText = newText.replace(" ", "+");
//            try{
//                HttpClient hClient = new DefaultHttpClient();
//                HttpGet hGet = new HttpGet("http://en.wikipedia.org/w/api.php?action=opensearch&search="+newText+"&limit=8&namespace=0&format=json");
//                ResponseHandler<String> rHandler = new BasicResponseHandler();
//                data = hClient.execute(hGet,rHandler);
//                suggest = new ArrayList<String>();
//                JSONArray jArray = new JSONArray(data);
//                for(int i=0;i<jArray.getJSONArray(1).length();i++){
//                    String SuggestKey = jArray.getJSONArray(1).getString(i);
//                    suggest.add(SuggestKey);
//                }
//
//            }catch(Exception e){
//                Log.w("Error", e.getMessage());
//            }
//            runOnUiThread(new Runnable(){
//                public void run(){
//                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item,suggest);
//                    autoComplete.setAdapter(aAdapter);
//                    aAdapter.notifyDataSetChanged();
//                }
//            });
//
//            return null;
//        }
//
//    }

    @Override
    public void onStart() {
        if (text != null)
            text.setText("");
        openBatchPicker();
        super.onStart();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        EditText textFragment = activity.findViewById(R.id.editText);
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
