package ru.npf_paker.wms;


import android.app.Dialog;
import android.arch.core.util.Function;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import data.Act;
import data.ActItem;
import helpers.Op;
import helpers.ProductBatch;
import helpers.ReindexerRequest;

public class BatchDialogFragment extends DialogFragment {
    private static BatchDialogFragment fragment = null;
    Act act;
    ProductBatch productBatch;

    public static BatchDialogFragment newInstance(Act act) {
        if (fragment == null)
            fragment = new BatchDialogFragment();
        fragment.act = act;
        fragment.productBatch = null;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        RecyclerView rv = (RecyclerView)view.findViewById(R.id.batchFoundList);
//        Если вы уверены, что размер RecyclerView не будет изменяться, вы можете добавить этот код для улучшения производительности:
//        rv.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
//        rv.setLayoutManager(llm);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ColorDrawable cd = new ColorDrawable(Color.GRAY);
            cd.setAlpha(200);
            window.setBackgroundDrawable(cd);
        }
    }

    //тэг для передачи результата обратно
    public static final String TAG_SELECTED = "batch";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_batch_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Act act = this.act;
        AutoCompleteTextView autoComplete = view.findViewById(R.id.productBatchSearchText);
        builder.setView(view)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //отправляем результат обратно
//                        Intent intent = new Intent();
//                        intent.putExtra(TAG_SELECTED, text.getText().toString());
//                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
//                    }
//                })
                .setPositiveButton("Количество", (dialog, which) -> {
                    if (productBatch != null) {
                        ActItem item = new ActItem();
                        item.productBatch = productBatch;
                        act.items.add(item);
                    }
                    ((MainActivity) getActivity()).OutcomeMode1Quantity(act);
                })
                .setNeutralButton("Добавить", null)
        ;
        Context context = getActivity().getApplicationContext();
        Gson gson = new Gson();

        Function f = (Object obj) -> {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(obj.toString());
                JSONArray jsonArray;
                try {
                    jsonArray = jsonObject.getJSONArray("product_batches");
                    ArrayList<ProductBatch> suggest = new ArrayList<ProductBatch>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        ProductBatch pb = gson.fromJson(jo.toString(), ProductBatch.class);
                        suggest.add(pb);
                    }
                    if (suggest.size() == 1) {
                        productBatch = suggest.get(0);
                        if (productBatch != null) {
                            ActItem item = new ActItem();
                            item.productBatch = productBatch;
                            act.items.add(item);
                            Toast.makeText(context, productBatch.toString(), Toast.LENGTH_SHORT).show();
                        }
                        productBatch = null;
                        autoComplete.setText("", false);
                    } else {
                        ArrayAdapter<ProductBatch> aAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, suggest);
                        autoComplete.setOnItemClickListener((adapterView, acView, position, id) -> {
                            productBatch = (ProductBatch) adapterView.getItemAtPosition(position);
                            autoComplete.setText(productBatch.product_title);
                        });
                        autoComplete.setAdapter(aAdapter);
                        aAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(obj);
            return "";
        };
        Long replyTo = MainActivity.mqttHelper.subscriptionTopic;
        DataStore.functionHashMap.put(replyTo, f);

        TextView.OnEditorActionListener onEditorActionListener = (TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
                    ) {
//                Long replyTo = MainActivity.mqttHelper.subscriptionTopic;
                ReindexerRequest rr = new ReindexerRequest();
                rr.ReplyTo = replyTo.toString();

                String newText = v.getText().toString().trim();
                Long l;
                try {
                    l = Long.valueOf("1" + newText);
                } catch (NumberFormatException e) {
                    l = Long.MIN_VALUE;
                }
                if (l > 9999999) {
                    Op op = new Op("WhereString");
                    op.Params.add("barcode");
                    op.Params.add(Op.whereOp.EQ.ordinal());
                    op.Params.add(newText);
                    rr.Operations.add(op);
                } else {
                    Op op = new Op("Match");
                    op.Params.add("text_search");
                    op.Params.add(newText);
                    rr.Operations.add(op);
                }
                Op op2 = new Op("Limit");
                op2.Params.add(10);
                rr.Operations.add(op2);

                Op selectOp = new Op("Select");
                selectOp.Params.add("product_id");
                selectOp.Params.add("product_title");
                selectOp.Params.add("batch_id");
                selectOp.Params.add("batch_title");
                rr.Operations.add(selectOp);
                MainActivity.mqttHelper.PublishToTopic("product-batch-request", gson.toJson(rr));
            }
            return true;
        };
        autoComplete.setOnEditorActionListener(onEditorActionListener);

        autoComplete.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
                productBatch = null;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Long replyTo = MainActivity.mqttHelper.subscriptionTopic;
//                ReindexerRequest rr = new ReindexerRequest();
//                rr.ReplyTo = replyTo.toString();
//
//                String newText = s.toString().trim();
//                Long l;
//                try {
//                    l = Long.valueOf("1" + newText);
//                } catch (NumberFormatException e){
//                    l = Long.MIN_VALUE;
//                }
//                if (l > 9999999) {
//                    Op op = new Op("WhereString");
//                    op.Params.add("barcode");
//                    op.Params.add(newText);
//                    rr.Operations.add(op);
//                } else {
//                    Op op = new Op("Match");
//                    op.Params.add("text_search");
//                    op.Params.add(newText);
//                    rr.Operations.add(op);
//                }
//                Op op2 = new Op("Limit");
//                op2.Params.add(10);
//                rr.Operations.add(op2);
//
//                Op selectOp = new Op("Select");
//                selectOp.Params.add("product_id");
//                selectOp.Params.add("product_title");
//                selectOp.Params.add("batch_id");
//                selectOp.Params.add("batch_title");
//                rr.Operations.add(selectOp);
//                Gson gson = new Gson();
//                MainActivity.mqttHelper.PublishToTopic("product-batch-request", gson.toJson(rr));
//                Function f = (Object obj) -> {
//                    JSONObject jsonObject;
//                    try {
//                        jsonObject = new JSONObject(obj.toString());
//                        JSONArray jsonArray;
//                        try {
//                            jsonArray = jsonObject.getJSONArray("product_batches");
//                            ArrayList<ProductBatch> suggest = new ArrayList<ProductBatch>();
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jo = jsonArray.getJSONObject(i);
//                                ProductBatch pb = gson.fromJson(jo.toString(), ProductBatch.class);
//                                suggest.add(pb);
//                            }
//                            if (suggest.size() == 1) {
//                                productBatch = suggest.get(0);
//                                if (productBatch != null) {
//                                    ActItem item = new ActItem();
//                                    item.productBatch = productBatch;
//                                    act.items.add(item);
//                                    Toast.makeText(context, productBatch.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                                productBatch = null;
//                                autoComplete.setText("", false);
//                            } else {
//                                ArrayAdapter<ProductBatch> aAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, suggest);
//                                autoComplete.setOnItemClickListener((adapterView, v, position, id) -> {
//                                    productBatch = (ProductBatch) adapterView.getItemAtPosition(position);
//                                    autoComplete.setText(productBatch.product_title);
//                                });
//                                autoComplete.setAdapter(aAdapter);
//                                aAdapter.notifyDataSetChanged();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(obj);
//                    return "";
//                };
//                DataStore.functionHashMap.put(replyTo, f);
            }

        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            button.setOnClickListener(view1 -> {
                if (productBatch != null) {
                    ActItem item = new ActItem();
                    item.productBatch = productBatch;
                    act.items.add(item);
                }
                productBatch = null;
                autoComplete.setText("", false);

                //Dismiss once everything is OK.
//                dialog.dismiss();
            });
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        FragmentActivity activity = this.getActivity();
//        mRecyclerView = (RecyclerView) activity.findViewById(R.id.batchFoundList);
//        mLayoutManager = new LinearLayoutManager(activity);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new MyBatchRecyclerViewAdapter(DummyContent.ITEMS, null);
//        mRecyclerView.setAdapter(mAdapter);
    }
}
