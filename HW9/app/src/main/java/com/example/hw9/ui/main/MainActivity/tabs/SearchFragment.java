package com.example.hw9.ui.main.MainActivity.tabs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.example.hw9.SharedGeneralPurposeMethods;
import com.example.hw9.RecycleViewDecorator;
import com.example.hw9.ui.main.MainActivity.adapters.AutoCompleteArrayAdapter;
import com.example.hw9.ui.main.MainActivity.adapters.EventResultsRecycleViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String cached_googleMap_api_key;
    // autocomplete variables
    private ArrayAdapter<String> autoCompleteAdapter;

    private ArrayList<ArrayList<String>> list_for_table = new ArrayList<>();

    private SharedGeneralPurposeMethods shared;

    private EventResultsRecycleViewAdapter event_results_adapter;

    private  RecyclerView event_search_recycleView;

    private ProgressBar event_search_pb;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init class variables
        shared = new SharedGeneralPurposeMethods();
        event_search_recycleView = view.findViewById(R.id.event_recycle_view);
        event_search_pb = view.findViewById(R.id.event_search_progress_bar);
        // Remove this after
        dev_inputs_placeholder(view);
        // init category dropdown spinner
        init_category_spinner(view);
        // init autocomplete array adapter
        init_ac_arrayAdapter(view);
        // hide/show location input
        toggle_location_input(view);
        //clear btn
        clear(view);
        // submit btn
        submit_with_inputs_validation(view);
        // black back btn
        nav_back_to_search(view);
        // autocomplete suggestions http request
        get_autoComplete_suggestions(view);
        // margin between each item in event result recycleView
        event_search_recycleView.addItemDecoration(new RecycleViewDecorator(50));
    }

    // Remove this function after
    private void dev_inputs_placeholder(View view){
        final AutoCompleteTextView keyword_input = view.findViewById(R.id.keyword_input);
        final EditText location_input = view.findViewById(R.id.location_input);
        keyword_input.setText("P!NK");
        location_input.setText("New York");
    }
    /* Custom Code Start Here */

    private void init_category_spinner(View view) {
        Spinner spinner = view.findViewById(R.id.category_input);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.category_array)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE); /* set text color for the selected item */
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE); /* set text color for the dropdown items */
                return view;
            }
        };
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);
    }

    private void init_ac_arrayAdapter(View view){
        autoCompleteAdapter = new AutoCompleteArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        AutoCompleteTextView autoComplete_tv = view.findViewById(R.id.keyword_input);
        autoComplete_tv.setThreshold(1);
        autoComplete_tv.setAdapter(autoCompleteAdapter);
    }
    private void toggle_location_input(View view) {
        SwitchCompat switchCompat = view.findViewById(R.id.auto_detect_switch);
        final EditText location_input = view.findViewById(R.id.location_input);

        switchCompat.setOnCheckedChangeListener((buttonView, is_checked) -> {
            if (is_checked) {
                location_input.setVisibility(View.GONE);
            } else {
                location_input.setVisibility(View.VISIBLE);
            }
        });


    }

    private void nav_back_to_search(View view){
        final CardView search_from = view.findViewById(R.id.search_form);
        final ImageButton black_back_btn = view.findViewById(R.id.black_back_btn);
        final TextView black_back_btn_prompt = view.findViewById(R.id.black_back_btn_prompt);
        final RecyclerView event_results_recycleView = view.findViewById(R.id.event_recycle_view);

        black_back_btn .setOnClickListener(v -> {
            search_from.setVisibility(View.VISIBLE);
            black_back_btn.setVisibility(View.GONE);
            black_back_btn_prompt.setVisibility(View.GONE);
            event_results_recycleView.setVisibility(View.GONE);
            // Clear event results reference and recycleView
            list_for_table = new ArrayList<>();
            EventResultsRecycleViewAdapter event_search_adapter = new EventResultsRecycleViewAdapter(list_for_table);
            event_results_recycleView.setAdapter(event_search_adapter);
            //event_results_recycleView.addItemDecoration(new EventResultsDecorator(-50));
        });

    }

    private void clear(View view) {
        final Button clear_btn = view.findViewById(R.id.clear_btn);
        final AutoCompleteTextView keyword_input = view.findViewById(R.id.keyword_input);
        final EditText distance_input = view.findViewById(R.id.distance_input);
        final Spinner category_spinner = view.findViewById(R.id.category_input);
        final SwitchCompat auto_detect_switch = view.findViewById(R.id.auto_detect_switch);
        final EditText location_input = view.findViewById(R.id.location_input);


        clear_btn.setOnClickListener(v -> {
            keyword_input.setText("");
            if(!TextUtils.isEmpty(distance_input.getText().toString()))distance_input.setText(R.string.distance_default);
            category_spinner.setSelection(0);
            auto_detect_switch.setChecked(false);
            location_input.setText("");
        });

    }

    private void submit_with_inputs_validation(View view){

        final Button search = view.findViewById(R.id.search_btn);

        search.setOnClickListener(v -> {
            final AutoCompleteTextView keyword_input = view.findViewById(R.id.keyword_input);
            final EditText distance_input = view.findViewById(R.id.distance_input);
            final Spinner category_input = view.findViewById(R.id.category_input);
            final EditText location_input = view.findViewById(R.id.location_input);
            final SwitchCompat auto_detect_switch = view.findViewById(R.id.auto_detect_switch);

            String keyword = keyword_input.getText().toString().trim();
            String distance = distance_input.getText().toString().trim();
            String category = category_input.getSelectedItem().toString().trim();
            String location =location_input.getText().toString().trim();
            boolean isSwitchOn = auto_detect_switch.isChecked();

            if (keyword.isEmpty() || distance.isEmpty() || (location.isEmpty() && !isSwitchOn)) {
                Snackbar snackBar = Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_SHORT);
                snackBar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_snack_bar)));
                TextView snackBar_text = snackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                snackBar_text.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                snackBar.show();
            }else{
                final CardView search_from = view.findViewById(R.id.search_form);
                final ImageButton black_back_btn = view.findViewById(R.id.black_back_btn);
                final TextView black_back_btn_prompt = view.findViewById(R.id.black_back_btn_prompt);
                final RecyclerView event_results_recycleView = view.findViewById(R.id.event_recycle_view);

                // UI logic
                search_from.setVisibility(View.GONE);
                event_search_pb.setVisibility(View.VISIBLE);
                black_back_btn.setVisibility(View.VISIBLE);
                black_back_btn_prompt.setVisibility(View.VISIBLE);
                event_results_recycleView.setVisibility(View.VISIBLE);

                if(!isSwitchOn) {
                    String preprocessed_location = preprocess_google_geoLoc_address(location);
                    String api_key = get_googleMap_api_key();
                    String base_url = "https://maps.googleapis.com/maps/api/geocode/json";
                    Uri.Builder builder = Uri.parse(base_url).buildUpon();
                    builder.appendQueryParameter("address", preprocessed_location);
                    builder.appendQueryParameter("key", api_key);
                    String url = builder.build().toString();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, resp -> {
                                String status = resp.optString("status");
                                if (!status.equals("ZERO_RESULTS")) {
                                    String lat, lng;
                                    Gson gson = new Gson();
                                    JsonObject respObj = gson.fromJson(String.valueOf(resp), JsonObject.class);
                                    JsonArray results = respObj.getAsJsonArray("results");

                                    if (results != null && results.size() > 0) {
                                        JsonObject result = results.get(0).getAsJsonObject();
                                        JsonObject geometry = result.getAsJsonObject("geometry");

                                        if (geometry != null) {
                                            JsonObject location_obj = geometry.getAsJsonObject("location");

                                            if (location_obj != null) {
                                                lat = location_obj.get("lat").getAsString().trim();
                                                lng = location_obj.get("lng").getAsString().trim();
                                                get_event_results(lat, lng, keyword, distance, category, view);
                                            }
                                        }
                                    }

                                }else {
                                    Log.d("GeoLoc status", "not result");
                                    event_search_pb.setVisibility(View.GONE);
                                }

                            }, error -> {
                                // Handle the error
                                event_search_pb.setVisibility(View.GONE);
                                Log.e("Error", "Volley Error: " + error.getMessage());
                            });
                    MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);


                }
            }
        });

    }

    /* Volley http requests */
    private void get_autoComplete_suggestions(View view){
        final AutoCompleteTextView keyword_input = view.findViewById(R.id.keyword_input);
        final ProgressBar auto_complete_pb = view.findViewById(R.id.ac_progressBar);


        keyword_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do something before the text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do something during the text change
                auto_complete_pb.setVisibility(View.VISIBLE);
                String text = s.toString().trim();
                // call your method here, passing the updated text as a parameter
                if(!text.trim().isEmpty()) {
                    String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/auto-complete";
                    Uri.Builder builder = Uri.parse(backend_url).buildUpon();
                    builder.appendQueryParameter("keyword", text);
                    String url = builder.build().toString();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, resp -> {
                                JSONObject embedded = resp.optJSONObject("_embedded");
                                JSONArray attractions_arr = embedded != null ? embedded.optJSONArray("attractions") : null;

                                if (embedded == null || attractions_arr == null) {
                                    List<String> ac_suggestions = new ArrayList<>();
                                    autoCompleteAdapter.clear();
                                    autoCompleteAdapter.addAll(ac_suggestions);
                                    autoCompleteAdapter.notifyDataSetChanged();
                                    auto_complete_pb.setVisibility(View.GONE);
                                    Log.e("Exception", "autocomplete suggestion is null");
                                    return;
                                }

                                // Use Gson to parse the JSON array into a list of maps
                                Gson gson = new Gson();
                                Type attractions_list_type = new TypeToken<List<Map<String, Object>>>() {
                                }.getType();
                                List<Map<String, Object>> attractions = gson.fromJson(attractions_arr.toString(), attractions_list_type);

                                // Extract the names of attractions
                                List<String> ac_suggestions = new ArrayList<>();
                                for (Map<String, Object> attraction_obj : attractions) {
                                    String name = (String) attraction_obj.get("name");
                                    if (name != null) {
                                        ac_suggestions.add(name);
                                    }
                                }
                                // Update the ArrayAdapter with the new list of names
                                autoCompleteAdapter.clear();
                                autoCompleteAdapter.addAll(ac_suggestions);
                                autoCompleteAdapter.notifyDataSetChanged();
                                auto_complete_pb.setVisibility(View.GONE);
                                // Handle the attractions names list
                                Log.d("debug", "Attractions names: " + ac_suggestions);

                            }, error -> {
                                // Handle the error
                                Log.e("Error", "Volley Error: " + error.getMessage());
                            });
                    MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);
                }else{
                    // 2Somehow ac api return result with empty key
                    List<String> ac_suggestions = new ArrayList<>();
                    autoCompleteAdapter.clear();
                    autoCompleteAdapter.addAll(ac_suggestions);
                    autoCompleteAdapter.notifyDataSetChanged();
                    auto_complete_pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
    }

    private void get_event_results(String lat, String lng, String keyword, String distance, String category, View view){
        String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/event-search";
        Uri.Builder builder = Uri.parse(backend_url).buildUpon();
        builder.appendQueryParameter("lat", lat);
        builder.appendQueryParameter("lng", lng);
        builder.appendQueryParameter("keyword", keyword);
        builder.appendQueryParameter("distance", distance);
        builder.appendQueryParameter("category", category);
        String url = builder.build().toString();

        JsonObjectRequest json_obj_request = new JsonObjectRequest
                (Request.Method.GET, url, null, resp -> {
                    Gson gson = new Gson();
                    JsonObject gson_resp = gson.fromJson(resp.toString(), JsonObject.class);

                    JsonArray events;

                    try {
                        events = gson_resp.getAsJsonObject("_embedded").getAsJsonArray("events");
                    } catch (NullPointerException e) {
                        Log.e("Exception", "Exception: No Search Results Available");
                        event_search_pb.setVisibility(View.GONE);
                        return;
                    }


                    int total_events = events.size();

                    for (int i = 0; i < total_events; i++) {
                        JsonObject resp_obj = events.get(i).getAsJsonObject();
                        ArrayList<String> buffer_arr = new ArrayList<>();

                        if (resp_obj.has("dates")) {
                            buffer_arr_append(buffer_arr, "dates", resp_obj);
                        } else {
                            buffer_arr.add("");
                            buffer_arr.add("");
                        }

                        if (resp_obj.has("images")) {
                            buffer_arr_append(buffer_arr, "images", resp_obj);
                        } else {
                            buffer_arr.add("");
                        }

                        if (resp_obj.has("name")) {
                            buffer_arr_append(buffer_arr, "name", resp_obj);
                        } else {
                            buffer_arr.add("");
                        }

                        if (resp_obj.has("classifications")) {
                            buffer_arr_append(buffer_arr, "classifications", resp_obj);
                        } else {
                            buffer_arr.add("");
                        }

                        if (resp_obj.has("_embedded")) {
                            buffer_arr_append(buffer_arr, "_embedded", resp_obj);
                        } else {
                            buffer_arr.add("");
                        }

                        if (resp_obj.has("id")) {
                            buffer_arr_append(buffer_arr, "id", resp_obj);
                        } else {
                            buffer_arr.add("");
                        }

                        list_for_table.add(buffer_arr);
                    }
                    sort_by_dateTime(list_for_table);

                    Log.d("table", list_for_table.toString());

                    // event search recycle view
                    event_results_adapter = new EventResultsRecycleViewAdapter(list_for_table);
                    shared.generate_linearLayout_recycleView(getContext(),event_search_recycleView,event_results_adapter);

                    // UI ready
                    event_search_pb.setVisibility(View.GONE);

                }, error -> {
                    // Handle the error
                    Log.e("Error", "Volley Error Ticketmaster Event Result: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(json_obj_request);
    }

    private void buffer_arr_append(ArrayList<String> buffer_arr, String header, JsonObject resp_obj) {
        JsonElement header_element = resp_obj.get(header);

        if (header_element == null) {
            return;
        }

        switch (header) {
            case "dates":
                JsonObject time_obj = ((JsonElement) header_element).getAsJsonObject();
                String start_local_date = shared.general_json_navigator(time_obj, "start", "localDate");
                String start_local_time = shared.general_json_navigator(time_obj, "start", "localTime");
                buffer_arr.add(start_local_date);
                buffer_arr.add(start_local_time);
                break;
            case "images":
                JsonArray img_obj = header_element.getAsJsonArray();
                String img_url = img_obj.size() > 0 ? shared.general_json_navigator(img_obj.get(0).getAsJsonObject(), "url") : null;
                buffer_arr.add(img_url);
                break;
            case "name":
            case "id":
                String name = header_element.getAsString().trim();
                buffer_arr.add(name);
                break;
            case "classifications":
                JsonArray genre_obj = header_element.getAsJsonArray();
                String genre = genre_obj.size() > 0 ? shared.general_json_navigator(genre_obj.get(0).getAsJsonObject(), "segment", "name") : null;
                buffer_arr.add(genre);
                break;
            case "_embedded":
                JsonObject venues_obj = header_element.getAsJsonObject();
                if (venues_obj.has("venues")) {
                    JsonArray venues_arr = venues_obj.getAsJsonArray("venues");
                    JsonObject target_venue = venues_arr.size() > 0 ? venues_arr.get(0).getAsJsonObject() : null;
                    String venue = shared.general_json_navigator(target_venue, "name");
                    buffer_arr.add(venue);
                } else {
                    buffer_arr.add("");
                }
                break;
            default:
                break;
        }
    }

    /* Helper Functions */
    private String preprocess_google_geoLoc_address(String location){
        Pattern regGeoLoc = Pattern.compile("\\s*$");
        Pattern regNonAlphanumeric = Pattern.compile("[^a-zA-Z\\d+]+");
        Matcher matcher = regGeoLoc.matcher(location);
        String location_temp = matcher.replaceAll("");
        return regNonAlphanumeric.matcher(location_temp).replaceAll("+");
    }
    private String get_googleMap_api_key() {
        if (cached_googleMap_api_key != null) {
            return cached_googleMap_api_key;
        }

        Context context = getContext();
        PackageManager pm = context != null ? context.getPackageManager() : null;
        String packageName = requireActivity().getPackageName();
        ApplicationInfo ai;
        try {
            ai = pm != null ? pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA) : null;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        assert ai != null;
        Bundle bundle = ai.metaData;
        String apiKey = bundle.getString("com.google.android.geo.API_KEY");
        cached_googleMap_api_key = apiKey;
        return apiKey;
    }


    // Sort event results in ascending dataTime, and convert to AM/PM format
    private static void sort_by_dateTime(ArrayList<ArrayList<String>> list_for_table) {
        String dateTime_format = "yyyy-MM-dd HH:mm:ss";
        Locale locale = Locale.US;

        Comparator<ArrayList<String>> custom_date_comparator = (a, b) -> {
            try {
                Date dateA = new SimpleDateFormat(dateTime_format, locale).parse(a.get(0) + " " + a.get(1));
                Date dateB = new SimpleDateFormat(dateTime_format, locale).parse(b.get(0) + " " + b.get(1));
                return dateA != null ? dateA.compareTo(dateB) : 0;
            } catch (ParseException e) {
                return 0;
            }
        };

        list_for_table.sort(custom_date_comparator);

        DateFormat desired_format = new SimpleDateFormat("h:mm a", locale);

        for (ArrayList<String> date : list_for_table) {
            String formatted_date = "";
            try {
                Date parsed_date = new SimpleDateFormat(dateTime_format, locale).parse(date.get(0) + " " + date.get(1));
                formatted_date = parsed_date != null ? desired_format.format(parsed_date) : "";
            } catch (ParseException ignored) {

            }
            date.set(1, formatted_date);
        }
    }

}