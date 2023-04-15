package com.example.hw9.ui.main;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.SyncStateContract;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw9.AutoCompleteArrayAdapter;
import com.example.hw9.MySingleton;
import com.example.hw9.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
        // autocomplete suggestions http request
        get_autoComplete_suggestions(view);

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
                                                get_event_results(lat, lng, keyword, distance, category);
                                            }
                                        }
                                    }
                                }else {
                                    Log.d("GeoLoc status", "not result");
                                }

                            }, error -> {
                                // Handle the error
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
        final ProgressBar progressBar = view.findViewById(R.id.ac_progressBar);


        keyword_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do something before the text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do something during the text change
                progressBar.setVisibility(View.VISIBLE);
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
                                    progressBar.setVisibility(View.GONE);
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
                                progressBar.setVisibility(View.GONE);
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
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
    }

    private void get_event_results(String lat, String lng, String keyword, String distance, String category){
        String backend_url = "https://csci571-hw8-spr23.wl.r.appspot.com/search/event-search";
        Uri.Builder builder = Uri.parse(backend_url).buildUpon();
        builder.appendQueryParameter("lat", lat);
        builder.appendQueryParameter("lng", lng);
        builder.appendQueryParameter("keyword", keyword);
        builder.appendQueryParameter("distance", distance);
        builder.appendQueryParameter("category", category);
        String url = builder.build().toString();

        Log.d("url", url );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, resp -> {
                    ArrayList<ArrayList<String>> listForTable = new ArrayList<>();

                    Gson gson = new Gson();
                    JsonObject gsonResponse = gson.fromJson(resp.toString(), JsonObject.class);
                    JsonArray events = gsonResponse.getAsJsonObject("_embedded").getAsJsonArray("events");
                    int totalEvents = events.size();

                    for (int i = 0; i < totalEvents; i++) {
                        JsonObject respObj = events.get(i).getAsJsonObject();
                        ArrayList<String> bufferArray = new ArrayList<>();

                        if (respObj.has("dates")) {
                            bufferArrayAppend(bufferArray, "dates", respObj);
                        } else {
                            bufferArray.add("");
                            bufferArray.add("");
                        }

                        if (respObj.has("images")) {
                            bufferArrayAppend(bufferArray, "images", respObj);
                        } else {
                            bufferArray.add("");
                        }

                        if (respObj.has("name")) {
                            bufferArrayAppend(bufferArray, "name", respObj);
                        } else {
                            bufferArray.add("");
                        }

                        if (respObj.has("classifications")) {
                            bufferArrayAppend(bufferArray, "classifications", respObj);
                        } else {
                            bufferArray.add("");
                        }

                        if (respObj.has("_embedded")) {
                            bufferArrayAppend(bufferArray, "_embedded", respObj);
                        } else {
                            bufferArray.add("");
                        }

                        if (respObj.has("id")) {
                            bufferArrayAppend(bufferArray, "id", respObj);
                        } else {
                            bufferArray.add("");
                        }

                        listForTable.add(bufferArray);
                    }
                    Log.d("table", listForTable.toString());

                }, error -> {
                    // Handle the error
                    Log.e("Error", "Volley Error Ticketmaster Event Result: " + error.getMessage());
                });
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void bufferArrayAppend(ArrayList<String> bufferArray, String header, JsonObject respObj) {
        JsonElement headerElement = respObj.get(header);

        if (headerElement == null) {
            return;
        }

        switch (header) {
            case "dates":
                JsonObject timeObj = ((JsonElement) headerElement).getAsJsonObject();
                String startLocalDate = getTrimmedValue(timeObj, "start", "localDate");
                String startLocalTime = getTrimmedValue(timeObj, "start", "localTime");
                bufferArray.add(startLocalDate != null ? startLocalDate : "");
                bufferArray.add(startLocalTime != null ? startLocalTime : "");
                break;
            case "images":
                JsonArray imageObj = headerElement.getAsJsonArray();
                String imageUrl = imageObj.size() > 0 ? getTrimmedValue(imageObj.get(0).getAsJsonObject(), "url") : null;
                bufferArray.add(imageUrl != null ? imageUrl : "");
                break;
            case "name":
                String name = headerElement.getAsString().trim();
                bufferArray.add(!name.isEmpty() ? name : "");
                break;
            case "classifications":
                JsonArray genreObj = headerElement.getAsJsonArray();
                String genre = genreObj.size() > 0 ? getTrimmedValue(genreObj.get(0).getAsJsonObject(), "segment", "name") : null;
                bufferArray.add(genre != null ? genre : "");
                break;
            case "_embedded":
                JsonObject venuesObj = headerElement.getAsJsonObject();
                if (venuesObj.has("venues")) {
                    JsonArray venuesArray = venuesObj.getAsJsonArray("venues");
                    JsonObject firstVenue = venuesArray.size() > 0 ? venuesArray.get(0).getAsJsonObject() : null;
                    String venue = getTrimmedValue(firstVenue, "name");
                    bufferArray.add(venue != null ? venue : "");
                } else {
                    bufferArray.add("");
                }
                break;
            case "id":
                String id = headerElement.getAsString().trim();
                bufferArray.add(!id.isEmpty() ? id : "");
                break;
            default:
                break;
        }
    }

    // Extracts data from Json navigating through its hierarchy using an array of keys in a specific order
    private String getTrimmedValue(JsonObject jsonObject, String... keys) {
        JsonObject currentObj = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            JsonElement element = currentObj.get(keys[i]);
            if (element == null || !element.isJsonObject()) {
                return null;
            }
            currentObj = element.getAsJsonObject();
        }
        JsonElement finalElement = currentObj.get(keys[keys.length - 1]);
        if (finalElement == null) {
            return null;
        }
        return finalElement.getAsString().trim();
    }


    /* Helper Functions */
    private String preprocess_google_geoLoc_address(String location){
        Pattern regGeoLoc = Pattern.compile("\\s*$");
        Pattern regNonAlphanumeric = Pattern.compile("[^a-zA-Z\\d+]+");
        Matcher matcher = regGeoLoc.matcher(location);
        String location_temp = matcher.replaceAll("");
        String preprocessed_location = regNonAlphanumeric.matcher(location_temp).replaceAll("+");
        return preprocessed_location;
    }
    private String get_googleMap_api_key() {
        if (cached_googleMap_api_key != null) {
            return cached_googleMap_api_key;
        }

        Context context = getContext();
        PackageManager pm = context.getPackageManager();
        String packageName = getActivity().getPackageName();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bundle bundle = ai.metaData;
        String apiKey = bundle.getString("com.google.android.geo.API_KEY");
        cached_googleMap_api_key = apiKey;
        return apiKey;
    }

}