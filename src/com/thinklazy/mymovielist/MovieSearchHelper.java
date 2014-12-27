package com.thinklazy.mymovielist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

class MovieSearchHelper extends AsyncTask<String, Integer, String> {

    private static final String TAG = null;

    protected String doInBackground(String... queries) {
	if (queries != null && queries.length > 0) {
	    String query = queries[0];
	    String result = getData(query);
	    return result;
	}
	return null;
    }

    @Override
    protected void onPostExecute(String result) {
	super.onPostExecute(result);
    }

    private String addParamsToUrl(String url, String movieName) {
	if (!url.endsWith("?"))
	    url += "?";

	List<NameValuePair> params = new LinkedList<NameValuePair>();

	params.add(new BasicNameValuePair("t", movieName));
	params.add(new BasicNameValuePair("plot", "short"));
	params.add(new BasicNameValuePair("r", "json"));

	String paramString = URLEncodedUtils.format(params, "utf-8");

	url += paramString;
	return url;
    }

    public String getData(String query) {
	URL url;
	try {
	    // url = new
	    // URL("http://www.omdbapi.com/?t=%+lagaan%&y=&plot=short&r=json");
	    query.replace(" ", "+");
	    url = new URL(
		    addParamsToUrl("http://www.omdbapi.com/", "%+"+ query + "%"));

	    InputStream in = url.openStream();
	    InputStreamReader is = new InputStreamReader(in);
	    BufferedReader reader = new BufferedReader(is);
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
		while ((line = reader.readLine()) != null) {
		    sb.append(line + "\n");
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		try {
		    is.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    Log.d(TAG, sb.toString());
	    return sb.toString();

	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}