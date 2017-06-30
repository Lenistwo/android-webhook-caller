package calltrack.sample.myapplication.Server;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by IWS_Kulvinder on 6/22/2015.
 */
public class PrefStore {
	Context ctx;
	String Tag = "calltracking";
	SharedPreferences prefs;

	public PrefStore(Context ctx) {
		this.ctx = ctx;
		prefs = ctx.getSharedPreferences(Tag, Context.MODE_PRIVATE);
	}

	public void saveString(String key, String value) {

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public String getString(String key) {
		return prefs.getString(key, "");
	}

	public void saveBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, false);
	}

	public void saveLong(String key, long value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getLong(String key) {
		return prefs.getLong(key, 01);
	}

	public boolean contains(String key) {
		if (prefs.contains(key))
			return true;
		else
			return false;
	}

}